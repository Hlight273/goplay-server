package com.github.goplay.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RoomCodeUtils {
    private static final String BASE62_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int ROOM_CODE_LENGTH = 10;

    public static String generateRoomCode(int userId, long timestamp) {
        // 生成一个种子字符串
        String seed = userId + "-" + timestamp;
        String hash = generateHash(seed); // 生成哈希值
        return createRoomCodeFromHash(hash); // 从哈希值生成房间代码
    }

    private static String generateHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0'); // 在单字符前面加零
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not generate hash", e);
        }
    }

    private static String createRoomCodeFromHash(String hash) {
        StringBuilder roomCode = new StringBuilder(ROOM_CODE_LENGTH);

        // 从哈希值中生成房间代码
        for (int i = 0; i < ROOM_CODE_LENGTH; i++) {
            // 取哈希中的一部分创建一个有效的索引
            int index = Math.abs(hash.charAt(i) % BASE62_CHARACTERS.length());
            roomCode.append(BASE62_CHARACTERS.charAt(index));
        }

        return roomCode.toString();
    }
}