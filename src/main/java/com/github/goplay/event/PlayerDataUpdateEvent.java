package com.github.goplay.event;

import com.github.goplay.dto.PlayerData;
import org.springframework.context.ApplicationEvent;

public class PlayerDataUpdateEvent extends ApplicationEvent {
    private final PlayerData playerData;

    public PlayerDataUpdateEvent(Object source, PlayerData playerData) {
        super(source);
        this.playerData = playerData;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }
}
