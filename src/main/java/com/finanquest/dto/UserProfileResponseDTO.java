package com.finanquest.dto;

import com.finanquest.entity.User;

public record UserProfileResponseDTO(
        Long id,
        String name,
        String email,
        int level,
        long experiencePoints
) {


}
