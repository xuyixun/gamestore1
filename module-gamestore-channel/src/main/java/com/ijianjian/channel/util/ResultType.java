package com.ijianjian.channel.util;

import com.ijianjian.core.common.interfaces.IResultType;

public class ResultType {
public enum UserError implements IResultType {
	name_exists(200000, "用户名已存在"), re_subscribe(200001, "重复订阅"), cant_subscribe(200002, "无法订阅"), nu_send_code(200004, "未发送验证码"), code_error(200005, "验证码错误"), send_code_error(200006, "发送失败"), cant_unsubscribe(200007, "无法取消订阅"),;

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

	UserError(int code, String resultMsgZh) {
		this.code = code;
		this.resultMsgZh = resultMsgZh;
	}
}
}
