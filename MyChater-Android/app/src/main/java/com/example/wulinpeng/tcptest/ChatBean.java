package com.example.wulinpeng.tcptest;

import android.graphics.Bitmap;

/**
 * Created by wulinpeng on 16/5/1.
 */
public class ChatBean {

    private int type;

    private String text;

    private Bitmap icon;

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
