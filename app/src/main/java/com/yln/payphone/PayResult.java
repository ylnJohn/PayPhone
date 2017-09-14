/**
 * PayResult.java	  V1.0   2015-8-22 下午2:40:32
 *
 * Copyright Talkweb Information System Co. ,Ltd. All rights reserved.
 *
 * Modification history(By    Time    Reason):
 * 
 * Description:
 */

package com.yln.payphone;

import android.text.TextUtils;

/**
 * descrition：
 * 
 * @author yaolinnan
 * 
 *         <p>
 *         modify history:
 *         </p>
 */
public class PayResult {

	private String resultStatus;
	private String result;
	private String memo;

	public PayResult(String rawResult) {

		if (TextUtils.isEmpty(rawResult))
			return;

		String[] resultParams = rawResult.split(";");
		for (String resultParam : resultParams) {
			if (resultParam.startsWith("resultStatus")) {
				resultStatus = gatValue(resultParam, "resultStatus");
			}
			if (resultParam.startsWith("result")) {
				result = gatValue(resultParam, "result");
			}
			if (resultParam.startsWith("memo")) {
				memo = gatValue(resultParam, "memo");
			}
		}
	}

	@Override
	public String toString() {
		return "resultStatus={" + resultStatus + "};memo={" + memo + "};result={" + result + "}";
	}

	private String gatValue(String content, String key) {
		String prefix = key + "={";
		return content.substring(content.indexOf(prefix) + prefix.length(),
				content.lastIndexOf("}"));
	}

	/**
	 * @return the resultStatus
	 */
	public String getResultStatus() {
		return resultStatus;
	}

	/**
	 * @return the memo
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}
}
