package com.example.seckilldemo.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {
    NEW("新建未支付", 0),
    PAY("已支付", 1),
    PAY_FAIL("支付失败", -1),
    CANCLE("取消订单", -2),
    SHIPPED("已发货", 2),
    RECEIPT("已收货", 3),
    RETURN("退货", -3),
    COMPLETE("完成", 4);

    private String name;

    private Integer value;

    public static Boolean isCanDelete(Integer status) {
        if (PAY_FAIL.value.equals(status) || RETURN.value.equals(status)
                || CANCLE.value.equals(status)) {
            return true;
        }
        return false;
    }

    public static Boolean isCanCancel(Integer status) {
        if (NEW.value.equals(status) || PAY_FAIL.value.equals(status)) {
            return true;
        }
        return false;
    }

    public static Boolean isCanShipped(Integer status) {
        if (PAY.value.equals(status)) {
            return true;
        }
        return false;
    }

    public static Boolean isCanReceipt(Integer status) {
        if (SHIPPED.value.equals(status)) {
            return true;
        }
        return false;
    }

    public static Boolean isCanReturn(Integer status) {
        if (status != null && PAY.value <= status) {
            return true;
        }
        return false;
    }

    public static Boolean isCanComplete(Integer status) {
        if (RECEIPT.value.equals(status)) {
            return true;
        }
        return false;
    }

    public static boolean isDeleteRedis(Integer status) {
        if (status < 0) {
            return true;
        }
        return false;
    }

    public static boolean isCanPay(Integer status) {
        if (NEW.value.equals(status)) {
            return true;
        }
        return false;
    }
}
