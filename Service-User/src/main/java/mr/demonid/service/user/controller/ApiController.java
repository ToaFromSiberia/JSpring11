package mr.demonid.service.user.controller;

import lombok.AllArgsConstructor;
import mr.demonid.service.user.domain.Account;
import mr.demonid.service.user.domain.Role;
import mr.demonid.service.user.domain.User;
import mr.demonid.service.user.dto.PaymentRequest;
import mr.demonid.service.user.dto.UserInfo;
import mr.demonid.service.user.dto.UserPayInfo;
import mr.demonid.service.user.services.RoleService;
import mr.demonid.service.user.services.TransferService;
import mr.demonid.service.user.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/user")
public class ApiController {

    private UserService userService;
    private RoleService roleService;
    private TransferService transferService;


    /**
     * Запрос баланса счета пользователя.
     * @param id Пользователь
     */
    @GetMapping("/account/{id}")
    public ResponseEntity<UserPayInfo> getAccount(@PathVariable Long id){
        Account account = userService.getUserAccount(id);
        return ResponseEntity.ok(new UserPayInfo(id, account.getId(), account.getAmount()));
    }

    /**
     * Перевод средств с одного счета на другой.
     * @param payInfo Информация, от кого, кому и сколько перевести
     */
    @PostMapping("account/transaction")
    public ResponseEntity<Void> transaction(@RequestBody PaymentRequest payInfo) {
        transferService.transfer(payInfo);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/get-all-users")
    public ResponseEntity<List<UserInfo>> getAllUsersInfo(){
        try {
            List<User> users = userService.getAllUsers();
            List<UserInfo> res = users.stream().map(e -> new UserInfo(e.getId(), e.getUsername(), e.getPaymentAccount().getAmount())).toList();
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ArrayList<>());
        }
    }

    @GetMapping("get-user/{id}")
    public ResponseEntity<UserInfo> getUser(@PathVariable Long id){
        User user = userService.getUserById(id);
        return ResponseEntity.ok(new UserInfo(user.getId(), user.getUsername(), user.getPaymentAccount().getAmount()));
    }

    /**
     * Тестовый эндпоинт.
     */
    @GetMapping("/get-all")
    public ResponseEntity<List<User>> getAll(){
        try {
            System.out.println(userService.getAllUsers());
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.ok().body(new ArrayList<>());
        }
    }

    @GetMapping("/get-roles")
    public ResponseEntity<List<Role>> getRoles(){
        List<Role> roles;
        try {
            roles = roleService.getAllRoles();
            System.out.println("roles: " + roles);
        } catch (Exception e) {
            roles = new ArrayList<>();
        }
        return ResponseEntity.ok(roles);
    }
}

