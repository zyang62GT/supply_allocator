package com.apple.allocator.repository;
import com.apple.allocator.model.SourcingRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface SourcingRuleRepository extends JpaRepository<SourcingRule, Integer> {

    @Query(value="select site from sourcing_rule where customer = ?1 and product = ?2",
            nativeQuery = true)
    Iterable<String> findSitesByCustomerAndProduct(String customer, String product);


}
