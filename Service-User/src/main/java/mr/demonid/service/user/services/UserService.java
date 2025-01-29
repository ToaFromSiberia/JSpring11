package mr.demonid.service.user.services;

import lombok.AllArgsConstructor;
import mr.demonid.service.user.domain.Account;
import mr.demonid.service.user.domain.User;
import mr.demonid.service.user.exceptions.NotFoundException;
import mr.demonid.service.user.exceptions.UserException;
import mr.demonid.service.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    public Account getUserAccount(Long userId) throws UserException {
        Optional<User> userOptional = userRepository.findUserById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException();
        }
        User user = userOptional.get();
        // по дефолту активным будет первый счет
        Account account = user.getPaymentAccount();
        if (account == null || account.getId() == null) {
            throw new NotFoundException();
        }
        return account;
    }

    public List<User> getAllUsers() throws UserException {
        return userRepository.findAllUsers();
    }

    public User getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findUserById(userId);
        if (userOptional.isEmpty()) {
            throw new NotFoundException();
        }
        return userOptional.get();
    }
}
