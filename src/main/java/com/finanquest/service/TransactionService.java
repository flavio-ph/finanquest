package com.finanquest.service;

import com.finanquest.dto.TransactionRequestDTO;
import com.finanquest.entity.Transaction;
import com.finanquest.entity.User;
import com.finanquest.exception.ResourceNotFoundException;
import com.finanquest.repository.TransactionRepository;
import com.finanquest.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final GamificationService gamificationService;
    private final UserService userService;

    @Transactional
    public Transaction createTransaction(TransactionRequestDTO transactionDTO, String userEmail) {
        // 1. Buscamos o utilizador real (User)
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado com email: " + userEmail));

        Transaction transaction = new Transaction();
        transaction.setDescriptions(transactionDTO.description());
        transaction.setAmount(transactionDTO.amount());
        transaction.setType(transactionDTO.type());
        transaction.setDate(transactionDTO.date());

        // 2. Associamos o utilizador à transação
        transaction.setUser(user);

        Transaction savedTransaction = transactionRepository.save(transaction);

        // 3. Processamos a gamificação
        gamificationService.processNewTransaction(savedTransaction);

        return savedTransaction;
    }

    // CORREÇÃO: Adicionado o parâmetro 'Pageable pageable'
    public Page<Transaction> findTransactionsByUserEmail(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado."));

        // Repassamos o pageable para o repositório
        return transactionRepository.findByUserId(user.getId(), pageable);
    }

    @Transactional
    public Transaction updateTransaction(Long transactionId, TransactionRequestDTO transactionDTO, String userEmail) {
        Transaction existingTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transação não encontrada"));

        // Verificação de Segurança
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

        // Verificação de Segurança
        if (!transaction.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Você não tem permissão para remover esta transação.");
        }

        transactionRepository.delete(transaction);
    }
}