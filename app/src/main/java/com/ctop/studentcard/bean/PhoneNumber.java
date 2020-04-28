package com.ctop.studentcard.bean;

import com.ctop.studentcard.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 情景模式
 */
public class PhoneNumber {
    /**
     * EachPhoneNumber
     * phoneType :
     * phoneNumber :
     */

    private String sosNumber;
    private List<EachPhoneNumber> items;

    public List<EachPhoneNumber> getItems() {
        return items;
    }

    public void setItems(List<EachPhoneNumber> items) {
        this.items = items;
    }

    public String getSosNumber() {
        return sosNumber;
    }

    public void setSosNumber(String sosNumber) {
        this.sosNumber = sosNumber;
    }

    public static class EachPhoneNumber {
        private String phoneType;
        private String phoneNumber;

        public String getPhoneType() {
            return phoneType;
        }

        public void setPhoneType(String phoneType) {
            this.phoneType = phoneType;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }

    /**
     * {"phoneType":"","phoneNumber":""}
     */
    //0=13900000000!1=!2=13900000002!3=13900000003!4=13900000004!5=13900000005!
    public static PhoneNumber parseJson(String data) {
        PhoneNumber phoneNumber = new PhoneNumber();
        List<EachPhoneNumber> eachPhoneNumberList = new ArrayList<>();
        String[] strings = data.split("!");
        for (int i = 0; i < strings.length; i++) {
            String[] strings1 = strings[i].split("=");
            EachPhoneNumber eachPhoneNumber = new EachPhoneNumber();
            if(strings1[0].equals("0")){
                phoneNumber.setSosNumber(strings1[1]);
            }else {
                eachPhoneNumber.setPhoneType(strings1[0]);
                eachPhoneNumber.setPhoneNumber(strings1[1]);
                eachPhoneNumberList.add(eachPhoneNumber);
            }
        }
        phoneNumber.setItems(eachPhoneNumberList);
        return phoneNumber;
    }


}
