package com.ijianjian.core.common.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ijianjian.core.common.constant.ResultType;
import com.ijianjian.core.common.interfaces.IResultType;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Tolerate;

import java.io.Serializable;

@Getter
@Builder
public class CommonResult implements Serializable {
private static final long serialVersionUID = -1572505615016208501L;

@Tolerate
CommonResult() {
}

@JsonProperty("result_code")
private Integer resultCode;
@JsonProperty("result_msg")
private String resultMsg;
@JsonProperty("result_msg_zh")
private String resultMsgZh;
private Object result;
private long timestamp;

public static CommonResult result(IResultType resultType, Object result) {
	return CommonResult.builder().timestamp(System.currentTimeMillis()).resultCode(resultType.getCode()).resultMsg(resultType.getMsg()).resultMsgZh(resultType.getMsgZh()).result(result).build();
}

public static CommonResult successResult(Object result) {
	ResultType.SuccessResult successResult = new ResultType.SuccessResult();
	return CommonResult.builder().timestamp(System.currentTimeMillis()).resultCode(successResult.getCode()).resultMsg(successResult.getMsg()).resultMsgZh(successResult.getMsgZh()).result(result).build();
}

public static CommonResult errorResult(IResultType resultType) {
	return CommonResult.builder().timestamp(System.currentTimeMillis()).resultCode(resultType.getCode()).resultMsg(resultType.getMsg()).resultMsgZh(resultType.getMsgZh()).result(null).build();
}

public static CommonResult errorResult(Integer code, String msg) {
	return CommonResult.builder().timestamp(System.currentTimeMillis()).resultCode(code).resultMsg(msg).resultMsgZh(msg).result(null).build();
}
}
