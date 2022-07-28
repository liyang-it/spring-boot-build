package com.mh.jishi.ucontroller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mh.jishi.entity.TPageContent;
import com.mh.jishi.mapper.TIssueMapper;
import com.mh.jishi.mapper.TPageContentMapper;
import com.mh.jishi.service.TIssueService;
import com.mh.jishi.util.ResponseUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @Author Lizr
 * @Description 內容管理
 * @CreateTime 2021-11-15 下午 1:35
 **/
@RestController
@RequestMapping(value = "/api/content")
public class AppPageContentController {

    @Resource
    private TPageContentMapper LitemallPageContentMapper;

    @Resource
    private TIssueService LitemallIssueService;

    @Resource
    private TIssueMapper LitemallIssueMapper;

    /**
     * 获取页面内容
     * @param keyword
     * @return
     */
    @GetMapping("/getPageContent")
    public Object getPageContent(@RequestParam("keyword") String keyword){
//        if(!SystemInitPageContent.defaultContentMap.containsKey(keyword)){
//            return ResponseUtil.fail(401, "该内容不存在！");
//        }
//        if("yszc".equals(keyword) || "yhxy".equals(keyword)){
//            return LitemallPageContentMapper.selectOne(new QueryWrapper<LitemallPageContent>().select("content").eq("keyword", keyword).eq("status", 1)).getContent();
//        }else{
//            return ResponseUtil.ok(LitemallPageContentMapper.selectOne(new QueryWrapper<LitemallPageContent>().eq("keyword", keyword).eq("status", 1)));
//        }
        return ResponseUtil.ok(LitemallPageContentMapper.selectOne(new QueryWrapper<TPageContent>().select("content").eq("keyword", keyword).eq("status", 1)).getContent());
    }

    /**
     * 帮助中心
     */
    @GetMapping("/getIssueList")
    public Object pageQueryIssue(String keyword, @RequestParam(value = "indexPage", defaultValue = "1") Integer indexPage, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        IPage<Map<String, String>> iPage = new Page<>(indexPage, pageSize);
        return ResponseUtil.ok(LitemallIssueMapper.pageQueryIssue(iPage, keyword));
    }
}
