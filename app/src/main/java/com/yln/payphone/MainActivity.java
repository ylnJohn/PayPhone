package com.yln.payphone;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.unionpay.UPPayAssistEx;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends Activity {

    private RadioGroup mPayTypeGroup;
    private Button mPayBtn;
    private IWXAPI api;
    private int type=-1;
    private String order="123456",notify_url="http://www.baidu.com/";//订单号与回调接口
    private String sign="";
    private Map<String, String> resultunifiedorder;
    private MyHandler mHandler;

    private static class MyHandler extends Handler{

        private WeakReference<MainActivity> wrf;

        public MyHandler(MainActivity activity){
            wrf=new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity=wrf.get();
            if(activity==null){
                return;
            }
            activity.mPayBtn.setEnabled(true);
            switch (msg.what){
                case 1:
                    String result = (String) msg.obj;
                    PayResult pr = new PayResult(result);
                    if (TextUtils.equals(pr.getResultStatus(), "9000")) {
                        Utils.showToast(activity, R.string.pay_success);
                    }
                    break;
                case 2:
                    if (activity.resultunifiedorder != null
                            && activity.resultunifiedorder.containsKey("prepay_id")) {
                        activity.api.registerApp(PayConstants.APPID);
                        activity.api.sendReq(activity.getWeixinReq());
                    } else {
                        Utils.showToast(activity, R.string.pay_failtrue);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPayTypeGroup= (RadioGroup) findViewById(R.id.pay_type);
        mPayBtn= (Button) findViewById(R.id.pay_btn);
        mPayTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.zfb_btn){
                    type=0;
                }else if(checkedId==R.id.weixin_btn){
                    type=1;
                }else if(checkedId==R.id.bank_btn){
                    type=2;
                }
            }
        });
        mPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(type>=0){
                    mPayBtn.setEnabled(false);
//                    Utils.showProgressDialog(MainActivity.this,"paying...");
                    switch (type){
                        case 0://支付宝
                            payByZFB();
                            break;
                        case 1://微信
                            payByWeixin();
                            break;
                        case 2://银联支付
                            UPPayAssistEx.startPay(MainActivity.this,null,null,PayConstants.TN_ID,"01");
                            break;
                    }
                }else{
                    Utils.showToast(MainActivity.this,R.string.pay_type_error);
                }
            }
        });
        mHandler=new MyHandler(MainActivity.this);
        api = WXAPIFactory.createWXAPI(getApplicationContext(), null);
    }

    //银联支付结果回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPayBtn.setEnabled(true);
        if (data == null) {
            return;
        }
        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        Log.v("payphone", "2 "+data.getExtras().getString("merchantOrderId"));
        if (str.equalsIgnoreCase("success")) {
            msg = "支付成功！";
        } else if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";

        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
        }
        Utils.showToast(MainActivity.this,msg);
        //支付完成,处理自己的业务逻辑!
    }

    private void payByWeixin(){
        if (api.getWXAppSupportAPI() >= com.tencent.mm.opensdk.constants.Build.PAY_SUPPORTED_SDK_INT) {
            GetPrepayIdTask getPrepayId = new GetPrepayIdTask();
            getPrepayId.execute();
        } else {
            mPayBtn.setEnabled(true);
            Utils.showToast(MainActivity.this, R.string.pay_type_error);
        }
    }

    private void payByZFB(){
        final String value = getPayValue(order, notify_url);
        sign = sign(value, PayConstants.KEY);
        try {
            // 仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("pay", "pay error!");
        }
        // sign =
        // "lBBK%2F0w5LOajrMrji7DUgEqNjIhQbidR13GovA5r3TgIbNqv231yC1NksLdw%2Ba3JnfHXoXuet6XNNHtn7VE%2BeCoRO1O%2BR1KugLrQEZMtG5jmJIe2pbjm%2F3kb%2FuGkpG%2BwYQYI51%2BhA3YBbvZHVQBYveBqK%2Bh8mUyb7GM1HxWs9k4%3D";
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(MainActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(value + "&sign=\"" + sign
                        + "\"&sign_type=\"RSA\"",true);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private String getPayValue(String order, String url) {
        StringBuffer sb = new StringBuffer();
        sb.append("partner=" + "\"" + PayConstants.PARTEN + "\"");
        sb.append("&seller_id=" + "\"" + PayConstants.SELLER + "\"");
        sb.append("&out_trade_no=" + "\"" + order + "\"");
        sb.append("&subject=" + "\"" + PayConstants.SUBJECT + "\"");
        sb.append("&body=" + "\"" + PayConstants.BODY + "\"");
        sb.append("&total_fee=" + "\"" + 0.01 + "\"");//支付金额
        sb.append("&notify_url=" + "\"" + url + "\"");
        sb.append("&service=" + "\"" + PayConstants.SEVICE + "\"");
        sb.append("&payment_type=\"1\"");
        sb.append("&_input_charset=\"UTF-8\"");
        sb.append("&sign_type=\"RSA\"");
        return sb.toString();
    }

    private String sign(String content, String privateKey) {
        try {
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(
                    com.yln.payphone.Base64.decode(privateKey));
            KeyFactory keyf = KeyFactory.getInstance(PayConstants.ALGORITHM);
            PrivateKey priKey = keyf.generatePrivate(priPKCS8);

            java.security.Signature signature = java.security.Signature
                    .getInstance(PayConstants.SIGN_ALGORITHMS);

            signature.initSign(priKey);
            signature.update(content.getBytes(PayConstants.DEFAULT_CHARSET));

            byte[] signed = signature.sign();

            return com.yln.payphone.Base64.encode(signed);
        } catch (Exception e) {
            Log.e("pay", "pay error!");
        }

        return null;
    }

    private String getRandomString(int length) {
        Random random = new Random();
        StringBuffer buffer = new StringBuffer(
                "0123456789abcdefghijklmnopqrstuvwxyz");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append(buffer.charAt(random.nextInt(buffer.length())));
        }
        return sb.toString();
    }

    private String genProductArgs() {

        try {
            String nonceStr = getRandomString(32);

            List<NameValuePair> packageParams = new LinkedList<>();
            packageParams
                    .add(new BasicNameValuePair("appid", PayConstants.APPID));
            packageParams.add(new BasicNameValuePair("body", order));
            packageParams.add(new BasicNameValuePair("mch_id", PayConstants.PARTERID));
            packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
            packageParams.add(new BasicNameValuePair("notify_url", notify_url));
            packageParams.add(new BasicNameValuePair("out_trade_no", order));
            packageParams.add(new BasicNameValuePair("spbill_create_ip", Utils.getIPAddress(MainActivity.this)));
            packageParams.add(new BasicNameValuePair("total_fee", "1"));//支付金额（以分为单位）
            packageParams.add(new BasicNameValuePair("trade_type", "APP"));
            String sign = genPackageSign(packageParams);
            packageParams.add(new BasicNameValuePair("sign", sign));

            String xmlstring = toXml(packageParams);

            return xmlstring;

        } catch (Exception e) {
            return null;
        }

    }

    private PayReq getWeixinReq() {
        PayReq req = new PayReq();
        String time = System.currentTimeMillis() / 1000 + "";
        String nonce = getRandomString(32);
        req.appId = PayConstants.APPID;
        req.partnerId = PayConstants.PARTERID;
        req.prepayId = resultunifiedorder.get("prepay_id");
        req.nonceStr = nonce;
        req.timeStamp = time;
        req.packageValue = "Sign=WXPay";
        req.sign = getWeixinSign(req);
        return req;
    }

    private String getWeixinSign(PayReq req) {
        String sign = "";
        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
        sign = genAppSign(signParams);
        return sign;
    }

    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(PayConstants.APPKEY);

        String appSign = Utils.md5(sb.toString().getBytes());
        Log.e("orion", appSign);
        return appSign.toUpperCase();
    }

    private String genPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(PayConstants.APPKEY);

        String packageSign = Utils.md5(sb.toString().getBytes()).toUpperCase();
        return packageSign;
    }

    private String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<" + params.get(i).getName() + ">");

            sb.append(params.get(i).getValue());
            sb.append("</" + params.get(i).getName() + ">");
        }
        sb.append("</xml>");

        Log.e("orion", sb.toString());
        return sb.toString();
    }

    private Map<String, String> decodeXml(String content) {

        try {
            Map<String, String> xml = new HashMap<>();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {

                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:

                        break;
                    case XmlPullParser.START_TAG:

                        if ("xml".equals(nodeName) == false) {
                            // 实例化student对象
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

            return xml;
        } catch (Exception e) {
            Log.e("orion", e.toString());
        }
        return null;

    }

    private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onPostExecute(Map<String, String> result) {
            resultunifiedorder = result;
            mHandler.sendEmptyMessage(2);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected Map<String, String> doInBackground(Void... params) {
            String entity = genProductArgs();
            byte[] buf = HttpUtil.httpPost(PayConstants.WEIXINPAY, entity);
            if (buf != null) {
                String content = new String(buf);
                Log.e("orion", content);
                Map<String, String> xml = decodeXml(content);
                return xml;
            } else
                return null;
        }
    }

}
