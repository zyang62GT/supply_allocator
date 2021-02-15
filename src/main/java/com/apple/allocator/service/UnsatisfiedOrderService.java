package com.apple.allocator.service;

import com.apple.allocator.model.UnsatisfiedOrder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UnsatisfiedOrderService {

    public List<UnsatisfiedOrder> getUnsatisfiedOrders() {
        return unsatisfiedOrders;
    }

    public void setUnsatisfiedOrders(List<UnsatisfiedOrder> unsatisfiedOrders) {
        this.unsatisfiedOrders = unsatisfiedOrders;
    }

    private List<UnsatisfiedOrder> unsatisfiedOrders = new ArrayList<>();
}
