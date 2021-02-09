package com.metasequoia.constent;

public class ErrorCode {
	public static final int SUCCESS = 0; // 成功
	public static final int FAIL = -1;
	public static final int ERR_PARAM_FAIL = -101;// 参数错误

	//TODO test
	public static String getErrMsg(int errCode) {
		switch (errCode) {
		case SUCCESS:
			return "成功";
			case ERR_PARAM_FAIL:
			return "参数错误";
		}
		return "未知错误";
	}
}
