package com.github.goplay.controller;

import com.github.goplay.constant.Status;
import com.github.goplay.entity.PaymentOrder;
import com.github.goplay.service.PaymentOrderService;
import com.github.goplay.utils.JwtUtils;
import com.github.goplay.utils.Result;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/mock/pay")
public class PayController {

    private final PaymentOrderService orderService;

    public PayController(PaymentOrderService paymentOrderService) {
        this.orderService = paymentOrderService;
    }

    // 创建充值订单
    @PostMapping("/create/{amount}")
    public Result createOrder(
            @RequestHeader("token") String token,
            @PathVariable float amount) {
        if(amount<1){
            return  Result.error().message("数额不得小于1！");
        }
        Integer userId = JwtUtils.getUserIdFromToken(token);
        PaymentOrder order = orderService.createOrder(userId, amount);

        return Result.ok()
                .oData(Map.of(
                        "orderNo", order.getOrderNo(),
                        "prepayId", order.getPrepayId(),
                        "qrUrl", generateMockQr(order.getPrepayId()),
                        "expireTime", order.getExpireTime()
                ))
                .message("订单创建成功");
    }


    // 订单查询接口
    @GetMapping("/status/{prepayId}")
    public Result queryOrderStatus(
            @RequestHeader("token") String token,
            @PathVariable String prepayId) {

        Integer userId = JwtUtils.getUserIdFromToken(token);
        PaymentOrder order = orderService.getValidOrder(userId, prepayId);

        if (order == null) {
            return Result.error().message("订单不存在！");
        }
        if(order.getPaidAmount()!=null && order.getSuccessTime()!=null){
            return Result.ok()
                    .oData(Map.of(
                            "status", Status.OrderStatus.getDescByCode(order.getStatus()),
                            "paidAmount", order.getPaidAmount(),
                            "successTime", order.getSuccessTime() ))
                    .message("订单已完成！");
        }else{
            return Result.empty().message("订单尚未完成！").oData(Map.of("status", Status.OrderStatus.getDescByCode(order.getStatus())));
        }
    }

    private String generateMockQr(String prepayId) {
        return "http://localhost:8081/mock/pay/confirm?prepayId=" + prepayId;
    }



    // 接收模拟支付结果
    @PostMapping("/confirm/{prepayId}/{success}")
    public Result confirmPayment(
            @PathVariable String prepayId,
            @PathVariable boolean success) {

        orderService.processMockPayment(prepayId, success);
        return Result.ok().message("支付结果已接收");
    }

//    // 模拟支付确认页面 //这部分让前端自己做单独页面
//    @GetMapping("/confirm/{prepayId}")
//    public String showConfirmPage(@PathVariable String prepayId) {
//        return "<html>...模拟支付页面（金额、确认按钮）...</html>";
//    }

//    // 支付结果回调接口 //目前前端使用轮询而不是后端主动通知，注释了
//    @PostMapping("/notify")
//    public String paymentNotify(@RequestBody Map<String, String> params) {
//        // 这里可以加入验签的逻辑
//        // if (!MockSignUtil.verify(params)) {
//        //     return "<xml><return_code>FAIL</return_code></xml>";
//        // }
//
//        PaymentOrder order = orderService.handlePaymentNotify(params);
//
//        // 处理支付成功后的逻辑，比如积分变更
//        if (order.getStatus() == Status.OrderStatus.PAID.getCode()) {
//            // 处理积分或其他逻辑
//            // vipService.addPoints(order.getUserId(), calculatePoints(order.getAmount()));
//        }
//
//        return "<xml><return_code>SUCCESS</return_code></xml>";
//    }

}
