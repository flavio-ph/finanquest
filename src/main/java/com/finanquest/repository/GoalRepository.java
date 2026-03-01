package com.finanquest.repository;

import com.finanquest.entity.Goal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    Page<Goal> findByUserId(Long userId, Pageable pageable);
}