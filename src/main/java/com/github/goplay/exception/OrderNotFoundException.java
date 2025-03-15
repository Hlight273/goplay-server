package com.github.goplay.exception;

public class OrderNotFoundException extends RuntimeException {

  public OrderNotFoundException() {
    super("订单不存在！");
  }

  public OrderNotFoundException(String message) {
    super(message);
  }

  public OrderNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public OrderNotFoundException(Throwable cause) {
    super(cause);
  }
}
