package com.finanquest.controller;

import com.finanquest.dto.TransactionRequestDTO;
import com.finanquest.dto.TransactionResponseDTO;
import com.finanquest.entity.Transaction;
import com.finanquest.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/{userId}/transactions")
@RequiredArgsConstructor
public class TrasansactionController {
    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @PathVariable Long userId,
            @Valid @RequestBody TransactionRequestDTO transactionDTO) {

        Transaction createdTransaction = transactionService.createTransaction(transactionDTO, userId);
        TransactionResponseDTO responseDTO = mapToResponseDTO(createdTransaction);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByUser(@PathVariable Long userId) {
        List<Transaction> transactions = transactionService.findTransactionsByUserId(userId);

        List<TransactionResponseDTO> responseDTOs = transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @PathVariable Long userId,
            @PathVariable Long transactionId,
            @Valid @RequestBody TransactionRequestDTO transactionDTO) {
        // No futuro, a lógica de segurança verificará se o 'userId' tem permissão para alterar a 'transactionId'.
        Transaction updatedTransaction = transactionService.updateTransaction(transactionId, transactionDTO);
        return ResponseEntity.ok(mapToResponseDTO(updatedTransaction));
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long userId,
            @PathVariable Long transactionId) {
        transactionService.deleteTransaction(transactionId);
        return ResponseEntity.noContent().build();
    }

    private TransactionResponseDTO mapToResponseDTO(Transaction transaction) {
        return new TransactionResponseDTO(
                transaction.getId(),
                transaction.getDescriptions(),
                transaction.getAmount(),
                transaction.getType(),
                transaction.getDate()
        );
    }

}
