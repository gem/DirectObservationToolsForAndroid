package com.idctdo.android;


public class DBRecord {
    
    private String orderName;
    private String orderStatus;
    private String json;
    private int layoutId;
    
    public String getOrderName() {
        return orderName;
    }
    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }
    public String getOrderStatus() {
        return orderStatus;
    }
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    
    public void setJson(String jsonString) {
        this.json = jsonString;
    }
    public String getJson() {
        return json;
    }
    
    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }
    public int getLayoutId() {
        return layoutId;
    }
}