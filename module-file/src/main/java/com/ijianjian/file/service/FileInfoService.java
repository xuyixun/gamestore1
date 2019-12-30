package com.ijianjian.file.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ijianjian.core.common.constant.Config;
import com.ijianjian.core.common.constant.ResultType;
import com.ijianjian.core.common.object.CommonResult;
import com.ijianjian.core.domain.util.FileInfoUpload;
import com.ijianjian.file.domain.po.FileInfo;
import com.ijianjian.file.domain.repository.FileInfoRepository;
import com.ijianjian.file.util.FieldConstant.FileType;
import com.ijianjian.file.util.LocalUser;

@RestController
public class FileInfoService implements LocalUser {
private final FileInfoRepository fileInfoRepository;

public FileInfoService(FileInfoRepository fileInfoRepository) {
	this.fileInfoRepository = fileInfoRepository;
}

public FileInfoUpload upload1(List<MultipartFile> files, FileType type) {
	String path = "";
	Long size = 0L;
	String fileName = "";
	switch (type) {
	case game_apk:
		path = Config.FileConfig.uploadPahtApk + "/apk";
		break;
	case game_icon:
		path = Config.FileConfig.uploadPahtIcon + "/icon";
		break;
	case column_app_background:
	case column_general_background:
		path = Config.FileConfig.uploadPahtIcon + "/background";
		break;
	default:
		path = Config.FileConfig.uploadPahtOther + "/other";
	}
	path += "/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	File dir = new File(path);
	if (!dir.exists()) {
		if (!dir.mkdirs()) {
			return null;
		}
	}
	String filePath = "";
	for (MultipartFile file : files) {
		Matcher m = Pattern.compile("(.+)\\.(.+)").matcher(file.getOriginalFilename());
		String ext = "";
		if (m.matches()) {
			ext = m.group(2);
		}
		String newFileName = System.currentTimeMillis() + "-" + Math.abs(new Random().nextInt()) + "." + ext.toLowerCase();

		try {
			// 保存文件
			// file.transferTo(new File(dir, newFileName));
			size = file.getSize();
			fileName = file.getOriginalFilename();
			byte[] bytes = file.getBytes();
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(dir, newFileName)));
			stream.write(bytes);
			stream.close();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		this.fileInfoRepository.save(FileInfo.builder().fSize(size).createUser(localCore()).fContentType(file.getContentType()).fExt(ext).fFileName(newFileName).fFileNameOriginal(file.getOriginalFilename()).fPath(path).type(type).build());
		filePath += path + "/" + newFileName + ",";
	}

	FileInfoUpload fileInfo = new FileInfoUpload();
	fileInfo.setFilePath(filePath);
	fileInfo.setFileName(fileName);
	fileInfo.setFileSize(size);
	return fileInfo;
}

@PostMapping("v1/file/apk")
public CommonResult uploadApk(MultipartFile file, FileType type) {
    List<MultipartFile> files = Lists.newArrayList();
    files.add(file);
    return upload(files, type);
}

@PostMapping("v1/file")
public CommonResult upload(List<MultipartFile> files, FileType type) {
	String path = "";
	Long size = 0L;
	String fileName = "";
	if (type == null) {
		type = FileType.other;
	}
	switch (type) {
	case game_apk:
		path = Config.FileConfig.uploadPahtApk + "/apk";
		break;
	case game_icon:
		path = Config.FileConfig.uploadPahtIcon + "/icon";
		break;
	case column_app_background:
	case column_general_background:
		path = Config.FileConfig.uploadPahtIcon + "/background";
		break;
	default:
		path = Config.FileConfig.uploadPahtOther + "/other";
	}
	path += "/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	File dir = new File(path);
	if (!dir.exists()) {
		if (!dir.mkdirs()) {
			return CommonResult.errorResult(ResultType.FileError.create_file_error);
		}
	}
	String filePath = "";
	for (MultipartFile file : files) {
		Matcher m = Pattern.compile("(.+)\\.(.+)").matcher(file.getOriginalFilename());
		String ext = "";
		if (m.matches()) {
			ext = m.group(2);
		}
		String newFileName = System.currentTimeMillis() + "-" + Math.abs(new Random().nextInt()) + "." + ext.toLowerCase();

		try {
			// 保存文件
			// file.transferTo(new File(dir, newFileName));
			size = file.getSize();
			fileName = file.getOriginalFilename();
			byte[] bytes = file.getBytes();
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(dir, newFileName)));
			stream.write(bytes);
			stream.close();
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		this.fileInfoRepository.save(FileInfo.builder().fSize(size).createUser(localCore()).fContentType(file.getContentType()).fExt(ext).fFileName(newFileName).fFileNameOriginal(file.getOriginalFilename()).fPath(path).type(type).build());
		filePath += path + "/" + newFileName + ",";
	}
	FileInfoUpload fileInfo = new FileInfoUpload();
	fileInfo.setFilePath(filePath);
	fileInfo.setFileName(fileName);
	fileInfo.setFileSize(size);
	return CommonResult.successResult(fileInfo);
}

public String fileName(String path) {
	return this.fileInfoRepository.findName(path);
}
}
