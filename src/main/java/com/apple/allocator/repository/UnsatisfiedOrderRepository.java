package com.apple.allocator.repository;

import com.apple.allocator.model.UnsatisfiedOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnsatisfiedOrderRepository extends JpaRepository<UnsatisfiedOrder, Integer> {

}
