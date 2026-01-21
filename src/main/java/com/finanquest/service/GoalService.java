package com.finanquest.service;

import com.finanquest.dto.GoalRequestDTO;
import com.finanquest.entity.Goal;
import com.finanquest.entity.User;
import com.finanquest.exception.ResourceNotFoundException;
import com.finanquest.repository.GoalRepository;
import com.finanquest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public Goal createGoal(GoalRequestDTO dto, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Goal goal = Goal.builder()
                .name(dto.name())
                .targetAmount(dto.targetAmount())
                .currentAmount(dto.currentAmount() != null ? dto.currentAmount() : BigDecimal.ZERO)
                .deadline(dto.deadline())
                .status(Goal.GoalStatus.IN_PROGRESS)
                .user(user)
                .build();

        return goalRepository.save(goal);
    }

    public List<Goal> getMyGoals(String userEmail) {
        return goalRepository.findByUserEmail(userEmail);
    }

    public void deleteGoal(Long id) {
        goalRepository.deleteById(id);
    }

    // Método para adicionar valor à meta (depósito)
    public Goal addAmount(Long goalId, BigDecimal amount) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Meta não encontrada"));

        goal.setCurrentAmount(goal.getCurrentAmount().add(amount));

        // Verifica se completou
        if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(Goal.GoalStatus.COMPLETED);
        }

        return goalRepository.save(goal);
    }
}