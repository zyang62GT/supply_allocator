package com.apple.allocator.repository;

import com.apple.allocator.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigInteger;

public interface PlanRepository extends JpaRepository<Plan, Integer> {
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "INSERT INTO plan (quantity, site, customer, product, date) VALUES (?1, ?2, ?3, ?4, ?5)",
            nativeQuery = true)
    void insertPlanEntry(BigInteger quantity, String site, String customer,
                                                  String product, java.sql.Date date);
}
