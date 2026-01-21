package com.finanquest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalRequestDTO(
        @NotBlank(message = "O nome da meta é obrigatório.")
        String name,

        @NotNull(message = "O valor alvo é obrigatório.")
        @DecimalMin(value = "0.01", message = "O valor alvo deve ser positivo.")
        BigDecimal targetAmount,

        BigDecimal currentAmount,

        @NotNull(message = "A data limite é obrigatória.")
        LocalDate deadline
) {}