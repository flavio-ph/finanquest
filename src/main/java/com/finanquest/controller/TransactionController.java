package com.finanquest.controller;

import com.finanquest.dto.TransactionRequestDTO;
import com.finanquest.dto.TransactionResponseDTO;
import com.finanquest.entity.Transaction;
import com.finanquest.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transactions") // Rota limpa!
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @AuthenticationPrincipal UserDetails userDetails, // O Spring injeta o utilizador aqui
            @Valid @RequestBody TransactionRequestDTO transactionDTO) {

        // Passamos o email do token para o serviço
        Transaction createdTransaction = transactionService.createTransaction(transactionDTO, userDetails.getUsername());

        return new ResponseEntity<>(mapToResponseDTO(createdTransaction), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getMyTransactions(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<Transaction> transactions = transactionService.findTransactionsByUserEmail(userDetails.getUsername());

        List<TransactionResponseDTO> responseDTOs = transactions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionResponseDTO> updateTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long transactionId,
            @Valid @RequestBody TransactionRequestDTO transactionDTO) {

        Transaction updatedTransaction = transactionService.updateTransaction(transactionId, transactionDTO, userDetails.getUsername());
        return ResponseEntity.ok(mapToResponseDTO(updatedTransaction));
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long transactionId) {

        transactionService.deleteTransaction(transactionId, userDetails.getUsername());
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