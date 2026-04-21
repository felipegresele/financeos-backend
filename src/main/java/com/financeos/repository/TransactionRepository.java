package com.financeos.repository;

import com.financeos.entity.Transaction;
import com.financeos.entity.Transaction.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    Page<Transaction> findByUserIdOrderByDateDesc(String userId, Pageable pageable);

    @Query("""
        SELECT t FROM Transaction t
        WHERE t.user.id = :userId
          AND (:startDate IS NULL OR t.date >= :startDate)
          AND (:endDate IS NULL OR t.date <= :endDate)
          AND (:type IS NULL OR t.type = :type)
          AND (:categoryId IS NULL OR t.category.id = :categoryId)
          AND (:search IS NULL OR LOWER(t.description) LIKE LOWER(CONCAT('%', :search, '%')))
        ORDER BY t.date DESC
    """)
    Page<Transaction> findWithFilters(
        @Param("userId") String userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("type") TransactionType type,
        @Param("categoryId") String categoryId,
        @Param("search") String search,
        Pageable pageable
    );

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t
        WHERE t.user.id = :userId
          AND t.type = :type
          AND t.date BETWEEN :startDate AND :endDate
    """)
    BigDecimal sumByUserAndTypeAndDateBetween(
        @Param("userId") String userId,
        @Param("type") TransactionType type,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT t FROM Transaction t
        WHERE t.user.id = :userId
          AND t.date BETWEEN :startDate AND :endDate
        ORDER BY t.date DESC
    """)
    List<Transaction> findByUserAndDateRange(
        @Param("userId") String userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT t FROM Transaction t
        WHERE t.user.id = :userId
        ORDER BY t.createdAt DESC
        LIMIT 10
    """)
    List<Transaction> findTop10ByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);
}
