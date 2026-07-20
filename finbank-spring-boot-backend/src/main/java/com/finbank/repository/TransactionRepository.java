package com.finbank.repository;

import com.finbank.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionReference(String reference);

    @Query("SELECT t FROM Transaction t WHERE t.senderAccount = :acc OR t.receiverAccount = :acc ORDER BY t.createdAt DESC")
    Page<Transaction> findByAccountNumber(@Param("acc") String accountNumber, Pageable pageable);
}