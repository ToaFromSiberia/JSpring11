package mr.demonid.service.user.services;

import lombok.AllArgsConstructor;
import mr.demonid.service.user.domain.Account;
import mr.demonid.service.user.domain.User;
import mr.demonid.service.user.dto.PaymentRequest;
import mr.demonid.service.user.exceptions.BadAccountException;
import mr.demonid.service.user.exceptions.NotEnoughAmountException;
import mr.demonid.service.user.exceptions.NotFoundException;
import mr.demonid.service.user.exceptions.UserException;
import mr.demonid.service.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@AllArgsConstructor
@Service
public class TransferService {

    private UserRepository userRepository;


    @Transactional
    public void transfer(PaymentRequest request) throws UserException {
        User userFrom = userRepository.findById(request.getFromUserId()).orElseThrow(NotFoundException::new);
        User userTo = userRepository.findById(request.getRecipientId()).orElseThrow(NotFoundException::new);

        // проверяем возможность операции
        BigDecimal amount = request.getTransferAmount();
        Account accountFrom = userFrom.getPaymentAccount();
        Account accountTo = userTo.getPaymentAccount();
        if (accountFrom == null || accountTo == null) {
            throw new BadAccountException();
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.compareTo(accountFrom.getAmount()) > 0) {
            throw new NotEnoughAmountException();
        }
        // обновляем и сохраняем данные
        accountFrom.setAmount(accountFrom.getAmount().subtract(amount));
        accountTo.setAmount(accountTo.getAmount().add(amount));
        userRepository.save(userTo);
        userRepository.save(userFrom);
//        storeHistory(request);
    }
}


//    private void storeHistory(User from, User to, BigDecimal amount) {
//        TransactionOperation op = new TransactionOperation();
//        op.setFromUserId(from.getId());
//        op.setRecipientId(to.getId());
//        op.setTransferAmount(amount);
//        historyRepository.save(op);
//    }
