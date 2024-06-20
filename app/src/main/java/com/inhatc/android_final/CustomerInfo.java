package com.inhatc.android_final;

import java.util.HashMap;
import java.util.Map;

public class CustomerInfo {
    public String strName;

    public CustomerInfo(){

    }
    public CustomerInfo(String Name){this.strName =Name;}

    public void mSet_CName(String Name){this.strName = Name;}
    public String mGet_CName(){return strName;}

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("Name", strName);

        return result;
    }
}
