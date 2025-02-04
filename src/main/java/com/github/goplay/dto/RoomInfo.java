package com.github.goplay.dto;

import com.github.goplay.entity.Song;
import com.github.goplay.entity.User;

import java.sql.Timestamp;
import java.util.List;

public class RoomInfo {
    private Integer id;
    private String roomName;
    private Integer ownerId;
    private Integer maxUsers;
    private Integer currentUsers;
    private String roomCode;
    private java.sql.Timestamp createdAt;
    private List<User> userList;
    private List<Song> songList;

    public RoomInfo() {
    }

    public RoomInfo(Integer id, String roomName, Integer ownerId, Integer maxUsers, Integer currentUsers, String roomCode, Timestamp createdAt, List<User> userList, List<Song> songList) {
        this.id = id;
        this.roomName = roomName;
        this.ownerId = ownerId;
        this.maxUsers = maxUsers;
        this.currentUsers = currentUsers;
        this.roomCode = roomCode;
        this.createdAt = createdAt;
        this.userList = userList;
        this.songList = songList;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }
}
