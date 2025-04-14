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

    /**
     * 基于匹配的维度数量判断两个MBTI类型是否相似（Integer版本）
     * @param encodedMbti1 第一个MBTI类型（编码后的Integer）
     * @param encodedMbti2 第二个MBTI类型（编码后的Integer）
     * @param requiredMatches 需要匹配的维度数量（0-4）
     * @return 如果类型被认为相似则返回true
     */
    public static boolean isMbtiSimilar(Integer encodedMbti1, Integer encodedMbti2, int requiredMatches) {
        // 处理null值情况（根据业务需求决定）
        if (encodedMbti1 == null || encodedMbti2 == null) {
            return false;
        }

        // 校验匹配维度参数是否合法
        if (requiredMatches < 0 || requiredMatches > 4) {
            return false;
        }

        // 初始化匹配计数器
        int matches = 0;
        // 定义每个维度的位掩码（E/I, N/S, F/T, J/P）
        // 注意：Java中Integer是32位，需要确保编码时只使用低8位
        int[] masks = {
                0b00010000,  // E/I维度掩码（第5位）
                0b00001000,  // N/S维度掩码（第4位）
                0b00000100,  // F/T维度掩码（第3位）
                0b00000010   // J/P维度掩码（第2位）
        };

        // 遍历每个维度进行比较
        for (int mask : masks) {
            // 使用位掩码提取对应维度的值进行比较
            if ((encodedMbti1 & mask) == (encodedMbti2 & mask)) {
                matches++;  // 如果该维度匹配，增加计数器
            }
        }

        // 判断匹配维度数是否达到要求
        return matches >= requiredMatches;
    }

    /**
     * 基于匹配的维度数量判断两个MBTI类型是否相似
     * @param encodedMbti1 第一个MBTI类型（编码后的字节）
     * @param encodedMbti2 第二个MBTI类型（编码后的字节）
     * @param requiredMatches 需要匹配的维度数量（0-4）
     * @return 如果类型被认为相似则返回true
     */
    public static boolean isMbtiSimilar(byte encodedMbti1, byte encodedMbti2, int requiredMatches) {
        // 校验匹配维度参数是否合法
        if (requiredMatches < 0 || requiredMatches > 4) {
            return false;
        }

        // 初始化匹配计数器
        int matches = 0;
        // 定义每个维度的位掩码（E/I, N/S, F/T, J/P）
        // 对应编码中的第5位到第2位（从右往左数）
        int[] masks = {
                0b10000,  // E/I维度掩码（第5位）
                0b01000,  // N/S维度掩码（第4位）
                0b00100,  // F/T维度掩码（第3位）
                0b00010   // J/P维度掩码（第2位）
        };

        // 遍历每个维度进行比较
        for (int mask : masks) {
            // 使用位掩码提取对应维度的值进行比较
            if ((encodedMbti1 & mask) == (encodedMbti2 & mask)) {
                matches++;  // 如果该维度匹配，增加计数器
            }
        }

        // 判断匹配维度数是否达到要求
        return matches >= requiredMatches;
    }

    /**
     * Mbti相似度匹配查询
     * @param mbti1 第一个mbti (e.g. "INTJ")
     * @param mbti2 第二个mbt (e.g. "INTP")
     * @param requiredMatches 要匹配几个字母 (0-4)
     * @return 如果是匹配，返回true
     */
    public static boolean isMbtiSimilar(String mbti1, String mbti2, int requiredMatches) {
        if (mbti1 == null || mbti2 == null ||
                mbti1.length() != 4 || mbti2.length() != 4) {
            return false;
        }

        int matches = 0;
        for (int i = 0; i < 4; i++) {
            if (Character.toUpperCase(mbti1.charAt(i)) ==
                    Character.toUpperCase(mbti2.charAt(i))) {
                matches++;
            }
        }

        return matches >= requiredMatches;
    }
}
