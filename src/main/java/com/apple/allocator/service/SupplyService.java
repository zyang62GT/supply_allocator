package com.apple.allocator.service;

import com.apple.allocator.helper.CSVHelper;
import com.apple.allocator.model.SourcingRule;
import com.apple.allocator.model.Supply;
import com.apple.allocator.repository.SourcingRuleRepository;
import com.apple.allocator.repository.SupplyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

@Service
public class SupplyService {
    @Autowired
    private SupplyRepository supplyRepository;

    public void save(MultipartFile file) {
        try {
            List<Supply> supplies = CSVHelper.csvToSupplies(file.getInputStream());
            supplyRepository.saveAll(supplies);
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

    public Iterable<Supply> getSupplyBySiteAndProductAndDate (String site, String product, java.sql.Date date) {
        return supplyRepository.getSupplyBySiteAndProductAndDate(site, product, date);
    }

    public Iterable<Supply> getOldestSupplyBySiteAndProduct(String site, String product) {
        Supply oldest = supplyRepository.findSupplyTopDateBySiteProduct(site,product);
        if (oldest == null) {
            return null;
        }
        return supplyRepository.getSupplyBySiteAndProductAndDate(site, product, oldest.getDate());
    }

    public void updateSupplyQuantityBySiteProductAndDate(BigInteger quantity, String site,
                                                  String product, java.sql.Date date){
        supplyRepository.updateSupplyQuantityBySiteProductAndDate(quantity, site, product, date);
    };

    public int getNonZeroSupplySize(){
        return supplyRepository.getNonZeroSupplySize();
    }

    public int getIdByQuantitySiteProductAndDate(BigInteger quantity, String site,
                                                 String product, java.sql.Date date){
        Iterable<Integer> ids = supplyRepository.getIdByQuantitySiteProductAndDate(quantity, site, product, date);
        Iterator<Integer> iter = ids.iterator();
        return iter.next();
    }

    public void updateSupplyQuantityById(BigInteger quantity, Integer id){
        supplyRepository.updateSupplyQuantityById(quantity, id);
    }

    public List<Supply> getAllSupplies() {
        return supplyRepository.findAllByOrderByDateAsc();
    }
}
