package com.apple.allocator.model;

import javax.persistence.*;

@Entity
public class SourcingRule {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public SourcingRule () {

    }

    public SourcingRule (String site, String customer, String product) {
        this.site = site;
        this.customer = customer;
        this.product = product;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "site")
    private String site;

    @Column(name = "customer")
    private String customer;

    @Column(name = "product")
    private String product;
}
