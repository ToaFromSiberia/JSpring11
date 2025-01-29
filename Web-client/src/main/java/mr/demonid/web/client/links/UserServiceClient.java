package mr.demonid.web.client.links;

import mr.demonid.web.client.dto.ProductInfo;
import mr.demonid.web.client.dto.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "USER-SERVICE", url = "http://localhost:8090/USER-SERVICE")      // имя сервиса, под которым он зарегистрирован в Eureka
public interface UserServiceClient {

    @GetMapping("/api/user/get-all-users")
    ResponseEntity<List<UserInfo>> getAllUsers();

    @GetMapping("/api/user/get-user/{id}")
    ResponseEntity<UserInfo> getUserById(@PathVariable Long id);

}


