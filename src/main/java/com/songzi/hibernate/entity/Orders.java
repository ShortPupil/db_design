package com.songzi.hibernate.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class Orders {
    private long id;
    private Timestamp orderTime;
    private Integer orderType;
    private Timestamp cancelTime;
    private Integer cancelType;

    private com.songzi.hibernate.entity.Package packageByPid;
    private Customer customerByCid;

    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "order_time")
    public Timestamp getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Timestamp orderTime) {
        this.orderTime = orderTime;
    }

    @Basic
    @Column(name = "order_type")
    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    @Basic
    @Column(name = "cancel_time")
    public Timestamp getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Timestamp cancelTime) {
        this.cancelTime = cancelTime;
    }

    @Basic
    @Column(name = "cancel_type")
    public Integer getCancelType() {
        return cancelType;
    }

    public void setCancelType(Integer cancelType) {
        this.cancelType = cancelType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Orders orders = (Orders) o;

        if (id != orders.id) return false;
        if (orderTime != null ? !orderTime.equals(orders.orderTime) : orders.orderTime != null) return false;
        if (orderType != null ? !orderType.equals(orders.orderType) : orders.orderType != null) return false;
        if (cancelTime != null ? !cancelTime.equals(orders.cancelTime) : orders.cancelTime != null) return false;
        if (cancelType != null ? !cancelType.equals(orders.cancelType) : orders.cancelType != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (orderTime != null ? orderTime.hashCode() : 0);
        result = 31 * result + (orderType != null ? orderType.hashCode() : 0);
        result = 31 * result + (cancelTime != null ? cancelTime.hashCode() : 0);
        result = 31 * result + (cancelType != null ? cancelType.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "cid", referencedColumnName = "id")
    public Customer getCustomerByCid() {
        return customerByCid;
    }

    public void setCustomerByCid(Customer customerByCId) {
        this.customerByCid = customerByCId;
    }

    @ManyToOne
    @JoinColumn(name = "pid", referencedColumnName = "id")
    public com.songzi.hibernate.entity.Package getPackageByPid() {
        return (com.songzi.hibernate.entity.Package)packageByPid;
    }

    public void setPackageByPid(com.songzi.hibernate.entity.Package packageByPid) {
        this.packageByPid = packageByPid;
    }
}
