package mr.demonid.web.client.controllers;

import feign.FeignException;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import mr.demonid.web.client.dto.ProductInfo;
import mr.demonid.web.client.dto.UserInfo;
import mr.demonid.web.client.links.UserServiceClient;
import mr.demonid.web.client.service.CatalogService;
import mr.demonid.web.client.service.OrderService;
import mr.demonid.web.client.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@AllArgsConstructor
public class AppController {

    private final UserService userService;
    private CatalogService catalogService;
    private OrderService orderService;

    @GetMapping
    public String baseDir() {
        System.out.println("redirect to index...");
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index(HttpSession session, Model model) {

        Long id = (Long) session.getAttribute("userId");    // смотрим, есть ли в сессии данные о текущем пользователе?
        if (id == null) {
            List<UserInfo> users = userService.getAllUsers();
            Optional<UserInfo> user = users.stream().filter(u -> u.getId() > 1).findFirst();
            id = user.isPresent() ? user.get().getId() : 0;
        } else {
            id = userService.getUserById(id).getId();       // нормализуем id
            System.out.println("session id: " + id);
        }
        return "redirect:/set-user?userId=" + id;
    }

    /**
     * Покупка товара
     */
    @PostMapping("/order")
    public String placeOrder(@RequestParam("productId") Long productId,
                             @RequestParam("name") String name,
                             @RequestParam("quantity") int quantity,
                             @RequestParam("price") BigDecimal price,
                             @RequestParam("userId") Long userId,
                             Model model)
    {
        System.out.println("Product ID: " + productId);
        System.out.println("Name: " + name);
        System.out.println("Quantity: " + quantity);
        System.out.println("Price: " + price);
        // костыль :)
        if (userId <= 1) {                  // магазин или гость?
            model.addAttribute("errorMessage", "Данный пользователь не может выполнять покупки");
            return "/error-order";
        }
        // открываем заказ
        try {
            UUID uuid = orderService.addOrder(productId, userId, quantity, price);
            model.addAttribute("productName", name);
            model.addAttribute("quantity", quantity);
            model.addAttribute("totalCost", price.multiply(BigDecimal.valueOf(quantity)));
            System.out.println("Покупка совершена!");
            return "/confirmed";
        } catch (FeignException e) {
            System.out.println("Облом!");
            model.addAttribute("errorMessage", e.contentUTF8());
            return "/error-order";
        }

    }

    @GetMapping("/set-user")
    public String setUser(HttpSession session, Model model, @RequestParam("userId") Long id) {

        List<ProductInfo> products = catalogService.getProducts();
        model.addAttribute("products", products);

        List<UserInfo> users = userService.getAllUsers();
        UserInfo currentUser = userService.getUserById(id);
        model.addAttribute("users", users);
        model.addAttribute("currentUser", currentUser);

        session.setAttribute("userId", currentUser.getId());        // сохраняем в сессию id текущего пользователя

        return "/home";
    }
}
