package com.github.goplay.utils.Data;

public class MBTICodec {
    // 将MBTI字符串编码为数字 (0-31)
    public static byte encodeMBTI(String mbtiType) {
        if (mbtiType == null || mbtiType.length() != 4) {
            throw new IllegalArgumentException("Invalid MBTI type");
        }

        int code = 0;
        code |= (mbtiType.charAt(0) == 'I' ? 1 : 0) << 4;  // E/I
        code |= (mbtiType.charAt(1) == 'S' ? 1 : 0) << 3;  // N/S
        code |= (mbtiType.charAt(2) == 'T' ? 1 : 0) << 2;  // F/T
        code |= (mbtiType.charAt(3) == 'J' ? 1 : 0) << 1;  // P/J
        // 第1位预留扩展

        return (byte) code;
    }

    // 将数字解码为MBTI字符串
    public static String decodeMBTI(byte code) {
        StringBuilder mbti = new StringBuilder();
        mbti.append((code & 16) != 0 ? 'I' : 'E')  // 第5位
                .append((code & 8) != 0 ? 'S' : 'N')   // 第4位
                .append((code & 4) != 0 ? 'T' : 'F')   // 第3位
                .append((code & 2) != 0 ? 'J' : 'P');  // 第2位
        // 第1位预留扩展

        return mbti.toString();
    }
}
