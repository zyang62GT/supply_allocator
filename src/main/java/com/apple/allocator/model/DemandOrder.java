package com.apple.allocator.model;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Date;

@Entity
public class DemandOrder {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "customer")
    private String customer;

    @Column(name = "product")
    private String product;

    @Column(name = "date")
    private java.sql.Date date;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigInteger getQuantity() {
        return quantity;
    }

    public void setQuantity(BigInteger quantity) {
        this.quantity = quantity;
    }

    @Column(name = "quantity")
    private BigInteger quantity;

    public DemandOrder () {

    }

    public DemandOrder (String customer, String product, java.sql.Date date, BigInteger quantity) {
        this.customer = customer;
        this.product = product;
        this.date = date;
        this.quantity = quantity;
    }
}
