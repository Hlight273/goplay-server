package com.github.goplay.entity;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.sql.Timestamp;

public class UserVip {
    private Integer id;

    private Integer vipLevel;

    private Timestamp startDate;

    private Timestamp endDate;

    @TableField(fill = FieldFill.INSERT)
    private Timestamp createdAt; // 记录创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Timestamp updatedAt; // 记录最后更新时间

    public UserVip(Integer id, Integer vipLevel, Timestamp startDate, Timestamp endDate) {
        this.id = id;
        this.vipLevel = vipLevel;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UserVip(Integer id, Integer vipLevel, Timestamp startDate, int validDays) {
        this.id = id;
        this.vipLevel = vipLevel;
        this.startDate = startDate;
        // 计算 endDate：有效天数 * 每天的毫秒数 加上 startDate 的时间戳
        long endTimeMillis = startDate.getTime() + validDays * 24 * 60 * 60 * 1000L;
        this.endDate = new Timestamp(endTimeMillis);
    }

    public UserVip() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(Integer vipLevel) {
        this.vipLevel = vipLevel;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
}
