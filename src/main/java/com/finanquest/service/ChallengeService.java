package com.finanquest.service;

import com.finanquest.dto.ChallengeRequestDTO;
import com.finanquest.entity.Challenge;
import com.finanquest.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page; // Importar
import org.springframework.data.domain.Pageable; // Importar
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public Challenge createChallenge(ChallengeRequestDTO dto) {
        Challenge challenge = Challenge.builder()
                .nome(dto.nome())
                .descriptions(dto.descriptions())
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .rewardExperiencePoints(dto.rewardExperiencePoints())
                .build();

        return challengeRepository.save(challenge);
    }

    public Page<Challenge> getAllChallenges(Pageable pageable) {
        return challengeRepository.findAll(pageable);
    }
}