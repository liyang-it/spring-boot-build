package com.mh.jishi.system;

import com.mh.jishi.entity.TPageContent;
import com.mh.jishi.mapper.TPageContentMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author Lizr
 * @Description 初始化页面内容
 * @CreateTime 2021-11-15 上午 10:19
 **/
@Component
public class SystemInitPageContent {
    @Resource
    private TPageContentMapper LitemallPageContentMapper;

    public static Map<String, String> defaultContentMap = new HashMap<>();

    static{
        defaultContentMap.put("yhxy", "用戶協議");
        defaultContentMap.put("yszc", "隱私政策");
        defaultContentMap.put("ysxy", "預售協議");
        defaultContentMap.put("cjwfgz", "抽獎玩法規則");
        defaultContentMap.put("gywm", "關於我們");
    }
    @PostConstruct
    private void init(){
            defaultContentMap.forEach((key, value)->{
                int c = LitemallPageContentMapper.queryDefaultKey(key);
                if(c == 0){
                    TPageContent content = new TPageContent();
                    content.setKeyword(key);
                    content.setLabel(value);
                    content.setStatus(1);
                    content.setContent("-");
                    LitemallPageContentMapper.insert(content);
                }
            });
    }

}
