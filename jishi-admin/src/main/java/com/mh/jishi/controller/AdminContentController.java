package com.mh.jishi.acontroller;

import com.mh.jishi.annotation.RequiresPermissionsDesc;
import com.mh.jishi.entity.TPageContent;
import com.mh.jishi.mapper.TPageContentMapper;
import com.mh.jishi.util.ResponseUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @Author Lizr
 * @Description 内容管理
 * @CreateTime 2021-11-15 上午 11:40
 **/
@RestController
@RequestMapping(value = "/admin/content")
public class AdminContentController {
    @Resource
    private TPageContentMapper LitemallPageContentMapper;


    @RequiresPermissions("admin:content:page")
    @RequiresPermissionsDesc(menu = {"内容管理", "页面内容"}, button = "查询")
    @GetMapping("/queryPageContent")
    public ResponseUtil queryPageContent(){
        return ResponseUtil.ok(LitemallPageContentMapper.selectList(null));
    }

    @RequiresPermissions("admin:content:page")
    @RequiresPermissionsDesc(menu = {"内容管理", "页面内容"}, button = "修改")
    @PostMapping("/updPageContent")
    @Transactional(rollbackFor = Exception.class)
    public Object updPageContent(@RequestBody @Valid TPageContent content){
        TPageContent query = LitemallPageContentMapper.selectById(content.getId());
        if(query == null){
            ResponseUtil.fail(401, "內容不存在");
        }
        query.setStatus(content.getStatus());
        query.setContent(content.getContent());
        LitemallPageContentMapper.updateById(query);
        return ResponseUtil.ok();
    }
}
