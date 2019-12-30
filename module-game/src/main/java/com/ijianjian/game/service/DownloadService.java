package com.ijianjian.game.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DownloadService {
public static void download(String filePath, String fileName, HttpServletRequest request, HttpServletResponse response) {
	File file = new File(filePath);
	long downloadSize = file.length();
	response.setHeader("Accept-Ranges", "bytes");
	try {
		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
	} catch (UnsupportedEncodingException e) {
		e.printStackTrace();
	}
	String mimeType = request.getServletContext().getMimeType(filePath);
	if (mimeType == null) {
		// set to binary type if MIME mapping not found
		mimeType = "application/octet-stream";
	}
	try {
		response.setContentType(Files.probeContentType(file.toPath()));
	} catch (IOException e) {
		e.printStackTrace();
	}

	long startByte = 0, endByte = 0;
	String range = request.getHeader("Range");
	if (range != null && range.contains("bytes=") && range.contains("-")) {
		String bytes = range.replaceAll("bytes=", "");
		String[] ranges = bytes.split("-");
		if (ranges.length == 1) {
			// 判断range的类型
			if (range.startsWith("-")) {
				// 类型一：bytes=-2343
				endByte = Long.parseLong(ranges[0]);
			} else if (range.endsWith("-")) {
				// 类型二：bytes=2343-
				startByte = Long.parseLong(ranges[0]);
			}
		} else if (ranges.length == 2) {
			// 类型三：bytes=22-2343
			startByte = Long.parseLong(ranges[0]);
			endByte = Long.parseLong(ranges[1]);
		}

		downloadSize = (endByte > startByte) ? (endByte - startByte) : (downloadSize - startByte);
		// 若客户端传来Range，说明之前下载了一部分，设置206状态(SC_PARTIAL_CONTENT)
		response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
	}
	response.setHeader("Content-Length", String.valueOf(downloadSize));

	OutputStream outputStream = null;
	RandomAccessFile randomAccessFile = null;
	// 已传送数据大小
	long transmitted = 0;
	try {
		randomAccessFile = new RandomAccessFile(file, "r");
		outputStream = new BufferedOutputStream(response.getOutputStream());// 设置下载起始位置
		if (startByte > 0) {
			randomAccessFile.seek(startByte);
		}

		byte[] buff = new byte[4096];
		int len = 0;
		while ((transmitted + len) <= downloadSize && (len = randomAccessFile.read(buff)) != -1) {
			outputStream.write(buff, 0, len);
			transmitted += len;
			// 停一下，方便测试，用的时候删了就行了
			// Thread.sleep(10);
		}
		// 处理不足buff.length部分
		if (transmitted < downloadSize) {
			len = randomAccessFile.read(buff, 0, (int) (downloadSize - transmitted));
			outputStream.write(buff, 0, len);
			transmitted += len;
		}
		response.flushBuffer();

		System.out.println("下载完毕：" + startByte + "-" + endByte + "：" + transmitted);
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} finally {
		try {
			if (null != outputStream)
				outputStream.flush();
			if (null != outputStream)
				outputStream.close();
			if (null != randomAccessFile)
				randomAccessFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
}
