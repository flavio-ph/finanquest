package com.finanquest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalResponseDTO(
        Long id,
        String name,
        BigDecimal currentAmount,
        BigDecimal targetAmount,
        LocalDate deadline,
        String status,
        int progressPercentage
) {}