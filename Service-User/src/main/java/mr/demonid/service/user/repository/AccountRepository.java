package mr.demonid.service.user.repository;

import mr.demonid.service.user.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findAccountByName(String name);

    Boolean existsAccountByName(String name);
}
