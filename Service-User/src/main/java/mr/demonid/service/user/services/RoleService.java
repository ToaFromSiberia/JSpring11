package mr.demonid.service.user.services;

import lombok.AllArgsConstructor;
import mr.demonid.service.user.domain.Role;
import mr.demonid.service.user.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class RoleService {

    private RoleRepository roleRepository;

    public Role getRole(String name) throws Exception {
        if (!name.startsWith("ROLE_")) {
            name = "ROLE_" + name;
        }
        Optional<Role> role = roleRepository.findRoleByName(name);
        if (role.isEmpty()) {
            throw new Exception("Такой роли не существует!");
        }
        return role.get();
    }


    public List<Role> getAllRoles() throws Exception {
        return roleRepository.findAll();
    }
}
