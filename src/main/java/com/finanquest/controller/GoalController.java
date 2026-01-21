package com.finanquest.controller;

import com.finanquest.dto.GoalRequestDTO;
import com.finanquest.dto.GoalResponseDTO;
import com.finanquest.entity.Goal;
import com.finanquest.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<GoalResponseDTO> createGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody GoalRequestDTO dto) {

        Goal goal = goalService.createGoal(dto, userDetails.getUsername());
        return new ResponseEntity<>(mapToResponseDTO(goal), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GoalResponseDTO>> getMyGoals(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<Goal> goals = goalService.getMyGoals(userDetails.getUsername());
        List<GoalResponseDTO> response = goals.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }

    private GoalResponseDTO mapToResponseDTO(Goal goal) {
        int progress = 0;
        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
            progress = goal.getCurrentAmount()
                    .divide(goal.getTargetAmount(), 2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100))
                    .intValue();
        }

        return new GoalResponseDTO(
                goal.getId(),
                goal.getName(),
                goal.getCurrentAmount(),
                goal.getTargetAmount(),
                goal.getDeadline(),
                goal.getStatus().name(),
                Math.min(progress, 100)
        );
    }

    @PutMapping("/{id}/deposit")
    public ResponseEntity<GoalResponseDTO> deposit(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> payload) {

        BigDecimal amount = payload.get("amount");
        Goal updatedGoal = goalService.addAmount(id, amount);
        return ResponseEntity.ok(mapToResponseDTO(updatedGoal));
    }
}