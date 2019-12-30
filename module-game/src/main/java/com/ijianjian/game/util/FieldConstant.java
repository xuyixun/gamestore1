package com.ijianjian.game.util;

public class FieldConstant {
public enum GameInfoStatus {
	shang_xian, cao_gao, xia_xian, dai_shen_he, bo_hui
}

public enum GameInfoChargeType {
	free, member
}

public enum ChannelType {
	sou_ye, lan_mu, ying_yong, tui_guang_qu_dao, wap, android
}

public enum AdType {
	wai_bu_lian_jie, ying_yong, lan_mu, qi_dong_ye_guang_gao, tui_guang_guang_gao, tao_can, pop_guang_gao, wen_zi_guang_gao
}

public enum ColumnMarketingType {
	ad, game, column
}

public enum GameLogType {
	detail, download
}

public enum GameStoreAppLogType {
	open, download, first_open
}

public enum GameLogFrom {
	home, column_app, column_general_l1, column_general_l2, search, recommend_search, detail_search, my_game, other, landing_page, detail_recommend, detail
}

public enum GameLogClient {
	h5, app
}
}
