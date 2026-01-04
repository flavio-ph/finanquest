package com.finanquest.service;

import com.finanquest.dto.TransactionRequestDTO;
import com.finanquest.entity.Transaction;
import com.finanquest.entity.User;
import com.finanquest.exception.ResourceNotFoundException;
import com.finanquest.repository.TransactionRepository;
import com.finanquest.repository.UserRepository; // Importar UserRepository
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
    private final UserRepository userRepository; // Injetamos o repositório diretamente
    private final GamificationService gamificationService;
    private final UserService userService; // Podemos manter para métodos auxiliares se necessário

    @Transactional
    public Transaction createTransaction(TransactionRequestDTO transactionDTO, String userEmail) {
        // 1. Buscamos o utilizador real (User), não um Optional
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado com email: " + userEmail));

        Transaction transaction = new Transaction();
        transaction.setDescriptions(transactionDTO.description());
        transaction.setAmount(transactionDTO.amount());
        transaction.setType(transactionDTO.type());
        transaction.setDate(transactionDTO.date());

        // 2. Agora passamos o objeto User real.
        // Como apagou o método errado na entidade, o Lombok vai usar o correto.
        transaction.setUser(user);

        Transaction savedTransaction = transactionRepository.save(transaction);
        gamificationService.processNewTransaction(savedTransaction);

        return savedTransaction;
    }

    public List<Transaction> findTransactionsByUserEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado."));

        return transactionRepository.findByUserId(user.getId());
    }

    @Transactional
    public Transaction updateTransaction(Long transactionId, TransactionRequestDTO transactionDTO, String userEmail) {
        Transaction existingTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

        // Validação de Segurança: O email do dono da transação bate com o email do token?
        if (!existingTransaction.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Você não tem permissão para alterar esta transação.");
        }

        existingTransaction.setDescriptions(transactionDTO.description());
        existingTransaction.setAmount(transactionDTO.amount());
        existingTransaction.setType(transactionDTO.type());
        existingTransaction.setDate(transactionDTO.date());

        return transactionRepository.save(existingTransaction);
    }

    @Transactional
    public void deleteTransaction(Long transactionId, String userEmail) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

        if (!transaction.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Você não tem permissão para remover esta transação.");
        }

        transactionRepository.delete(transaction);
    }
}