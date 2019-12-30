package com.ijianjian.core.common.constant;

import com.ijianjian.core.common.interfaces.IResultType;

public class ResultType {
public static class SuccessResult implements IResultType {
@Override
public Integer getCode() {
	return 0;
}

@Override
public String getMsg() {
	return "success";
}

@Override
public String getMsgZh() {
	return "成功";
}
}

public enum CommonError implements IResultType {
	data_not_exist(100000, "无效数据"), list_empty(0, "无数据"), param_error(100002, "参数错误");

	private final Integer code;
	private final String resultMsgZh;

	@Override
	public Integer getCode() {
		return code;
	}

	@Override
	public String getMsg() {
		return this.name();
	}

	@Override
	public String getMsgZh() {
		return resultMsgZh;
	}

	CommonError(int code, String resultMsgZh) {
		this.code = code;
		this.resultMsgZh = resultMsgZh;
	}
}

public enum AuthError implements IResultType {
	un_authorized(101000, "未授权"), forbidden(101001, "无权限"), bad_credentials(101002, "账号密码错误"), user_not_exist(101003, "用户不存在"), user_disable(1001004, "用户已禁用"), sms_code_error(101005, "短信验证码错误");

	private final Integer code;
	private final String resultMsgZh;

	@Override
	public Integer getCode() {
		return code;
	}

	@Override
	public String getMsg() {
		return this.name();
	}

	@Override
	public String getMsgZh() {
		return resultMsgZh;
	}

	AuthError(int code, String resultMsgZh) {
		this.code = code;
		this.resultMsgZh = resultMsgZh;
	}
}

public enum FileError implements IResultType {
	create_file_error(200000, "上传失败");

	private final Integer code;
	private final String resultMsgZh;

	@Override
	public Integer getCode() {
		return code;
	}

	@Override
	public String getMsg() {
		return this.name();
	}

	@Override
	public String getMsgZh() {
		return resultMsgZh;
	}

	FileError(int code, String resultMsgZh) {
		this.code = code;
		this.resultMsgZh = resultMsgZh;
	}
}
}
