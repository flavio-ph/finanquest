package com.finanquest.controller;

import com.finanquest.dto.TransactionRequestDTO;
import com.finanquest.dto.TransactionResponseDTO;
import com.finanquest.entity.Transaction;
import com.finanquest.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<Page<TransactionResponseDTO>> getMyTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            // @PageableDefault define o padrão se o frontend não enviar nada
            @PageableDefault(page = 0, size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable
    ) {

        Page<Transaction> transactionPage = transactionService.findTransactionsByUserEmail(userDetails.getUsername(), pageable);

        Page<TransactionResponseDTO> responseDTOs = transactionPage.map(this::mapToResponseDTO);

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