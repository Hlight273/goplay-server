package com.github.goplay.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.goplay.utils.PrivilegeCode;

import java.sql.Timestamp;

public class RoomUser {

  @TableId(type = IdType.AUTO)
  private Integer id;
  private Integer roomId;
  private Integer userId;
  private java.sql.Timestamp joinedAt;
  private Integer isActive;
  private Integer privilege;

  public RoomUser(Integer id, Integer roomId, Integer userId, Timestamp joinedAt, Integer isActive, Integer privilege) {
    this.id = id;
    this.roomId = roomId;
    this.userId = userId;
    this.joinedAt = joinedAt;
    this.isActive = isActive;
    this.privilege = privilege;
  }

  public RoomUser() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getRoomId() {
    return roomId;
  }

  public void setRoomId(Integer roomId) {
    this.roomId = roomId;
  }


  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }


  public java.sql.Timestamp getJoinedAt() {
    return joinedAt;
  }

  public void setJoinedAt(java.sql.Timestamp joinedAt) {
    this.joinedAt = joinedAt;
  }


  public Integer getIsActive() {
    return isActive;
  }

  public void setIsActive(Integer isActive) {
    this.isActive = isActive;
  }

  public Integer getPrivilege() {
    return privilege;
  }

  public void setPrivilege(Integer privilege) {
    this.privilege = privilege;
  }

  public void setPrivilegeRoomOwner(){
    setPrivilege(PrivilegeCode.ROOM_OWNER);
  }
  public void setPrivilegeAdmin(){
    setPrivilege(PrivilegeCode.ADMIN);
  }
  public void setPrivilegeMember(){
    setPrivilege(PrivilegeCode.MEMBER);
  }
}
