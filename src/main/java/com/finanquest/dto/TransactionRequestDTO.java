package com.finanquest.dto;

import com.finanquest.entity.Transaction;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequestDTO(
        @NotBlank(message = "A descrição não pode estar em branco.")
        String description,

        @NotNull(message = "O valor não pode ser nulo.")
        @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
        BigDecimal amount,

        @NotNull(message = "O tipo da transação não pode ser nulo.")
        Transaction.TransactionType type,

        @NotNull(message = "A data não pode ser nula.")
        @PastOrPresent(message = "A data não pode ser no futuro.")
        LocalDate date
) {}
