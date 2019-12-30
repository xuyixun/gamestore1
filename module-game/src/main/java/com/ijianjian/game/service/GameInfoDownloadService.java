package com.ijianjian.game.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.annotations.ApiOperation;

@Controller
public class GameInfoDownloadService {
@ApiOperation("下载")
@GetMapping("v1/game_info/download")
public void download(String filePath, String fileName, HttpServletRequest request, HttpServletResponse response) {
	DownloadService.download(filePath, fileName, request, response);
}
}
