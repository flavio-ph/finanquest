package com.finanquest.dto;

import com.finanquest.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponseDTO(
        Long id,
        String description,
        BigDecimal amount,
        Transaction.TransactionType type,
        LocalDate date
) {

}
