package com.finanquest.service;

import com.finanquest.dto.TransactionRequestDTO;
import com.finanquest.entity.Transaction;
import com.finanquest.entity.User;
import com.finanquest.exception.ResourceNotFoundException;
import com.finanquest.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
        gamificationService.processNewTransaction(savedTransaction);

        return savedTransaction;
    }

    public List<Transaction> findTransactionsByUserId(Long userId) {
        userService.findById(userId);
        return transactionRepository.findByUserId(userId);
    }

    @Transactional
    public Transaction updateTransaction(Long transactionId, TransactionRequestDTO transactionDTO, Long userId) {
        Transaction existingTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

        // Verificação de Segurança
        if (!existingTransaction.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Você não tem permissão para alterar esta transação.");
        }

        existingTransaction.setDescriptions(transactionDTO.description());
        existingTransaction.setAmount(transactionDTO.amount());
        existingTransaction.setType(transactionDTO.type());
        existingTransaction.setDate(transactionDTO.date());

        return transactionRepository.save(existingTransaction);
    }

    @Transactional
    public void deleteTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

        // Verificação de Segurança
        if (!transaction.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Você não tem permissão para remover esta transação.");
        }

        transactionRepository.delete(transaction);
    }
}