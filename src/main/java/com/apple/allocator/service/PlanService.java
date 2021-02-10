package com.apple.allocator.service;

import com.apple.allocator.helper.CSVHelper;
import com.apple.allocator.model.DemandOrder;
import com.apple.allocator.model.Plan;
import com.apple.allocator.model.Supply;
import com.apple.allocator.repository.PlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class PlanService {
    @Autowired
    PlanRepository planRepository;

    public List<Plan> getPlans() {
        return plans;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }

    private List<Plan> plans = new ArrayList<>();
    private String lastCustomer;

    @Autowired
    SourcingRuleService sourcingRuleService;

    @Autowired
    DemandOrderService demandOrderService;

    @Autowired
    SupplyService supplyService;


    public void allocate() {
        int count = 0;
        while (demandOrderService.getNonZeroDemandOrderSize() > 0) {
            System.out.println("demandOrderService.getNonZeroDemandOrderSize(): " + demandOrderService.getNonZeroDemandOrderSize());
            Iterable<DemandOrder> oldestOrders = demandOrderService.getOldestDemandOrders();

            for (DemandOrder demandOrder : oldestOrders) {

                String currentCustomer = demandOrder.getCustomer();
                String currentProduct = demandOrder.getProduct();
                java.sql.Date currentDate = demandOrder.getDate();
                BigInteger currentQuantity = demandOrder.getQuantity();
                if (currentCustomer.equals(lastCustomer)) {
                    count++;
                }
                if (count > 1) {
                    // if same customer 3 times in a row, push the customer's date back by one day
                    count = 0;
                    java.sql.Date nextDate = new java.sql.Date(currentDate.getTime() + 24*60*60*1000);
                    demandOrderService.updateDemandOrderDateBySiteProductAndQuantity(nextDate, currentCustomer,
                            currentProduct,currentQuantity);
                    continue;
                }

                Iterable<String> sites = sourcingRuleService.findSitesByCustomerAndProduct(currentCustomer,
                        currentProduct);
                Iterator iter = sites.iterator();
                while (iter.hasNext()) {
                    String site = (String) iter.next();
                    Iterable<Supply> supplies = supplyService.getOldestSupplyBySiteAndProduct(site, currentProduct);
                    if (supplies == null && ! iter.hasNext()) {
                        return;
                    }
                    if (supplies == null) {
                        continue;
                    }
                    for (Supply supply : supplies) {
                        if (currentQuantity.compareTo(BigInteger.ZERO) == 0) {
                            continue;
                        } else if (currentQuantity.compareTo(supply.getQuantity()) > 0) {
                            System.out.println("currentQuantity: " + currentQuantity + ", supply.getQuantity: " + supply.getQuantity());
                            currentQuantity = currentQuantity.subtract(supply.getQuantity());
                            // if customer quantity > supply quantity
                            // customer quantity = customer quantity - supply quantity
                            // add new plan where quantity = supply quantity, and supply quantity becomes 0
                            supplyService.updateSupplyQuantityBySiteProductAndDate(BigInteger.ZERO, site, currentProduct,
                                    supply.getDate());
                            System.out.println("Supply quantity = 0 at site: " + site + ", product: " + currentProduct + ", date: " + supply.getDate());
                            demandOrderService.updateDemandOrderQuantityBySiteProductAndDate(currentQuantity,
                                    currentCustomer, currentProduct, currentDate);
                            System.out.println("demand order quantity = " + currentQuantity + " customer: " + currentCustomer + ", product: " + currentProduct + ", date: " + currentDate);
                            plans.add(new Plan(site, currentCustomer, currentProduct, supply.getDate(), supply.getQuantity()));
                            lastCustomer = currentCustomer;
                        } else {
                            System.out.println("currentQuantity: " + currentQuantity + ", supply.getQuantity: " + supply.getQuantity());
                            BigInteger newSupplyQuantity = supply.getQuantity().subtract(currentQuantity);
                            // if customer quantity <= supply quantity
                            // supply quantity = supply quantity - customer quantity
                            // add new plan where quantity = customer quantity, and customer quantity becomes 0
                            demandOrderService.updateDemandOrderQuantityBySiteProductAndDate(BigInteger.ZERO,
                                    currentCustomer, currentProduct, currentDate);
                            System.out.println("demand order quantity = 0,  customer: " + currentCustomer + ", product: " + currentProduct + ", date: " + currentDate);
                            supplyService.updateSupplyQuantityBySiteProductAndDate(newSupplyQuantity, site, currentProduct,
                                    supply.getDate());
                            System.out.println("Supply quantity = " + newSupplyQuantity + " at site: " + site + ", product: " + currentProduct + ", date: " + supply.getDate());
                            plans.add(new Plan(site, currentCustomer, currentProduct, supply.getDate(), currentQuantity));
                            currentQuantity = BigInteger.ZERO;
                            lastCustomer = currentCustomer;
                        }
                    }
                }
            }
        }
    }

    public void insertPlanEntry(BigInteger quantity, String site, String customer,
                         String product, java.sql.Date date){
        planRepository.insertPlanEntry(quantity, site, customer, product, date);
    }

    public ByteArrayInputStream load() {
        List<Plan> plans = planRepository.findAll();

        ByteArrayInputStream in = CSVHelper.plansToCSV(plans);
        return in;
    }
}
