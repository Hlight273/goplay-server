package com.github.goplay.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("payment_order")
public class PaymentOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("prepay_id")
    private String prepayId;

    @TableField("user_id")
    private Integer userId;

    private BigDecimal amount;

    private Integer status;

    @TableField("expire_time")
    private LocalDateTime expireTime;

    @TableField("paid_amount")
    private BigDecimal paidAmount;

    @TableField("success_time")
    private LocalDateTime successTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    public PaymentOrder(Long id, String orderNo, String prepayId, Integer userId, BigDecimal amount, Integer status, LocalDateTime expireTime, BigDecimal paidAmount, LocalDateTime successTime, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.orderNo = orderNo;
        this.prepayId = prepayId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.expireTime = expireTime;
        this.paidAmount = paidAmount;
        this.successTime = successTime;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public PaymentOrder() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public LocalDateTime getSuccessTime() {
        return successTime;
    }

    public void setSuccessTime(LocalDateTime successTime) {
        this.successTime = successTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

//    public void setCreateTime(LocalDateTime createTime) {
//        this.createTime = createTime;
//    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

//    public void setUpdateTime(LocalDateTime updateTime) {
//        this.updateTime = updateTime;
//    }
}