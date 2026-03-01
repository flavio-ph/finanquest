package com.finanquest.controller;

import com.finanquest.dto.ChallengeRequestDTO;
import com.finanquest.entity.Challenge;
import com.finanquest.service.ChallengeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page; // Importar
import org.springframework.data.domain.Pageable; // Importar
import org.springframework.data.domain.Sort; // Importar
import org.springframework.data.web.PageableDefault; // Importar
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/challenges")
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @PostMapping
    public ResponseEntity<Challenge> createChallenge(@Valid @RequestBody ChallengeRequestDTO dto) {
        return new ResponseEntity<>(challengeService.createChallenge(dto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<Challenge>> getAllChallenges(
            @PageableDefault(page = 0, size = 10, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(challengeService.getAllChallenges(pageable));
    }
}