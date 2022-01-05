package com.mh.jishi.push;

import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;
import com.mh.jishi.constants.JiGuangConstants;
import com.mh.jishi.service.TSystemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @Author Lizr
 * @Description 极光推送
 * @CreateTime 2021-12-29 下午 4:19
 **/
@Component
public class JiGuangPush {
    private static Logger logger = LoggerFactory.getLogger(JiGuangPush.class);
    private String AppKey;
    private String Secret;
    private JPushClient jPushClient;

    @Autowired
    private TSystemService TSystemService;

    @PostConstruct
    private void init(){
        String  AppKey_ = TSystemService.getValue(JiGuangConstants.AppKey);
        String  Secret_ = TSystemService.getValue(JiGuangConstants.Secret);
        try {
            this.AppKey = AppKey_;
            this.Secret = Secret_;
            this.jPushClient = new JPushClient(Secret_,AppKey_);
        }catch (Exception e){
            logger.error("初始化极光配置失败, AppKey: 【{}】, Secret: 【{}】",AppKey_, Secret_ );
            logger.error("具体原因：", e);
        }
    }
    /**
     * 通知推送
     * 备注：推送方式不为空时，推送的值也不能为空；推送方式为空时，推送值不做要求
     * @param type 推送方式：1、“tag”标签推送，2、“alias”别名推送
     * @param value 推送的标签或别名值
     * @param alert 推送的内容
     */
    public void pushNotice(String type,String value,String alert){
        PushPayload.Builder builder= PushPayload.newBuilder();
        builder.setPlatform(Platform.all());//设置接受的平台，all为所有平台，包括安卓、ios、和微软的
        //设置如果用户不在线、离线消息保存的时间
        Options options= Options.sendno();
        options.setTimeToLive(86400l);    //设置为86400为保存一天，如果不设置默认也是保存一天
        builder.setOptions(options);
        //设置推送方式
        if(type.equals("alias")){
            builder.setAudience(Audience.alias(value));//根据别名推送
        }else if(type.equals("tag")){
            builder.setAudience(Audience.tag(value));//根据标签推送
        }else{
            builder.setAudience(Audience.all());//Audience设置为all，说明采用广播方式推送，所有用户都可以接收到
        }
        //设置为采用通知的方式发送消息
        builder.setNotification(Notification.alert(alert));
        PushPayload pushPayload=builder.build();
        try{
            //进行推送，实际推送就在这一步
            PushResult pushResult= jPushClient.sendPush(pushPayload);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 自定义消息推送
     * 备注：推送方式不为空时，推送的值也不能为空；推送方式为空时，推送值不做要求
     * @param type 推送方式：1、“tag”标签推送，2、“alias”别名推送
     * @param value 推送的标签或别名值
     * @param alert 推送的内容
     */
    public void pushMsg(String type, String value,String alert){
        PushPayload.Builder builder= PushPayload.newBuilder();
        builder.setPlatform(Platform.all());//设置接受的平台
        if(type.equals("alias")){
            builder.setAudience(Audience.alias(value));//别名推送
        }else if(type.equals("tag")){
            builder.setAudience(Audience.tag(value));//标签推送
        }else{
            builder.setAudience(Audience.all());//Audience设置为all，说明采用广播方式推送，所有用户都可以接收到
        }
        Message.Builder newBuilder=Message.newBuilder();
        newBuilder.setMsgContent(alert);//消息内容
        Message message=newBuilder.build();
        builder.setMessage(message);
        PushPayload pushPayload=builder.build();
        try{
            PushResult pushResult=jPushClient.sendPush(pushPayload);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
