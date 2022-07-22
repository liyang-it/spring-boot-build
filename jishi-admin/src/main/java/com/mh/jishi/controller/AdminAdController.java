package com.mh.jishi.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mh.jishi.annotation.RequiresPermissionsDesc;
import com.mh.jishi.entity.TAd;
import com.mh.jishi.service.TAdService;
import com.mh.jishi.util.ResponseUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * 轮播图控制层
 */
@RestController
@RequestMapping("/admin/ad")
@Validated
public class AdminAdController {
    private final Log logger = LogFactory.getLog(AdminAdController.class);

    @Autowired
    private TAdService adService;

    @RequiresPermissions("admin:ad:list")
    @RequiresPermissionsDesc(menu = {"广告管理", "广告管理"}, button = "查询")
    @GetMapping("/list")
    public ResponseUtil list(String name, String content,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(defaultValue = "add_time") String sort,
                             @RequestParam(defaultValue = "desc") String order) {
        IPage<TAd> adList = adService.querySelective(name, content, page, limit, sort, order);
        return ResponseUtil.ok(adList);
    }

    private Object validate(TAd ad) {
        String name = ad.getName();
        if (StringUtils.isEmpty(name)) {
            return ResponseUtil.fail("轮播图名称");
        }
        String url = ad.getUrl();
        if (StringUtils.isEmpty(url)) {
            return ResponseUtil.fail("轮播图链接");
        }

        return null;
    }

    @RequiresPermissions("admin:ad:create")
    @RequiresPermissionsDesc(menu = {"推广管理", "广告管理"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody TAd ad) {
        Object error = validate(ad);
        if (error != null) {
            return error;
        }
        adService.add(ad);
        return ResponseUtil.ok(ad);
    }

    @RequiresPermissions("admin:ad:read")
    @RequiresPermissionsDesc(menu = {"广告管理", "广告管理"}, button = "详情")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        TAd ad = adService.getById(id);
        return ResponseUtil.ok(ad);
    }

    @RequiresPermissions("admin:ad:update")
    @RequiresPermissionsDesc(menu = {"广告管理", "广告管理"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody TAd ad) {
        Object error = validate(ad);
        if (error != null) {
            return error;
        }
        if (adService.updateByIdAndDate(ad) == false) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok(ad);
    }

    @RequiresPermissions("admin:ad:delete")
    @RequiresPermissionsDesc(menu = {"广告管理", "广告管理"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody TAd ad) {
        Integer id = ad.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        adService.deleteByIdAndDate(id);
        return ResponseUtil.ok();
    }

}
