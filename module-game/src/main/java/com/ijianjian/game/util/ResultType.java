package com.ijianjian.game.util;

import com.ijianjian.core.common.interfaces.IResultType;

public class ResultType {
public enum LanguageError implements IResultType {
	number_exists(300000, "语言编号已存在"),;

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

	LanguageError(int code, String resultMsgZh) {
		this.code = code;
		this.resultMsgZh = resultMsgZh;
	}
}

public enum ColumnAppGameError implements IResultType {
	game_exists(300100, "游戏已绑定"),;

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

	ColumnAppGameError(int code, String resultMsgZh) {
		this.code = code;
		this.resultMsgZh = resultMsgZh;
	}
}

public enum ColumnGeneralGameError implements IResultType {
	game_exists(300100, "游戏已绑定"), no_child_game(300101, "非子节点无法添加游戏"), bind_ad(300202, "已绑定广告"), bind_column_marking(300203, "已绑定营销栏目"), exists_child(300204, "有下级栏目"), bind_channel(300202, "已绑定渠道");

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

	ColumnGeneralGameError(int code, String resultMsgZh) {
		this.code = code;
		this.resultMsgZh = resultMsgZh;
	}
}

public enum ColumnMarketingError implements IResultType {
	game_exists(300200, "游戏已绑定"), ad_exists(300201, "广告已绑定"), column_exists(300202, "广告已绑定栏目");

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

	ColumnMarketingError(int code, String resultMsgZh) {
		this.code = code;
		this.resultMsgZh = resultMsgZh;
	}
}

public enum AdError implements IResultType {
	bind_column_marking(300300, "已绑定营销栏目");

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

	AdError(int code, String resultMsgZh) {
		this.code = code;
		this.resultMsgZh = resultMsgZh;
	}
}

public enum GameInfoError implements IResultType {
	only_member_download(300400, "仅限会员下载"), login_user_download(300401, "登录下载"), need_shell(3004002, "游戏需要加壳");

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

	GameInfoError(int code, String resultMsgZh) {
		this.code = code;
		this.resultMsgZh = resultMsgZh;
	}
}

public enum GameStoreAppError implements IResultType {
	version_exists(300500, "版本号已存在"), version_cant_low(300501, "版本号不能低于最大版本");

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

	GameStoreAppError(int code, String resultMsgZh) {
		this.code = code;
		this.resultMsgZh = resultMsgZh;
	}
}
}
