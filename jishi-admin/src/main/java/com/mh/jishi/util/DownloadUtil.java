package com.mh.jishi.util;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.Assert;

import com.mh.jishi.config.ServiceException;
import com.mh.jishi.entity.TStorage;
import com.mh.jishi.storage.LocalStorage;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <h2>下载文件工具类</h2>
 * <p>
 *
 * </p>
 *
 * @author 作者<1922802352@qq.com>
 * @since 2022年07月11日 上午11:35:00
 **/
@Slf4j
public class DownloadUtil {
	/**
	 * 下载文件，这个是下载本地文件
	 * 
	 * @param fileName 文件名称
	 * @param file     文件对象
	 * @param isDel    操作完是否删除文件, true 是
	 */
	public static void outFileByFile(String fileName, File file, Boolean isDel, HttpServletRequest request,
			HttpServletResponse response) {
		Assert.notNull(file, "文件不能为空");
		Assert.isTrue(file.exists(), "文件不存在");

		try {
			/**
			 * 中文乱码解决
			 */
			String type = request.getHeader("User-Agent").toLowerCase();
			String charsetCode = "UTF-8";
			if (type.indexOf("firefox") > 0 || type.indexOf("chrome") > 0) {
				/**
				 * 谷歌或火狐
				 */
				fileName = new String(fileName.getBytes(charsetCode), "iso8859-1");
			} else {
				/**
				 * IE
				 */
				fileName = URLEncoder.encode(fileName, charsetCode);
			}
			// 设置响应的头部信息
			response.setHeader("content-disposition", "attachment;filename=" + fileName);
			// 设置响应内容的类型
			MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();

			String mimeType = fileTypeMap.getContentType(file.getName());
			response.setContentType(mimeType + "; charset=" + charsetCode);
			response.setContentLength((int) file.length());
			// 设置响应内容的长度
			response.setHeader("file-length", String.valueOf(file.length()));
			// 通过文件管道下载
			WritableByteChannel writableByteChannel = Channels.newChannel(response.getOutputStream());
			FileChannel fileChannel = new FileInputStream(file.getAbsolutePath()).getChannel();
			fileChannel.transferTo(0, fileChannel.size(), writableByteChannel);
			log.info("输出文件成功");
			fileChannel.close();
			writableByteChannel.close();
		} catch (Exception e) {
			log.error("输出文件异常, 异常原因: ", e);
			throw new ServiceException(e.getMessage());
		} finally {
			ThreadUtil.execAsync(new Runnable() {
				@Override
				public void run() {
					log.info("输出文件，是否删除本地文件：【{}】", isDel);
					if (null != isDel) {
						if(isDel) {
							if(file.exists()) {
								boolean isSuccessDel = file.delete();
								log.info("输出文件，删除本地文件是否成功：【{}】", isSuccessDel);
							}
						}
					}
				}
			});

		}

	}

}
