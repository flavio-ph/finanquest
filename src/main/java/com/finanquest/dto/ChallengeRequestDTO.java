package com.finanquest.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record ChallengeRequestDTO(
        @NotBlank String nome,
        @NotBlank String descriptions,
        LocalDate startDate,
        LocalDate endDate,
        long rewardExperiencePoints
) {}