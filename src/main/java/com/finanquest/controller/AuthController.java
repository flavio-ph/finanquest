package com.finanquest.controller;

import com.finanquest.dto.AuthResponseDTO;
import com.finanquest.dto.LoginRequestDTO;
import com.finanquest.dto.UserRegistrationDTO;
import com.finanquest.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO) {
        authService.register(userRegistrationDTO);
        return new ResponseEntity<>("Utilizador registrado com sucesso", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequestDTO) {

        try {
            // REMOVIDO: String.valueOf(...) que quebrava o JSON
            return ResponseEntity.ok(authService.login(loginRequestDTO));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
}
