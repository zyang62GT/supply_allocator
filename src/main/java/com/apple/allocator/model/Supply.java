package com.apple.allocator.model;

import javax.persistence.*;
import java.math.BigInteger;
import java.sql.Date;

@Entity
public class Supply {
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    @Column(name = "site")
    private String site;

    @Column(name = "product")
    private String product;

    @Column(name = "date")
    private java.sql.Date date;

    @Column(name = "quantity")
    private BigInteger quantity;

    public Supply (String site, String product, java.sql.Date date, BigInteger quantity) {
        this.site = site;
        this.product = product;
        this.date = date;
        this.quantity = quantity;
    }

    public Supply () {

    }
}
