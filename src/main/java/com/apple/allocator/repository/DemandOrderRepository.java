package com.apple.allocator.repository;

import com.apple.allocator.model.DemandOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.List;

public interface DemandOrderRepository extends JpaRepository<DemandOrder, Integer> {

    @Query(value = "select * from demand_order where quantity != 0 order by date limit 1",
            nativeQuery = true)
    Iterable<DemandOrder> findDemandOrderTopDate();

    @Query(value = "select * from demand_order where date = ?1 and quantity != 0",
            nativeQuery = true)
    Iterable<DemandOrder> findDemandOrdersByDate(String date);

    @Query(value = "select count(*) from demand_order where quantity != 0",
            nativeQuery = true)
    Integer getNonZeroDemandOrderSize();

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update demand_order set quantity = ?1 where customer = ?2 and product = ?3 and date = ?4",
            nativeQuery = true)
    void updateDemandOrderQuantityBySiteProductAndDate(BigInteger quantity, String customer,
                                                  String product, java.sql.Date date);





}
