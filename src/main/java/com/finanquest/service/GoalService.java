package com.finanquest.service;

import com.finanquest.dto.GoalRequestDTO;
import com.finanquest.entity.Goal;
import com.finanquest.entity.User;
import com.finanquest.exception.ResourceNotFoundException;
import com.finanquest.repository.GoalRepository;
import com.finanquest.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Transactional
    public Goal createGoal(GoalRequestDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Goal goal = Goal.builder()
                .name(dto.name())
                .targetAmount(dto.targetAmount())
                // Garante que não começa nulo
                .currentAmount(dto.currentAmount() != null ? dto.currentAmount() : BigDecimal.ZERO)
                .deadline(dto.deadline())
                .status(Goal.GoalStatus.IN_PROGRESS)
                .user(user)
                .build();

        return goalRepository.save(goal);
    }

    // MELHORIA DE PERFORMANCE: Agora retorna Page e recebe Pageable
    public Page<Goal> findAllGoals(String userEmail, Pageable pageable) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return goalRepository.findByUserId(user.getId(), pageable);
    }

    // MELHORIA DE SEGURANÇA: Recebe userEmail para garantir que o dono está deletando
    @Transactional
    public void deleteGoal(Long id, String userEmail) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        if (!goal.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Você não tem permissão para remover esta meta.");
        }

        goalRepository.delete(goal);
    }

    // MELHORIA DE SEGURANÇA: Recebe userEmail para validar a posse antes de adicionar saldo
    @Transactional
    public Goal addAmount(Long goalId, BigDecimal amount, String userEmail) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        if (!goal.getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Você não tem permissão para alterar esta meta.");
        }

        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));

        // Verifica se completou a meta
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(Goal.GoalStatus.COMPLETED);
        }

        return goalRepository.save(goal);
    }
}