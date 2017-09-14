package com.yln.payphone;

/**
 * Created by linnan.yao on 2017/9/14.
 */

public class PayConstants {

    public static final String ALGORITHM = "RSA";

    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    public static final String DEFAULT_CHARSET = "UTF-8";

    public static final String MODE = "00";// 00正式环境 01测试环境
    public static final String PARTEN = "2088811691070175";// 支付宝商户ID
    // 支付宝商户私钥
    public static final String KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAPNdE0YUFGIWaJqO3zjZ7XIMENM8NRKJM3FyqiS9Zv207ZUyQi6PovTgS/Kb+fjSEhewrWlAjo9GdshH29eU2owLwEFh4dsH47prV8StqzFyf6bP2d5tlHHANIyDpyDAOt8+I9lR1dYG8FOUljf/DXToDaQ8Acoicss57NGQiu+lAgMBAAECgYB1p5Xd129TTIodztzUW2eUL4ki1oTCBVq9ijX/tSHJnmMOMlIUuJSL1rRhAk+Dn+vnUB9GN1O0qNhcU4FRZEGTad/gyYilOAyzm9tkwrmD4iEe7mrrFQ3MeG1wJciCc4qBXKioa3znXqTQwFQzdmn5h1n2s6yN4LPBedrA8al3sQJBAP/W+wZoxa/S6GcJ7UA2ro6slpEi02AhPc2tQ8y2wNg3Sc9jAlVrfXDmjd58gpoSIuBVfLNtBcqJUSjPZQ5CFkcCQQDzhBgpbm2aHEvjSC5Ag1tmGEhSi0VbRHUFKUNS11uTO662hI5MjmQgr5MO11Oj5T81rmjm5Ph2y8IHUgRPAcSzAkBDztz1k+thcIr8VFs3e8bENeYqTYqsneLdkqC3r3BpXi4H83v42+aBd/n+EA4le80WnFHS1ICGb+K86EsmfkmdAkAvIFI4umk1mOJK0u44h/NJN8vPRaqfdgkbfZfM0WakgLlYEyEiya+w76mLBrrmDfiEoAMnJAq9msMq/YFd9N6nAkB9pEUVshzLBWfvNytCyAQAPOEMoqUdWYYAlkh6XbgsLxKTBVhYaxgOHU6Zfs3dm6FOASrgynDK17B8VUcAkNxY";
    public static final String SELLER = "sc888@51szsc.com";// 支付宝商家账号
    public static final String SUBJECT = "停车互联系统";// 主题
    public static final String BODY = "停车扣费";// 内容
    public static final String SIGN = "停车扣费";// 签名
    public static final String SEVICE = "mobile.securitypay.pay";// 服务
    public static final String APPID = "wx8a3d99a4490bd903";//微信公众账号或开放平台APP的唯一标识
    public static final String APPKEY = "szsc1306745401rbbr1045476031cszs";
    public static final String PARTERID = "1306745401";//微信支付商户号
    public static final String WEIXINPAY = "https://api.mch.weixin.qq.com/pay/unifiedorder";//微信支付接口链接
    public static final String TN_ID="611687233439937794600";//银联支付所需TN号
}
