package com.mh.jishi.util;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * <h2>路径操作工具类</h2>
 * <p>
 *
 * </p>
 *
 * @author 作者<1922802352@qq.com>
 * @since 2022年07月14日 上午10:51:34
 **/
public class PathUtil {

	/**
	 * 获取 jishi-start -> Resource资源 目录下的文件
	 * 
	 * @param filePath 文件在 resource下的路径 例如: 要获取 resource/static/a.txt 文件, filePath =
	 *                 /static/a.txt
	 * @throws IOException
	 */
	public static File getResourceFile(String filePath) throws IOException {
		// 获取输入流
		Resource resource = new ClassPathResource(filePath);
		InputStream inputStream = resource.getInputStream();
		/**
		 * 输出文件的路径
		 * 
		 * @filed outFilePath 当前 程序jar路径, 比如当前 程序jar 在D:\my\jar, 那么路径就是 D:\my\jar +
		 *        在拼接上文件名称
		 */
		String outFilePath = (new File(".")).getCanonicalPath();
		// 文件名称 
		String outFileName = String.format("/tmp-files/tmp-file-%s", System.currentTimeMillis());
		File file = new File(outFilePath.concat(outFileName));
		// 将inputStream 流复制输出到文件， 后续操作完文件，记得删除这个文件，避免时间久了产生太多没必要的文件， 也可以写个定时器 定时删除 过期文件
		FileUtils.copyInputStreamToFile(inputStream, file);
		inputStream.close();
		return file;

	}

}
