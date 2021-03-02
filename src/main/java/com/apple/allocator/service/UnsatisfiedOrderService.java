package com.apple.allocator.service;

import com.apple.allocator.model.UnsatisfiedOrder;
import com.apple.allocator.repository.UnsatisfiedOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UnsatisfiedOrderService {

    @Autowired
    UnsatisfiedOrderRepository unsatisfiedOrderRepository;

    public List<UnsatisfiedOrder> getUnsatisfiedOrders() {
        return unsatisfiedOrders;
    }

    public void setUnsatisfiedOrders(List<UnsatisfiedOrder> unsatisfiedOrders) {
        this.unsatisfiedOrders = unsatisfiedOrders;
    }

    private List<UnsatisfiedOrder> unsatisfiedOrders = new ArrayList<>();

    public List<UnsatisfiedOrder> getAllUnsatisfiedOrders() {
        return unsatisfiedOrderRepository.findAll();
    }
}
