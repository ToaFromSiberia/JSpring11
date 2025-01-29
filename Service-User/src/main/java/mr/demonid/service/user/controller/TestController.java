package mr.demonid.service.user.controller;

import lombok.AllArgsConstructor;
import mr.demonid.service.user.dto.RegistrationRequest;
import mr.demonid.service.user.services.TestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/user/create")
public class TestController {

    private TestService testService;

    /**
     * Сервис регистрирует нового пользователя, автоматически добавляя ему счет.
     */
    @PostMapping("/user")
    public ResponseEntity<String> createUser(@RequestBody RegistrationRequest request) {
        try {
            testService.registerUser(request);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка регистрации: " + e.getMessage());
        }
    }

    /**
     * Сервис создаёт все возможные роли.
     * @return
     */
    @GetMapping("/roles")
    public ResponseEntity<String> createRole() {
        try {
            testService.createRoles("USER", "ADMIN", "DEVELOPER", "PRODUCER");
            return ResponseEntity.ok("Role created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка создания ролей: " + e.getMessage());
        }
    }

}
