package com.androidsolution.manager;

/**
 * Created by wukai on 16/7/19.
 */
public class PayParm {
    public String nonceStr;
    public String partnerId;
    public String prepayId;
    public String timeStamp;
    public String sign;
    public PayParm(String partnerid,String prepayid,String noncestr,String timestamp,String sign){
        this.partnerId = partnerid;
        this.prepayId = prepayid;
        this.nonceStr = noncestr;
        this.timeStamp = timestamp;
        this.sign = sign;
    }
}
