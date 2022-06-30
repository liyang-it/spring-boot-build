package com.mh.jishi.service.log;

import com.mh.jishi.config.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.time.LocalDate;

/**
 * <h2>日志服务层</h2>
 * <p>
 *
 * </p>
 *
 * @author Evan <1922802352@qq.com>
 * @since 2022年06月30日 11:46
 */
@Service
public class AdminLogService {
    private final String logErrorPre;
    private final String logErrorName;
    private final String logInfoPre;
    private final String logInfolName;

    public AdminLogService() {
        this.logErrorPre = "logs/error%s.log";
        this.logInfoPre = "logs/log%s.log";
        this.logErrorName = "异常日志文件-%s.text";
        this.logInfolName = "正常日志文件-%s.text";
    }

    /**
     * 下载正常日志文件
     * @param logType 1 正常日志文件， 2 异常日志文件
     * @param nowDate 日期 yyyy-MM-dd 格式, 如果为空 则返回当天的日志文件
     */
    public void downLog(Integer logType, String nowDate, HttpServletRequest request, HttpServletResponse response) {
        // 文件名
        String fileName = null;
        String path = null;
        String nowDateStr = null;
        if(StringUtils.isNotBlank(nowDate)){
            nowDateStr = nowDate;
            nowDate = "-" + nowDate;
        }else{
            nowDate = "";
            nowDateStr = LocalDate.now().toString();
        }

        switch (logType){
            case 1:
                path = String.format(logInfoPre, nowDate);
                fileName = String.format(logInfolName, nowDateStr);
                break;
            case 2:
                path = String.format(logErrorPre, nowDate);
                fileName = String.format(logErrorName, nowDateStr);
                break;
            default:
                throw new ServiceException("不支持的日志类型");
        }
        // 文件对象
        File file = new File(path);
        if (!file.exists()) {
            throw new ServiceException("日志文件不存在");
        }
        /**
         * 中文乱码解决
         */
        String type = request.getHeader("User-Agent").toLowerCase();

        String charsetCode = "UTF-8";
        try {
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
            response.setContentType("text/plain; charset=" + charsetCode);
            response.setContentLength((int) file.length());
            // 设置响应内容的长度
            response.setHeader("file-length", String.valueOf(file.length()));
            //通过文件管道下载输出
            WritableByteChannel writableByteChannel = Channels.newChannel(response.getOutputStream());
            FileChannel fileChannel = new FileInputStream(file.getAbsolutePath()).getChannel();
            fileChannel.transferTo(0, fileChannel.size(), writableByteChannel);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }

    }

    /**
     * 输出正常日志文件
     *
     * @param nowDate 日期 yyyy-MM-dd 格式
     */
    public void outInfoLog(String nowDate) {

    }

}
