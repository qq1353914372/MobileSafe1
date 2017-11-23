package com.helloword.lgy.mobilesafe.db.db.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by hasee on 2017/11/2.
 */

public class ProcessInfo {
    public String name;
    public String packageName;
    public Drawable icon;
    public boolean isSystem;
    public long processmemory;
    public boolean isCheck;

    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public long getProcessmemory() {
        return processmemory;
    }

    public void setProcessmemory(long processmemory) {
        this.processmemory = processmemory;
    }
}
