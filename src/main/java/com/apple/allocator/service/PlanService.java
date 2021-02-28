package com.apple.allocator.service;

import com.apple.allocator.helper.CSVHelper;
import com.apple.allocator.model.DemandOrder;
import com.apple.allocator.model.Plan;
import com.apple.allocator.model.Supply;
import com.apple.allocator.model.UnsatisfiedOrder;
import com.apple.allocator.repository.PlanRepository;
import com.apple.allocator.repository.UnsatisfiedOrderRepository;
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

    public List<UnsatisfiedOrder> getUnsatisfiedOrders() {
        return unsatisfiedOrders;
    }

    public void setUnsatisfiedOrders(List<UnsatisfiedOrder> unsatisfiedOrders) {
        this.unsatisfiedOrders = unsatisfiedOrders;
    }

    private List<UnsatisfiedOrder> unsatisfiedOrders = new ArrayList<>();
    private String lastCustomer;
    private int lastOrderId;

    @Autowired
    SourcingRuleService sourcingRuleService;

    @Autowired
    DemandOrderService demandOrderService;

    @Autowired
    SupplyService supplyService;

    @Autowired
    UnsatisfiedOrderRepository unsatisfiedOrderRepository;


    public void allocate() {
        int sameIdCount = 0;
        while (demandOrderService.getNonZeroDemandOrderSize() > 0 && supplyService.getNonZeroSupplySize() > 0) {
            Iterable<DemandOrder> oldestOrders = demandOrderService.getOldestDemandOrders();
            Iterator orderIter = oldestOrders.iterator();
            while (orderIter.hasNext()) {
                DemandOrder demandOrder = (DemandOrder) orderIter.next();
                String currentCustomer = demandOrder.getCustomer();
                String currentProduct = demandOrder.getProduct();
                java.sql.Date currentDate = demandOrder.getDate();
                BigInteger currentQuantity = demandOrder.getQuantity();
                int orderId = demandOrderService.getIdByQuantityCustomerProductAndDate(currentQuantity,currentCustomer,
                        currentProduct,currentDate);
                // if same order 3 times in a row, push the customer's date back by 3 days
                if (sameIdCount > 2) {
                    sameIdCount = 0;
                    java.sql.Date nextDate = new java.sql.Date(currentDate.getTime() + 3*24*60*60*1000);
                    demandOrderService.updateDemandOrderDateById(nextDate, orderId);
                    break;
                }
                if (orderId == lastOrderId) {
                    sameIdCount++;
                } else {
                    sameIdCount = 0;
                }
                lastOrderId = orderId;
                // switch customer when this order is from the same customer of the last order, unless all latest orders are from this customer;
                if (currentCustomer.equals(lastCustomer) && ! orderIter.hasNext()) {
                    lastCustomer = "";
                    continue;
                }
                if (currentCustomer.equals(lastCustomer)) {
                    continue;
                }
                Iterable<String> sites = sourcingRuleService.findSitesByCustomerAndProduct(currentCustomer,
                        currentProduct);
                Iterator siteIter = sites.iterator();
                while (siteIter.hasNext()) {
                    String site = (String) siteIter.next();
                    Iterable<Supply> supplies = supplyService.getOldestSupplyBySiteAndProduct(site, currentProduct);
                    //unsatisfied orders are inserted to unsatisfied_order table
                    if (supplies == null && ! siteIter.hasNext()) {
                        unsatisfiedOrders.add(new UnsatisfiedOrder(currentCustomer, currentProduct,
                                currentDate, currentQuantity));
                        demandOrderService.updateDemandOrderQuantityById(BigInteger.ZERO, orderId);
                        break;
                    }

                    if (supplies == null) {
                        continue;
                    }
                    for (Supply supply : supplies) {
                        if (supply.getQuantity().equals(BigInteger.ZERO)) {
                            continue;
                        }
                        if (currentQuantity.equals(BigInteger.ZERO)) {
                            break;
                        }
                        int supplyId = supplyService.getIdByQuantitySiteProductAndDate(supply.getQuantity(), supply.getSite(),
                                supply.getProduct(), supply.getDate());
                        if (currentQuantity.compareTo(supply.getQuantity()) > 0) {
                            currentQuantity = currentQuantity.subtract(supply.getQuantity());
                            // if customer quantity > supply quantity
                            // customer quantity = customer quantity - supply quantity
                            // add new plan where quantity = supply quantity, and supply quantity becomes 0
                            supplyService.updateSupplyQuantityById(BigInteger.ZERO, supplyId);
                            demandOrderService.updateDemandOrderQuantityById(currentQuantity, orderId);
                            plans.add(new Plan(site, currentCustomer, currentProduct, supply.getDate(), supply.getQuantity()));
                            supply.setQuantity(BigInteger.ZERO);
                            lastCustomer = currentCustomer;
                        } else {
                            BigInteger newSupplyQuantity = supply.getQuantity().subtract(currentQuantity);
                            // if customer quantity <= supply quantity
                            // supply quantity = supply quantity - customer quantity
                            // add new plan where quantity = customer quantity, and customer quantity becomes 0
                            demandOrderService.updateDemandOrderQuantityById(BigInteger.ZERO, orderId);
                            supplyService.updateSupplyQuantityById(newSupplyQuantity, supplyId);
                            plans.add(new Plan(site, currentCustomer, currentProduct, supply.getDate(), currentQuantity));
                            currentQuantity = BigInteger.ZERO;
                            supply.setQuantity(newSupplyQuantity);
                            lastCustomer = currentCustomer;
                        }
                    }
                }
            }
        }
        planRepository.saveAll(plans);
        unsatisfiedOrderRepository.saveAll(unsatisfiedOrders);
    }

    public void insertPlanEntry(BigInteger quantity, String site, String customer,
                         String product, java.sql.Date date){
        planRepository.insertPlanEntry(quantity, site, customer, product, date);
    }

    public ByteArrayInputStream load() {
        List<Plan> plans = planRepository.findAllByOrderByDateAsc();

        ByteArrayInputStream in = CSVHelper.plansToCSV(plans);
        return in;
    }
}
