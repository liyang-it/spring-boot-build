package com.mh.jishi.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mh.jishi.annotation.RequiresPermissionsDesc;
import com.mh.jishi.entity.TIssue;
import com.mh.jishi.mapper.TIssueMapper;
import com.mh.jishi.service.TIssueService;
import com.mh.jishi.util.ResponseUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 商场管理 - 商场管理
 */
@RestController
@RequestMapping("/admin/issue")
@Validated
public class AdminIssueController {
    private final Log logger = LogFactory.getLog(AdminIssueController.class);

    @Autowired
    private TIssueService issueService;
    @Resource
    private TIssueMapper IssueMapper;

    @RequiresPermissions("admin:issue:list")
    @RequiresPermissionsDesc(menu = {"内容管理", "帮助中心"}, button = "查询")
    @GetMapping("/list")
    public ResponseUtil list(String question,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @RequestParam(defaultValue = "add_time") String sort,
                       @RequestParam(defaultValue = "desc") String order) {
        IPage<TIssue> iPage = new Page<>(page, limit);
        QueryWrapper<TIssue> queryWrapper = new QueryWrapper<>();
        if("desc".equals(order)){
            queryWrapper.orderByDesc(sort);
        }else{
            queryWrapper.orderByAsc(sort);
        }
        queryWrapper.eq("deleted", 0);
        iPage = IssueMapper.selectPage(iPage, queryWrapper);
        return ResponseUtil.ok(iPage);
    }

    private Object validate(TIssue issue) {
        String question = issue.getQuestion();
        if (StringUtils.isEmpty(question)) {
            return ResponseUtil.badArgument();
        }
        String answer = issue.getAnswer();
        if (StringUtils.isEmpty(answer)) {
            return ResponseUtil.badArgument();
        }
        return null;
    }

    @RequiresPermissions("admin:issue:create")
    @RequiresPermissionsDesc(menu = {"内容管理", "帮助中心"}, button = "添加")
    @PostMapping("/create")
    public Object create(@RequestBody TIssue issue) {
        Object error = validate(issue);
        if (error != null) {
            return error;
        }
        issueService.add(issue);
        return ResponseUtil.ok(issue);
    }

    @RequiresPermissions("admin:issue:read")
    @GetMapping("/read")
    public Object read(@NotNull Integer id) {
        TIssue issue = issueService.getById(id);
        return ResponseUtil.ok(issue);
    }

    @RequiresPermissions("admin:issue:update")
    @RequiresPermissionsDesc(menu = {"内容管理", "帮助中心"}, button = "编辑")
    @PostMapping("/update")
    public Object update(@RequestBody TIssue issue) {
        Object error = validate(issue);
        if (error != null) {
            return error;
        }
        issue.setUpdateTime(LocalDateTime.now());
        if (issueService.updateById(issue) == false) {
            return ResponseUtil.updatedDataFailed();
        }

        return ResponseUtil.ok(issue);
    }

    @RequiresPermissions("admin:issue:delete")
    @RequiresPermissionsDesc(menu = {"内容管理", "帮助中心"}, button = "删除")
    @PostMapping("/delete")
    public Object delete(@RequestBody TIssue issue) {
        Integer id = issue.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        issueService.deleteById(id);
        return ResponseUtil.ok();
    }

}
