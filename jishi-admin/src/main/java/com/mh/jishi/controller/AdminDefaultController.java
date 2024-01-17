package com.mh.jishi.controller;

import com.mh.jishi.util.IpUtil;
import com.mh.jishi.util.ResponseUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 轮播图控制层
 */
@RestController
public class AdminDefaultController {
   private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
   @RequestMapping(value = "/")
//   @CurrentLimitingRequest
    public ResponseUtil index(HttpServletRequest request){
       String hello = String.format("当前时间：%s, 访问客户端本机IP：%s, 访问客户端外网IP：%s", formatter.format(LocalDateTime.now()), IpUtil.getIpAddr(request), IpUtil.getV4OrV6IP());
       return ResponseUtil.ok(hello);
   }

}
