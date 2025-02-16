package com.github.goplay.event;

import org.springframework.context.ApplicationEvent;

public class RoomUpdateEvent extends ApplicationEvent {
    private final Integer roomId;
    private final String type;  // "userList" 或 "songList"

    public RoomUpdateEvent(Object source, Integer roomId, String type) {
        super(source);
        this.roomId = roomId;
        this.type = type;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public String getType() {
        return type;
    }
}
