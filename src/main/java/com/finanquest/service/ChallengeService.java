package com.finanquest.service;

import com.finanquest.dto.ChallengeRequestDTO;
import com.finanquest.entity.Challenge;
import com.finanquest.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }
}