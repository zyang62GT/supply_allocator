package com.apple.allocator.repository;

import com.apple.allocator.model.Supply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.math.BigInteger;

public interface SupplyRepository extends JpaRepository<Supply, Integer> {

    @Query(value="select * from supply where site = ?1 and product = ?2 and quantity != 0 and date = ?3 ;",
            nativeQuery = true)
    Iterable<Supply> getSupplyBySiteAndProductAndDate(String site, String product, java.sql.Date date);

    @Query(value = "select * from supply where site = ?1 and product = ?2 and quantity != 0 order by date limit 1",
            nativeQuery = true)
    Supply findSupplyTopDateBySiteProduct(String site, String product);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update supply set quantity = ?1 where site = ?2 and product = ?3 and date = ?4 ;",
    nativeQuery = true)
    void updateSupplyQuantityBySiteProductAndDate(BigInteger quantity, String site,
                                                     String product, java.sql.Date date);

    @Query(value = "select count(*) from supply where quantity != 0",
            nativeQuery = true)
    Integer getNonZeroSupplySize();

    @Query(value = "select id from supply where quantity = ?1 and site = ?2 and product = ?3 and date = ?4 limit 1",
            nativeQuery = true)
    Iterable<Integer> getIdByQuantitySiteProductAndDate(BigInteger quantity, String site,
                                                            String product, java.sql.Date date);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update supply set quantity = ?1 where id = ?2",
            nativeQuery = true)
    void updateSupplyQuantityById(BigInteger quantity, Integer id);
}
