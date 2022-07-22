package com.mh.jishi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mh.jishi.annotation.RequiresPermissionsDesc;
import com.mh.jishi.entity.TFeedback;
import com.mh.jishi.mapper.TFeedbackMapper;
import com.mh.jishi.util.ResponseUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @desc 用户管理 - 反馈管理
 * @author lizr
 * @date 2021-11-15 4:14:32
 */
@RestController
@RequestMapping("/admin/feedback")
@Validated
public class AdminFeedbackController {
    private final Logger logger = LoggerFactory.getLogger(AdminFeedbackController.class);

    @Resource
    private TFeedbackMapper feedbackMapper;

    @RequiresPermissions("admin:feedback:list")
    @RequiresPermissionsDesc(menu = {"用户管理", "意见反馈"}, button = "查询")
    @GetMapping("/list")
    public ResponseUtil list(String userInfo,
                       @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
                       @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        IPage<TFeedback> iPage = new Page<>(page, limit);
        iPage = feedbackMapper.pageQuery(iPage, startTime, endTime, userInfo);
        return ResponseUtil.ok(iPage);
    }
}
