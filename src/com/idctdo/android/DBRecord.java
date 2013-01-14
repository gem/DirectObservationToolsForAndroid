/*******************************************************************************
 * Copyright (c) 2010-2012, GEM Foundation.
 * IDCT Android is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * IDCT Android is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with IDCT Android.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
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
