package com.finanquest.dto;

public record AuthResponseDTO(
        String token,
        UserProfileResponseDTO user
) {

}
