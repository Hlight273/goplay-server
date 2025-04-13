package com.github.goplay.cache;

import com.github.goplay.dto.PlayerData;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlayerOperationCache {
    private Map<String, OperationRecord> operationRecords = new ConcurrentHashMap<>();


    private static class OperationRecord {
        private String operationType;  // play/pause
        private Integer songIndex;
        private Long timestamp;

        public OperationRecord(String operationType, Integer songIndex, Long timestamp) {
            this.operationType = operationType;
            this.songIndex = songIndex;
            this.timestamp = timestamp;
        }

        public OperationRecord() {
        }

        public String getOperationType() {
            return operationType;
        }

        public void setOperationType(String operationType) {
            this.operationType = operationType;
        }

        public Integer getSongIndex() {
            return songIndex;
        }

        public void setSongIndex(Integer songIndex) {
            this.songIndex = songIndex;
        }

        public Long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Long timestamp) {
            this.timestamp = timestamp;
        }
    }



    public void setOperationRecords(Map<String, OperationRecord> operationRecords) {
        this.operationRecords = operationRecords;
    }

    public boolean isOperationAllowed(Integer roomId, Integer userId, PlayerData playerData) {
        String key = roomId + "_" + userId;
        OperationRecord lastRecord = operationRecords.get(key);
        long currentTime = System.currentTimeMillis();

        // 检查是否是相同操作
        if (lastRecord != null
                && lastRecord.getOperationType().equals(playerData.isPaused() ? "pause" : "play")
                && lastRecord.getSongIndex().equals(playerData.getIndex())
                && currentTime - lastRecord.getTimestamp() < 1000) {
            return false;
        }

        // 更新最新操作记录
        operationRecords.put(key, new OperationRecord(
                playerData.isPaused() ? "pause" : "play",
                playerData.getIndex(),
                currentTime
        ));
        return true;
    }
}