package com.finanquest.repository;

import com.finanquest.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Altera de List<Transaction> para Page<Transaction> e recebe Pageable
    Page<Transaction> findByUserId(Long userId, Pageable pageable);
}
