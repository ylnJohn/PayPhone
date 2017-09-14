/**
 * WXPayEntryActivity.java	  V1.0   2016-1-26 下午11:30:17
 *
 * Copyright Talkweb Information System Co. ,Ltd. All rights reserved.
 *
 * Modification history(By    Time    Reason):
 * 
 * Description:
 */

package com.yln.payphone.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yln.payphone.PayConstants;
import com.yln.payphone.R;
import com.yln.payphone.Utils;


/**
 * descrition：微信支付结果回调
 * 
 * @author yaolinnan
 * 
 *         <p>
 *         modify history:
 *         </p>
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private Context mContext = null;

	private IWXAPI api;

	@Override
	public void onReq(BaseReq req) {
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:

			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			break;
		case ConstantsAPI.COMMAND_LAUNCH_BY_WX:

			break;
		default:
			break;
		}
	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			switch (resp.errCode) {
			case 0:
				Utils.showToast(mContext, R.string.pay_success);
				// setResult(Constants.PAY_RESULT_SUCCESS_CODE);

				break;
			case -1:
				Utils.showToast(mContext, R.string.pay_failtrue);
				break;
			case -2:
				Utils.showToast(mContext, R.string.pay_failtrue);
				break;
			}
			finish();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getApplicationContext();
		api = WXAPIFactory.createWXAPI(this, PayConstants.APPID);
		api.handleIntent(getIntent(), this);
	}

}
