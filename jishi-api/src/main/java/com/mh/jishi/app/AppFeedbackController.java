package com.mh.jishi.app;

import com.mh.jishi.annotation.ApiDesc;
import com.mh.jishi.annotation.LoginUser;
import com.mh.jishi.entity.TFeedback;
import com.mh.jishi.entity.TUser;
import com.mh.jishi.service.TFeebackService;
import com.mh.jishi.service.TUserService;
import com.mh.jishi.util.ResponseUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 意见反馈服务
 */
@RestController
@RequestMapping("/api/feedback")
@Validated
public class AppFeedbackController {
    private final Log logger = LogFactory.getLog(AppFeedbackController.class);

    @Autowired
    private TFeebackService feedbackService;
    @Autowired
    private TUserService userService;

    private Object validate(TFeedback feedback) {
        String content = feedback.getContent();
        if (StringUtils.isEmpty(content)) {
            return ResponseUtil.business( "请输入反馈內容");
        }

        String type = feedback.getFeedType();
        if (StringUtils.isEmpty(type)) {
            return ResponseUtil.business( "请输入反馈內容");
        }
        return null;
    }

    /**
     * 添加意见反馈
     *
     * @param userId   用户ID
     * @param feedback 意见反馈
     * @return 操作结果
     */
    @ApiDesc(methodDesc = "用户提交意见反馈信息")
    @PostMapping("submit")
    public Object submit(@LoginUser Integer userId, @RequestBody TFeedback feedback) {

        Object error = validate(feedback);
        if (error != null) {
            return error;
        }
        if(userId != null){
            TUser user = userService.getById(userId);
            String username = user.getUsername();
            feedback.setId(null);
            feedback.setUserId(userId);
            feedback.setUsername(username);
        }
        feedback.setAddTime(LocalDateTime.now());
        //状态默认是0，1表示状态已发生变化
        feedbackService.save(feedback);

        return ResponseUtil.ok();
    }
}
