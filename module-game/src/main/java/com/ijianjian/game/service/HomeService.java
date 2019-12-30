package com.ijianjian.game.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ijianjian.core.common.object.CommonResult;
import com.ijianjian.game.domain.po.Ad;
import com.ijianjian.game.domain.po.AdDetail;
import com.ijianjian.game.domain.po.ColumnGeneral;
import com.ijianjian.game.domain.po.ColumnGeneralDetail;
import com.ijianjian.game.domain.po.ColumnMarketing;
import com.ijianjian.game.domain.po.ColumnMarketingAd;
import com.ijianjian.game.domain.po.ColumnMarketingColumn;
import com.ijianjian.game.domain.po.ColumnMarketingDetail;
import com.ijianjian.game.domain.po.ColumnMarketingGame;
import com.ijianjian.game.domain.po.GameInfo;
import com.ijianjian.game.domain.po.GameInfoDetail;
import com.ijianjian.game.domain.repository.AdDetailRepository;
import com.ijianjian.game.domain.repository.ColumnGeneralDetailRepository;
import com.ijianjian.game.domain.repository.ColumnMarketingAdRepository;
import com.ijianjian.game.domain.repository.ColumnMarketingColumnRepository;
import com.ijianjian.game.domain.repository.ColumnMarketingDetailRepository;
import com.ijianjian.game.domain.repository.ColumnMarketingGameRepository;
import com.ijianjian.game.domain.repository.ColumnMarketingRepository;
import com.ijianjian.game.domain.repository.GameInfoDetailRepository;
import com.ijianjian.game.util.FieldConstant.GameInfoStatus;

import io.swagger.annotations.Api;

@Api(tags = "主页")
@RestController
public class HomeService {
private final ColumnMarketingRepository columnMarketingRepository;
private final ColumnMarketingDetailRepository columnMarketingDetailRepository;
private final ColumnMarketingAdRepository columnMarketingAdRepository;
private final ColumnMarketingColumnRepository columnMarketingColumnRepository;
private final ColumnMarketingGameRepository columnMarketingGameRepository;
private final AdDetailRepository adDetailRepository;
private final GameInfoDetailRepository gameInfoDetailRepository;
private final ColumnGeneralDetailRepository columnGeneralDetailRepository;

public HomeService(ColumnMarketingRepository columnMarketingRepository, ColumnMarketingDetailRepository columnMarketingDetailRepository, ColumnMarketingAdRepository columnMarketingAdRepository, ColumnMarketingColumnRepository columnMarketingColumnRepository,
  ColumnMarketingGameRepository columnMarketingGameRepository, AdDetailRepository adDetailRepository, GameInfoDetailRepository gameInfoDetailRepository, ColumnGeneralDetailRepository columnGeneralDetailRepository) {
	this.columnMarketingRepository = columnMarketingRepository;
	this.columnMarketingDetailRepository = columnMarketingDetailRepository;
	this.columnMarketingAdRepository = columnMarketingAdRepository;
	this.columnMarketingColumnRepository = columnMarketingColumnRepository;
	this.columnMarketingGameRepository = columnMarketingGameRepository;
	this.adDetailRepository = adDetailRepository;
	this.gameInfoDetailRepository = gameInfoDetailRepository;
	this.columnGeneralDetailRepository = columnGeneralDetailRepository;
}

//@Cacheable(cacheNames = "home", key = "#language")
@GetMapping("v1/home/{language}")
public CommonResult homePage(@PathVariable String language) {
	List<Map<String, Object>> list001 = Lists.newArrayList();
	List<ColumnMarketing> cmList = this.columnMarketingRepository.findByFDeletedFalseAndFOrderHomeNot(0, Sort.by("fOrderHome"));
	cmList.forEach(a -> {
		Map<String, Object> cmMap = Maps.newHashMap();
		if (!a.getFLanguageDefault().equals(language)) {
			Optional<ColumnMarketingDetail> optional = this.columnMarketingDetailRepository.findByColumnMarketing_fUuidAndFLanguageNumber(a.getFUuid(), language);
			if (optional.isPresent()) {
				a.setFName(optional.get().getFName());
				a.setFBackground(optional.get().getFBackground());
			}
		}
		cmMap.put("type", a.getType());
		cmMap.put("name", a.getFName());
		cmMap.put("icon", a.getFIcon());
		cmMap.put("background", a.getFBackground());
		cmMap.put("detail", a.getFDetail());
		switch (a.getType()) {
		case ad:
			cmMap.put("ad", this.ad(language, a.getFUuid()));
			break;
		case column:
			List<ColumnMarketingColumn> listColumn = this.columnMarketingColumnRepository.findByColumnMarketing_fUuid(a.getFUuid(), Sort.by("fOrder"));
			listColumn.size();
			break;
		case game:
			cmMap.put("game", this.game(language, a.getFUuid()));
			break;
		}
		cmMap.put("uuid", a.getFUuid());
		list001.add(cmMap);
	});

	return CommonResult.successResult(list001);
}

private List<Map<String, Object>> ad(String language, String uuid) {
	List<ColumnMarketingAd> listAd = this.columnMarketingAdRepository.findByColumnMarketing_fUuid(uuid, Sort.by("fOrder"));
	List<Map<String, Object>> list = Lists.newArrayList();
	listAd.forEach(a -> {
		Ad ad = a.getAd();
		Map<String, Object> map = Maps.newHashMap();
		if (!ad.getFLanguageDefault().equals(language)) {
			Optional<AdDetail> optional = this.adDetailRepository.findByAd_fUuidAndFLanguageNumber(ad.getFUuid(), language);
			if (optional.isPresent()) {
				ad.setFAdPicture(optional.get().getFAdPicture());
				ad.setFDetail(optional.get().getFDetail());
			}
		}
		map.put("type", ad.getType());
		map.put("adPicture", ad.getFAdPicture());
		map.put("detail", ad.getFDetail());
		switch (ad.getType()) {
		case lan_mu:
			Map<String, Object> map001 = Maps.newHashMap();
			ColumnGeneral columnGeneral = ad.getColumnGeneral();
			if (!columnGeneral.getFLanguageDefault().equals(language)) {
				Optional<ColumnGeneralDetail> optional = this.columnGeneralDetailRepository.findByColumnGeneral_fUuidAndFLanguageNumber(columnGeneral.getFUuid(), language);
				if (optional.isPresent()) {
					columnGeneral.setFName(optional.get().getFName());
					columnGeneral.setFBackground(optional.get().getFBackground());
				}
			}
			map001.put("uuid", columnGeneral.getFUuid());
			map001.put("name", columnGeneral.getFName());
			map001.put("icon", columnGeneral.getFIcon());
			map001.put("background", columnGeneral.getFBackground());
			map001.put("detail", columnGeneral.getFDetail());
			map.put("column", map001);
			break;
		case ying_yong:
			GameInfo gameInfo = ad.getGameInfo();
			map.put("game", gameInfo.getStatus() == GameInfoStatus.shang_xian ? this.game(gameInfo, language) : null);
			break;
		default:
			map.put("data", ad.getFData());
			break;
		}
		list.add(map);
	});
	return list;
}

private List<Map<String, Object>> game(String language, String uuid) {
	List<ColumnMarketingGame> listGame = this.columnMarketingGameRepository.findTop15ByColumnMarketing_fUuid(uuid, Sort.by("fOrder"));
	List<Map<String, Object>> list = Lists.newArrayList();
	listGame.forEach(a -> {
		GameInfo gameInfo = a.getGameInfo();
		if (gameInfo.getStatus() == GameInfoStatus.shang_xian) {
			list.add(this.game(gameInfo, language));
		}
	});
	return list;
}

private Map<String, Object> game(GameInfo gameInfo, String language) {
	Map<String, Object> map = Maps.newHashMap();
	if (!gameInfo.getFLanguageDefault().equals(language)) {
		Optional<GameInfoDetail> optional = this.gameInfoDetailRepository.findByGameInfo_fUuidAndFLanguageNumber(gameInfo.getFUuid(), language);
		if (optional.isPresent()) {
			gameInfo.setFName(optional.get().getFName());
		}
	}
	map.put("uuid", gameInfo.getFUuid());
	map.put("name", gameInfo.getFName());
	map.put("icon", gameInfo.getFIcon());
	map.put("score", gameInfo.getFScore());
	map.put("apk_size", gameInfo.getFApkSize());
	map.put("apk_name", gameInfo.getFApkName());
	map.put("chargeType", gameInfo.getChargeType());
	map.put("download_count", gameInfo.getFDownloadCount());
	return map;
}
}
