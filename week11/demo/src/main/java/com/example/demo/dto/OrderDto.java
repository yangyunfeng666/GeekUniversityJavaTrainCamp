package com.example.demo.dto;

import java.io.Serializable;


public class OrderDto implements Serializable {

  private long orderId;

  private String name;

  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
