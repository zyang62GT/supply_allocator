package com.apple.allocator.service;

import com.apple.allocator.helper.CSVHelper;
import com.apple.allocator.model.DemandOrder;
import com.apple.allocator.repository.DemandOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Date;
import java.util.Iterator;
import java.util.List;

@Service
public class DemandOrderService {
    @Autowired
    private DemandOrderRepository demandOrderRepository;

    public void save(MultipartFile file) {
        try {
            List<DemandOrder> demandOrders = CSVHelper.csvToDemandOrders(file.getInputStream());
            demandOrderRepository.saveAll(demandOrders);
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

    public Iterable<DemandOrder> getOldestDemandOrders() {
        Iterable<DemandOrder> oldest = demandOrderRepository.findDemandOrderTopDate();
        java.sql.Date date = null;
        for (DemandOrder demandOrder : oldest) {
            date = demandOrder.getDate();
        }
        Iterable<DemandOrder> oldestOrders = demandOrderRepository.findDemandOrdersByDate(date.toString());
        return oldestOrders;
    }

    public void updateDemandOrderQuantityBySiteProductAndDate(BigInteger quantity, String customer,
                                                       String product, java.sql.Date date){
        demandOrderRepository.updateDemandOrderQuantityBySiteProductAndDate(quantity, customer, product, date);
    }

    public void updateDemandOrderDateBySiteProductAndQuantity(java.sql.Date date, String customer,
                                                              String product, BigInteger quantity){
        demandOrderRepository.updateDemandOrderDateBySiteProductAndQuantity(date, customer, product, quantity);
    }

    public int getNonZeroDemandOrderSize() {
        return demandOrderRepository.getNonZeroDemandOrderSize();
    }

    public int getIdByQuantityCustomerProductAndDate(BigInteger quantity, String customer,
                                                            String product, java.sql.Date date){
        Iterable<Integer> ids = demandOrderRepository.getIdByQuantityCustomerProductAndDate(quantity, customer,
                product, date);
        Iterator<Integer> iter = ids.iterator();
        return iter.next();
    }

    public void updateDemandOrderQuantityById(BigInteger quantity, Integer id){
        demandOrderRepository.updateDemandOrderQuantityById(quantity, id);
    }

    public void updateDemandOrderDateById(java.sql.Date date, Integer id){
        demandOrderRepository.updateDemandOrderDateById(date, id);
    }

    public List<DemandOrder> getAllDemandOrders() {
        return demandOrderRepository.findAll();
    }
}
