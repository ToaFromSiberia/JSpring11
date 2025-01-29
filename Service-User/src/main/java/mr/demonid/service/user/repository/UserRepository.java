package mr.demonid.service.user.repository;

import mr.demonid.service.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u JOIN FETCH u.accounts JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findUserById(Long id);
    /**
     * Возвращает данные пользователя.
     * @param username Имя пользователя.
     */
    @Query("SELECT u FROM User u JOIN FETCH u.accounts JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u JOIN FETCH u.accounts JOIN FETCH u.roles")
    List<User> findAllUsers();

    /**
     * Постраничная выборка данных из БД.
     */
    @Query("SELECT u FROM User u JOIN FETCH u.accounts JOIN FETCH u.roles")
    List<User> findAllUsersWithLimit(Pageable pageable);

    /**
     * Проверяет наличие пользователя в БД по его имени.
     * @param username Имя пользователя.
     */
    boolean existsByUsername(String username);

    boolean existsUserByEmail(String email);

}
