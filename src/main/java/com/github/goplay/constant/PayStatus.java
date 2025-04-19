package com.github.goplay.constant;

import java.util.HashMap;
import java.util.Map;

public class PayStatus {
    public enum OrderStatus {
        CREATED(0, "已创建"),
        PAYING(1, "支付中"),
        PAID(2, "支付成功"),
        FAILED(3, "支付失败"),
        EXPIRED(4, "已过期");

        private final int code;
        private final String desc;

        OrderStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        private static final Map<Integer, String> CODE_TO_DESC = new HashMap<>();
        static {
            for (OrderStatus status : values()) {
                CODE_TO_DESC.put(status.code, status.desc);
            }
        }
        public static String getDescByCode(int code) {
            String desc = CODE_TO_DESC.get(code);
            if (desc == null) {
                throw new IllegalArgumentException("无效状态码: " + code);
            }
            return desc;
        }
    }
}
