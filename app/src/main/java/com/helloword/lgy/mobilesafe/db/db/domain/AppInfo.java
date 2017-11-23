package com.helloword.lgy.mobilesafe.db.db.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by hasee on 2017/10/31.
 */

public class AppInfo {
    public String name;
    public String packageName;
    public Drawable icon;
    public boolean isCdCard;
    public boolean isSystem;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public boolean isCdCard() {
        return isCdCard;
    }

    public void setCdCard(boolean cdCard) {
        isCdCard = cdCard;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
