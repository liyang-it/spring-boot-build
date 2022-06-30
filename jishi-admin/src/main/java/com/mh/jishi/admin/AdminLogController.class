package com.mh.jishi.admin;

import com.mh.jishi.service.log.AdminLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <h2>日志文件控制层</h2>
 * <p>
 *
 * </p>
 *
 * @author Evan <1922802352@qq.com>
 * @since 2022年06月30日 11:30
 */
@RestController
@RequestMapping(value = "/admin/log")
public class AdminLogController {
    private final AdminLogService service;
    public AdminLogController(AdminLogService service){
        this.service = service;
    }

    @GetMapping(value = "/downLog")
    public void downLog(@RequestParam(defaultValue = "1") Integer type, @RequestParam(defaultValue = "") String nowDate, HttpServletRequest request, HttpServletResponse response){
        service.downLog(type, nowDate, request, response);
    }
}
