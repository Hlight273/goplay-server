package com.github.goplay.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.sql.Timestamp;

public class Room {

  @TableId(type = IdType.AUTO)
  private Integer id;
  private String roomName;
  private Integer ownerId;
  private Integer maxUsers;
  private Integer currentUsers;
  private String roomCode;
  private java.sql.Timestamp createdAt;
  private Integer isActive;

  public Room(Integer id, String roomName, Integer ownerId, Integer maxUsers, Integer currentUsers, String roomCode, Timestamp createdAt, Integer isActive) {
    this.id = id;
    this.roomName = roomName;
    this.ownerId = ownerId;
    this.maxUsers = maxUsers;
    this.currentUsers = currentUsers;
    this.roomCode = roomCode;
    this.createdAt = createdAt;
    this.isActive = isActive;
  }

  public Room() {
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }


  public String getRoomName() {
    return roomName;
  }

  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }


  public Integer getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Integer ownerId) {
    this.ownerId = ownerId;
  }


  public Integer getMaxUsers() {
    return maxUsers;
  }

  public void setMaxUsers(Integer maxUsers) {
    this.maxUsers = maxUsers;
  }


  public Integer getCurrentUsers() {
    return currentUsers;
  }

  public void setCurrentUsers(Integer currentUsers) {
    this.currentUsers = currentUsers;
  }


  public String getRoomCode() {
    return roomCode;
  }

  public void setRoomCode(String roomCode) {
    this.roomCode = roomCode;
  }


  public java.sql.Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(java.sql.Timestamp createdAt) {
    this.createdAt = createdAt;
  }


  public Integer getIsActive() {
    return isActive;
  }

  public void setIsActive(Integer isActive) {
    this.isActive = isActive;
  }

}
