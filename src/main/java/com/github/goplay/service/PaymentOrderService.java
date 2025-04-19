package com.github.goplay.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.goplay.constant.PayStatus;
import com.github.goplay.entity.PaymentOrder;
import com.github.goplay.exception.OrderNotFoundException;
import com.github.goplay.mapper.PaymentOrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class PaymentOrderService {

    private final PaymentOrderMapper paymentOrderMapper;

    public PaymentOrderService(PaymentOrderMapper paymentOrderMapper) {
        this.paymentOrderMapper = paymentOrderMapper;
    }

    public PaymentOrder createOrder(Integer userId, float amount) {
        PaymentOrder order = new PaymentOrder();
        order.setOrderNo(generateOrderNo());
        order.setPrepayId("MOCK_" + UUID.randomUUID());
        order.setUserId(userId);
        order.setAmount(BigDecimal.valueOf(amount));
        order.setStatus(PayStatus.OrderStatus.CREATED.getCode());
        order.setExpireTime(LocalDateTime.now().plusMinutes(30));

        // 持久化订单到数据库
        paymentOrderMapper.insert(order);
        return order;
    }

    public PaymentOrder processMockPayment(String prepayId, boolean success) {
        QueryWrapper<PaymentOrder> queryWrapper = Wrappers.query();
        queryWrapper.eq("prepay_id", prepayId);
        PaymentOrder order = paymentOrderMapper.selectOne(queryWrapper);

        if (order == null) {
            throw new OrderNotFoundException();
        }

        order.setStatus(success ? PayStatus.OrderStatus.PAID.getCode() : PayStatus.OrderStatus.FAILED.getCode());
        if(success){
            //mock假设金额就是充值金额
            order.setPaidAmount(order.getAmount());
            order.setSuccessTime(LocalDateTime.now());
        }
        // 更新订单状态
        paymentOrderMapper.updateById(order);
        // 触发模拟回调
        //sendMockNotify(order);
        return order;
    }

    public PaymentOrder getValidOrder(Integer userId, String prepayId) {
        QueryWrapper<PaymentOrder> queryWrapper = Wrappers.query();
        queryWrapper.eq("prepay_id", prepayId).eq("user_id", userId);
        PaymentOrder order = paymentOrderMapper.selectOne(queryWrapper);

        if (order == null) {
            throw new OrderNotFoundException();
        }
        return order;
    }

    private String generateOrderNo() {
        // 生成唯一订单编号的逻辑
        return UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }

    //    public PaymentOrder handlePaymentNotify(Map<String, String> params) {
//        String prepayId = params.get("prepay_id");
//        QueryWrapper<PaymentOrder> queryWrapper = Wrappers.query();
//        queryWrapper.eq("prepay_id", prepayId);
//        PaymentOrder order = paymentOrderMapper.selectOne(queryWrapper);
//
//        // 检查订单是否存在
//        if (order == null) {
//            throw new OrderNotFoundException();
//        }
//
//        // 如果订单已处理过，则返回
//        if (order.getStatus() == Status.OrderStatus.PAID.getCode()) {
//            return order; // 已处理过回调
//        }
//
//        // 根据返回的结果代码更新状态
//        if ("SUCCESS".equals(params.get("result_code"))) {
//            // 状态转移到支付成功
//            updateOrderStatus(order, Status.OrderStatus.PAID, params);
//        } else {
//            // 状态转移到支付失败
//            updateOrderStatus(order, Status.OrderStatus.FAILED, params);
//        }
//
//        // 更新订单状态
//        paymentOrderMapper.updateById(order);
//        return order;
//    }


//    // Mock notify logic (simplified for clarity) 理论上需要前端订阅端点，前端打算直接用手动轮询，所以这里方法可以省略
//    private void sendMockNotify(PaymentOrder order) {
//        Map<String, String> params = new HashMap<>();
//        params.put("prepay_id", order.getPrepayId());
//        params.put("total_fee", order.getAmount().toString());
//        params.put("result_code", order.getStatus() == Status.OrderStatus.PAID.getCode() ? "SUCCESS" : "FAIL");
//
//        // 异步发送回调
//        CompletableFuture.runAsync(() -> {
//            // 这里可以使用任何需要的线程安全的方法来发送请求，理论上需要前端订阅端点，前端打算直接用手动轮询，所以这里方法可以省略
//            System.out.println("Sending mock notification: " + params);
//        });
//    }

//    // 新增方法以更新订单状态
//    private void updateOrderStatus(PaymentOrder order, Status.OrderStatus newStatus, Map<String, String> params) {
//        order.setStatus(newStatus.getCode());
//
//        if (newStatus == Status.OrderStatus.PAID) {
//            order.setPaidAmount(new BigDecimal(params.get("total_fee")));
//            order.setSuccessTime(LocalDateTime.now());
//        }
//        // 可扩展：还可以在这里处理失败状态时的其他信息，例如错误码，失败原因等
//    }
}