package com.jpabook.jpashop.domain;

import lombok.Getter;

@Getter
public enum OrderStatus {
    NEW("신규 주문"),
    PAID("결제 완료"),
    PREPARING("상품 준비 중"),
    SHIPPED("배송 시작"),
    DELIVERED("배송 완료"),
    CANCELLED("주문 취소");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.name() + " (" + this.description + ")";
    }
}
