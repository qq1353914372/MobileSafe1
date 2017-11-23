package com.helloword.lgy.mobilesafe.db.db.domain;

/**
 * Created by hasee on 2017/10/25.
 */

public class BlackNumberInfo {
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String phone;
    public String mode;

    @Override
    public String toString() {
        return "BlackNumberInfo{" +
                "phone='" + phone + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }
}
