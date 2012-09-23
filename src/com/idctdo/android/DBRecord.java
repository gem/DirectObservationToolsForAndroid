package com.idctdo.android;


public class DBRecord {
    
    private String attributeDescription;
    private String attributeValue;
    private String attributeScope;
    private int recordCode;
    
    public String getAttributeDescription() {
        return attributeDescription;
    }
    public void setAttributeDescription(String orderName) {
        this.attributeDescription = orderName;
    }
    public String getAttributeValue() {
        return attributeValue;
    }
    public void setAttributeValue(String orderStatus) {
        this.attributeValue = orderStatus;
    }
    
    public void setJson(String jsonString) {
        this.attributeScope = jsonString;
    }
    public String getJson() {
        return attributeScope;
    }
    
    public void setrecordCode(int recordCode) {
        this.recordCode = recordCode;
    }
    public int getCode() {
        return recordCode;
    }
    
    public String toString(){
    	
		return attributeDescription + " " + attributeValue + " " + attributeScope + " " + recordCode;
    	
    
    }
}