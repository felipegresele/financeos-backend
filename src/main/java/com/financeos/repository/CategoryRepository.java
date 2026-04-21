package com.financeos.repository;

import com.financeos.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    List<Category> findByUserIdOrIsDefaultTrue(String userId);
    List<Category> findByUserId(String userId);
    Optional<Category> findByIdAndUserId(String id, String userId);
    boolean existsByNameAndUserId(String name, String userId);
}
