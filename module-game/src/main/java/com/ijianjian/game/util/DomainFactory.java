package com.ijianjian.game.util;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.ijianjian.game.domain.po.Ad;
import com.ijianjian.game.domain.po.AdDetail;
import com.ijianjian.game.domain.po.Channel;
import com.ijianjian.game.domain.po.ColumnApp;
import com.ijianjian.game.domain.po.ColumnAppDetail;
import com.ijianjian.game.domain.po.ColumnAppGame;
import com.ijianjian.game.domain.po.ColumnGeneral;
import com.ijianjian.game.domain.po.ColumnGeneralDetail;
import com.ijianjian.game.domain.po.ColumnGeneralGame;
import com.ijianjian.game.domain.po.ColumnGeneralRelation;
import com.ijianjian.game.domain.po.ColumnMarketing;
import com.ijianjian.game.domain.po.ColumnMarketingAd;
import com.ijianjian.game.domain.po.ColumnMarketingColumn;
import com.ijianjian.game.domain.po.ColumnMarketingDetail;
import com.ijianjian.game.domain.po.ColumnMarketingGame;
import com.ijianjian.game.domain.po.GameInfo;
import com.ijianjian.game.domain.po.GameInfoDetail;
import com.ijianjian.game.domain.po.GameStoreApp;
import com.ijianjian.game.domain.po.Language;
import com.ijianjian.game.domain.po.MyGame;
import com.ijianjian.game.domain.vo.AdDetailVO;
import com.ijianjian.game.domain.vo.AdVO_001;
import com.ijianjian.game.domain.vo.AdVO_002;
import com.ijianjian.game.domain.vo.ChannelVO_001;
import com.ijianjian.game.domain.vo.ColumnAppDetailVO;
import com.ijianjian.game.domain.vo.ColumnAppGameVO;
import com.ijianjian.game.domain.vo.ColumnAppVO_001;
import com.ijianjian.game.domain.vo.ColumnAppVO_002;
import com.ijianjian.game.domain.vo.ColumnGeneralDetailVO;
import com.ijianjian.game.domain.vo.ColumnGeneralGameVO;
import com.ijianjian.game.domain.vo.ColumnGeneralVO_001;
import com.ijianjian.game.domain.vo.ColumnGeneralVO_002;
import com.ijianjian.game.domain.vo.ColumnMarketingAdVO;
import com.ijianjian.game.domain.vo.ColumnMarketingColumnVO;
import com.ijianjian.game.domain.vo.ColumnMarketingDetailVO;
import com.ijianjian.game.domain.vo.ColumnMarketingGameVO;
import com.ijianjian.game.domain.vo.ColumnMarketingVO_001;
import com.ijianjian.game.domain.vo.ColumnMarketingVO_002;
import com.ijianjian.game.domain.vo.GameInfoDetailVO;
import com.ijianjian.game.domain.vo.GameInfoVO_001;
import com.ijianjian.game.domain.vo.GameInfoVO_002;
import com.ijianjian.game.domain.vo.GameStoreAppVO_001;
import com.ijianjian.game.domain.vo.GameStoreAppVO_002;
import com.ijianjian.game.domain.vo.LanguageVO_001;
import com.ijianjian.game.domain.vo.MyGameVO;

public class DomainFactory {
public static LanguageVO_001 _2VO(Language s) {
	return s == null ? LanguageVO_001.builder().build() : LanguageVO_001.builder().uuid(s.getFUuid()).name(s.getFName()).number(s.getFNumber()).build();
}

public static List<LanguageVO_001> _2LanguageVO_001(List<Language> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2VO).collect(Collectors.toList());
}

public static ChannelVO_001 _2VO(Channel s) {
	return s == null ? ChannelVO_001.builder().build() : ChannelVO_001.builder().uuid(s.getFUuid()).number(s.getFName()).name(s.getFName()).url(s.getFUrl()).type(s.getType()).urlEncrypt(s.getFUrlEncrypt()).callBackParams(s.getFCallBackParams()).callBackUrl(s.getFCallBackUrl()).build();
}

public static List<ChannelVO_001> _2ChannelVO_001(List<Channel> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2VO).collect(Collectors.toList());
}

public static GameInfoVO_001 _2GameInfoVO_001(GameInfo s) {
	return s == null ? GameInfoVO_001.builder().build() : GameInfoVO_001.builder().uuid(s.getFUuid()).icon(s.getFIcon()).apkName(s.getFApkName()).apkSize(s.getFApkSize()).chargeType(s.getChargeType()).developmentCompany(s.getFDevelopmentCompany()).downloadCount(s.getFDownloadCount()).name(s.getFName()).score(s.getFScore()).status(s.getStatus()).version(s.getFVersion()).needShell(s.isFNeedShell()).build();
}

public static GameInfoVO_002 _2GameInfoVO_002(GameInfo s) {
	return s == null ? GameInfoVO_002.builder().build()
	  : GameInfoVO_002.builder().adsPictures(s.getFAdsPictures()).apk(s.getFApk()).apkName(s.getFApkName()).chargeType(s.getChargeType()).detail(s.getFDetail()).downloadCount(s.getFDownloadCount()).developmentCompany(s.getFDevelopmentCompany()).icon(s.getFIcon()).name(s.getFName()).score(s.getFScore())
	    .screenshot(s.getFScreenshot()).tag(s.getFTag()).uuid(s.getFUuid()).version(s.getFVersion()).languageDefault(s.getFLanguageDefault()).apkSize(s.getFApkSize()).build();
}

public static GameInfoDetailVO _2GameInfoDetailVO(GameInfoDetail s) {
	return s == null ? GameInfoDetailVO.builder().build()
	  : GameInfoDetailVO.builder().gameInfoUuid(s.getGameInfo().getFUuid()).name(s.getFName()).developmentCompany(s.getFDevelopmentCompany()).tag(s.getFTag()).detail(s.getFDetail()).adsPictures(s.getFAdsPictures()).screenshot(s.getFScreenshot()).language(s.getFLanguageNumber()).build();
}

//应用栏目
public static ColumnAppVO_001 _2ColumnAppVO_001(ColumnApp s) {
	return s == null ? ColumnAppVO_001.builder().build() : ColumnAppVO_001.builder().detail(s.getFDetail()).name(s.getFName()).uuid(s.getFUuid()).build();
}

public static List<ColumnAppVO_001> _2ColumnAppVO_001(List<ColumnApp> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2ColumnAppVO_001).collect(Collectors.toList());
}

public static ColumnAppVO_002 _2ColumnAppVO_002(ColumnApp s) {
	return s == null ? ColumnAppVO_002.builder().build() : ColumnAppVO_002.builder().detail(s.getFDetail()).background(s.getFBackground()).languageDefault(s.getFLanguageDefault()).name(s.getFName()).order(s.getFOrder()).uuid(s.getFUuid()).build();
}

public static ColumnAppDetailVO _2VO(ColumnAppDetail s) {
	return s == null ? ColumnAppDetailVO.builder().build() : ColumnAppDetailVO.builder().columnAppUuid(s.getColumnApp().getFUuid()).background(s.getFBackground()).language(s.getFLanguageNumber()).name(s.getFName()).build();
}

public static ColumnAppGameVO _2VO(ColumnAppGame s) {
	return s == null ? ColumnAppGameVO.builder().build()
	  : ColumnAppGameVO.builder().gameDevelopmentCompany(s.getGameInfo().getFDevelopmentCompany()).gameName(s.getGameInfo().getFName()).gameScore(s.getGameInfo().getFScore()).gameVersion(s.getGameInfo().getFVersion()).uuid(s.getFUuid()).build();
}

public static List<ColumnAppGameVO> _2ColumnAppGameVO(List<ColumnAppGame> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2VO).collect(Collectors.toList());
}

//普通栏目
public static ColumnGeneralVO_001 _2ColumnGeneralVO_001(ColumnGeneral s) {
	return s == null ? ColumnGeneralVO_001.builder().build() : ColumnGeneralVO_001.builder().child(s.isFChild()).detail(s.getFDetail()).name(s.getFName()).uuid(s.getFUuid()).build();
}

public static List<ColumnGeneralVO_001> _2ColumnGeneralVO_001(List<ColumnGeneral> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2ColumnGeneralVO_001).collect(Collectors.toList());
}

public static ColumnGeneralVO_002 _2ColumnGeneralVO_002(ColumnGeneral s) {
	return s == null ? ColumnGeneralVO_002.builder().build() : ColumnGeneralVO_002.builder().child(s.isFChild()).detail(s.getFDetail()).background(s.getFBackground()).languageDefault(s.getFLanguageDefault()).name(s.getFName()).order(s.getFOrder()).uuid(s.getFUuid()).build();
}

public static ColumnGeneralDetailVO _2VO(ColumnGeneralDetail s) {
	return s == null ? ColumnGeneralDetailVO.builder().build() : ColumnGeneralDetailVO.builder().columnGeneralUuid(s.getColumnGeneral().getFUuid()).background(s.getFBackground()).language(s.getFLanguageNumber()).name(s.getFName()).build();
}

public static ColumnGeneralGameVO _2VO(ColumnGeneralGame s) {
	return s == null ? ColumnGeneralGameVO.builder().build()
	  : ColumnGeneralGameVO.builder().gameDevelopmentCompany(s.getGameInfo().getFDevelopmentCompany()).gameName(s.getGameInfo().getFName()).gameScore(s.getGameInfo().getFScore()).gameVersion(s.getGameInfo().getFVersion()).uuid(s.getFUuid()).build();
}

public static List<ColumnGeneralGameVO> _2ColumnGeneralGameVO(List<ColumnGeneralGame> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2VO).collect(Collectors.toList());
}

public static ColumnGeneralVO_001 _2VO(ColumnGeneralRelation s) {
	return s == null ? ColumnGeneralVO_001.builder().build() : ColumnGeneralVO_001.builder().uuid(s.getFUuid()).name(s.getChild().getFName()).detail(s.getChild().getFDetail()).build();
}

public static List<ColumnGeneralVO_001> _2ColumnGeneralVO_001_a(List<ColumnGeneralRelation> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2VO).collect(Collectors.toList());
}

//广告
public static AdVO_001 _2AdVO_001(Ad s) {
	return s == null ? AdVO_001.builder().build() : AdVO_001.builder().data(s.getFData()).type(s.getType()).name(s.getFName()).enable(s.isFEnable()).uuid(s.getFUuid()).build();
}

public static List<AdVO_001> _2AdVO_001(List<Ad> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2AdVO_001).collect(Collectors.toList());
}

public static AdVO_002 _2AdVO_002(Ad s) {
	return s == null ? AdVO_002.builder().build() : AdVO_002.builder().type(s.getType()).data(s.getFData()).adPicture(s.getFAdPicture()).detail(s.getFDetail()).languageDefault(s.getFLanguageDefault()).name(s.getFName()).uuid(s.getFUuid()).build();
}

public static AdDetailVO _2VO(AdDetail s) {
	return s == null ? AdDetailVO.builder().build() : AdDetailVO.builder().detail(s.getFDetail()).adPicture(s.getFAdPicture()).language(s.getFLanguageNumber()).adUuid(s.getFUuid()).build();
}

//营销栏目
public static ColumnMarketingVO_001 _2ColumnMarketingVO_001(ColumnMarketing s) {
	return s == null ? ColumnMarketingVO_001.builder().build() : ColumnMarketingVO_001.builder().orderHome(s.getFOrderHome()).type(s.getType()).detail(s.getFDetail()).name(s.getFName()).uuid(s.getFUuid()).build();
}

public static List<ColumnMarketingVO_001> _2ColumnMarketingVO_001(List<ColumnMarketing> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2ColumnMarketingVO_001).collect(Collectors.toList());
}

public static ColumnMarketingVO_002 _2ColumnMarketingVO_002(ColumnMarketing s) {
	return s == null ? ColumnMarketingVO_002.builder().build() : ColumnMarketingVO_002.builder().type(s.getType()).detail(s.getFDetail()).background(s.getFBackground()).languageDefault(s.getFLanguageDefault()).name(s.getFName()).order(s.getFOrder()).uuid(s.getFUuid()).build();
}

public static ColumnMarketingDetailVO _2VO(ColumnMarketingDetail s) {
	return s == null ? ColumnMarketingDetailVO.builder().build() : ColumnMarketingDetailVO.builder().columnMarketingUuid(s.getColumnMarketing().getFUuid()).background(s.getFBackground()).language(s.getFLanguageNumber()).name(s.getFName()).build();
}

public static ColumnMarketingGameVO _2VO(ColumnMarketingGame s) {
	return s == null ? ColumnMarketingGameVO.builder().build()
	  : ColumnMarketingGameVO.builder().gameDevelopmentCompany(s.getGameInfo().getFDevelopmentCompany()).gameName(s.getGameInfo().getFName()).gameScore(s.getGameInfo().getFScore()).gameVersion(s.getGameInfo().getFVersion()).uuid(s.getFUuid()).build();
}

public static List<ColumnMarketingGameVO> _2ColumnMarketingGameVO(List<ColumnMarketingGame> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2VO).collect(Collectors.toList());
}

public static ColumnMarketingAdVO _2VO(ColumnMarketingAd s) {
	return s == null ? ColumnMarketingAdVO.builder().build() : ColumnMarketingAdVO.builder().adName(s.getAd().getFName()).uuid(s.getFUuid()).build();
}

public static List<ColumnMarketingAdVO> _2ColumnMarketingAdVO(List<ColumnMarketingAd> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2VO).collect(Collectors.toList());
}

public static ColumnMarketingColumnVO _2VO(ColumnMarketingColumn s) {
	return s == null ? ColumnMarketingColumnVO.builder().build() : ColumnMarketingColumnVO.builder().adName(s.getColumnGeneral().getFName()).uuid(s.getFUuid()).build();
}

public static List<ColumnMarketingColumnVO> _2ColumnMarketingColumnVO(List<ColumnMarketingColumn> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2VO).collect(Collectors.toList());
}

public static MyGameVO _2VO(MyGame s) {
	return s == null ? MyGameVO.builder().build()
	  : MyGameVO.builder().uuid(s.getFUuid()).score(s.getGameInfo().getFScore()).time(s.getFTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).gameInfoApkName(s.getGameInfo().getFApkName()).gameInfoApkSize(s.getGameInfo().getFApkSize()).gameInfoIcon(s.getGameInfo().getFIcon()).gameInfoName(s.getGameInfo().getFName())
	    .gameInfoUuid(s.getGameInfo().getFUuid()).build();
}

public static List<MyGameVO> _2MyGameVO(List<MyGame> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2VO).collect(Collectors.toList());
}

public static GameStoreAppVO_001 _2VO_001(GameStoreApp s) {
	return s == null ? GameStoreAppVO_001.builder().build() : GameStoreAppVO_001.builder().uuid(s.getFUuid()).createTime(s.getFCreateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).apkSize(s.getFApkSize()).apk(s.getFApk()).detail(s.getFDetail()).name(s.getFName()).version(s.getFVersion()).build();
}

public static List<GameStoreAppVO_001> _2GameStoreAppVO_001(List<GameStoreApp> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2VO_001).collect(Collectors.toList());
}


public static GameStoreAppVO_002 _2VO_002(GameStoreApp s) {
	return s == null ? GameStoreAppVO_002.builder().build() : GameStoreAppVO_002.builder().uuid(s.getFUuid()).version(s.getFVersion()).build();
}

public static List<GameStoreAppVO_002> _2GameStoreAppVO_002(List<GameStoreApp> source) {
	return (source == null || source.isEmpty()) ? Collections.emptyList() : source.stream().map(DomainFactory::_2VO_002).collect(Collectors.toList());
}
}
