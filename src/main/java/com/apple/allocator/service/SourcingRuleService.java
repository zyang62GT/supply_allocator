package com.apple.allocator.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import com.apple.allocator.helper.CSVHelper;
import com.apple.allocator.model.SourcingRule;
import com.apple.allocator.repository.SourcingRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SourcingRuleService {

    @Autowired
    private SourcingRuleRepository sourcingRuleRepository;

    public void save(MultipartFile file) {
        try {
            List<SourcingRule> sourcingRules = CSVHelper.csvToSourcingRules(file.getInputStream());
            sourcingRuleRepository.saveAll(sourcingRules);
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }


    public Iterable<String> findSitesByCustomerAndProduct(String customer, String product){
        return sourcingRuleRepository.findSitesByCustomerAndProduct(customer, product);
    }

    public List<SourcingRule> getAllSourcingRules() {
        return sourcingRuleRepository.findAll();
    }
}

