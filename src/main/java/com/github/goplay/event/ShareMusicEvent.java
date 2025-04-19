package com.github.goplay.event;

import com.github.goplay.dto.newDTO.MusicShareMessage;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class ShareMusicEvent extends ApplicationEvent {
    private final MusicShareMessage shareMessage;
    private final String type;

    public ShareMusicEvent(Object source, MusicShareMessage shareMessage, String type) {
        super(source);
        this.shareMessage = shareMessage;
        this.type = type;
    }

    public ShareMusicEvent(Object source, Clock clock, MusicShareMessage shareMessage, String type) {
        super(source, clock);
        this.shareMessage = shareMessage;
        this.type = type;
    }

    public MusicShareMessage getShareMessage() {
        return shareMessage;
    }

    public String getType() {
        return type;
    }
}
