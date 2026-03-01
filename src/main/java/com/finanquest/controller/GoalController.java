package com.finanquest.controller;

import com.finanquest.dto.GoalRequestDTO;
import com.finanquest.dto.GoalResponseDTO;
import com.finanquest.entity.Goal;
import com.finanquest.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

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

    // MELHORIA DE PERFORMANCE: Paginação implementada
    @GetMapping
    public ResponseEntity<Page<GoalResponseDTO>> getMyGoals(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(page = 0, size = 10, sort = "deadline", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<Goal> goals = goalService.findAllGoals(userDetails.getUsername(), pageable);

        // Converte a Page de Entity para Page de DTO
        Page<GoalResponseDTO> response = goals.map(this::mapToResponseDTO);

        return ResponseEntity.ok(response);
    }

    // MELHORIA DE SEGURANÇA: Passamos o userDetails para validar posse
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        goalService.deleteGoal(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // MELHORIA DE SEGURANÇA: Passamos o userDetails para validar posse
    @PutMapping("/{id}/deposit")
    public ResponseEntity<GoalResponseDTO> deposit(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> payload) {

        BigDecimal amount = payload.get("amount");

        // Passamos o email para garantir que o dono está a depositar
        Goal updatedGoal = goalService.addAmount(id, amount, userDetails.getUsername());

        return ResponseEntity.ok(mapToResponseDTO(updatedGoal));
    }

    private GoalResponseDTO mapToResponseDTO(Goal goal) {
        int progress = 0;
        // Evita divisão por zero
        if (goal.getTargetAmount() != null && goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
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
                Math.min(progress, 100) // Garante que não passa de 100%
        );
    }
}