package com.finanquest.repository;

import com.finanquest.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    List<Transaction> findByUserId(Long userid);

    List<Transaction> findByUserIdAndDateBetween(Integer userId, LocalDate startDate, LocalDate endDate);
}
