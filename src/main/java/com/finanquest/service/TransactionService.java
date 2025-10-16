package com.finanquest.service;

import com.finanquest.dto.TransactionRequestDTO;
import com.finanquest.entity.Transaction;
import com.finanquest.entity.User;
import com.finanquest.exception.ResourceNotFoundException;
import com.finanquest.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final GamificationService gamificationService;

    @Transactional
    public Transaction createTransaction(TransactionRequestDTO transactionDTO, Long userId) {
        Optional<User> user = userService.findById(userId);

        Transaction transaction = new Transaction();
        transaction.setDescriptions(transactionDTO.description());
        transaction.setAmount(transactionDTO.amount());
        transaction.setType(transactionDTO.type());
        transaction.setDate(transactionDTO.date());
        transaction.setUser(user);

        Transaction savedTransaction = transactionRepository.save(transaction);

        // Após salvar a transação, delega a lógica de gamificação para o serviço responsável.
        gamificationService.processNewTransaction(savedTransaction);

        return savedTransaction;
    }


    public List<Transaction> findTransactionsByUserId(Long userId) {
        // A verificação da existência do utilizador já é tratada dentro do userService.findById()
        userService.findById(userId);
        return transactionRepository.findByUserId(userId);
    }

    @Transactional
    public Transaction updateTransaction(Long transactionId, TransactionRequestDTO transactionDTO) {
        Optional<Transaction> optionalTransaction = transactionRepository.findById(transactionId);

        if (optionalTransaction.isEmpty()) {
            System.out.println("⚠️ Transação não encontrada com o id: " + transactionId);
            return null; // ou apenas retorne null temporariamente
        }

        Transaction existingTransaction = optionalTransaction.get();
        existingTransaction.setDescriptions(transactionDTO.description());
        existingTransaction.setAmount(transactionDTO.amount());
        existingTransaction.setType(transactionDTO.type());
        existingTransaction.setDate(transactionDTO.date());

        return transactionRepository.save(existingTransaction);
    }

    @Transactional
    public void deleteTransaction(Long transactionId) {
        if (!transactionRepository.existsById(transactionId)) {
            System.out.println("⚠️ Transação não encontrada com o id: " + transactionId);
            return; // apenas sai do método, sem lançar erro
        }

        transactionRepository.deleteById(transactionId);
    }

}
