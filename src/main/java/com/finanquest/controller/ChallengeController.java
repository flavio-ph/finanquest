package com.finanquest.controller;

import com.finanquest.dto.ChallengeRequestDTO;
import com.finanquest.entity.Challenge;
import com.finanquest.service.ChallengeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @PostMapping
    public ResponseEntity<Challenge> createChallenge(@Valid @RequestBody ChallengeRequestDTO dto) {
        return new ResponseEntity<>(challengeService.createChallenge(dto), HttpStatus.CREATED);
    }

    // Listar todos os desafios disponíveis
    @GetMapping
    public ResponseEntity<List<Challenge>> getAllChallenges() {
        return ResponseEntity.ok(challengeService.getAllChallenges());
    }
}
