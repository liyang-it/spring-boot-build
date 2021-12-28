package com.mh.jishi.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mh.jishi.entity.TIssue;
import com.mh.jishi.mapper.TIssueMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @Author Lizr
 * @Description 问答服务层
 * @CreateTime 2021-12-27 下午 4:36
 **/
@Service
public class TIssueService extends ServiceImpl<TIssueMapper, TIssue> {
    public void deleteById(Integer id) {
        TIssue q = getById(id);
        if(q != null){
            q.setUpdateTime(LocalDateTime.now());
            q.setDeleted(true);
            updateById(q);
        }
    }

    public void add(TIssue issue) {
        issue.setAddTime(LocalDateTime.now());
        issue.setUpdateTime(LocalDateTime.now());
        save(issue);
    }
}
