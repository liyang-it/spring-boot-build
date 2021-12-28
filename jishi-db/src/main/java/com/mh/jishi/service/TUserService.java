package com.mh.jishi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mh.jishi.entity.TUser;
import com.mh.jishi.mapper.TUserMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @Author Lizr
 * @Description 用户服务层
 * @CreateTime 2021-12-27 下午 3:56
 **/
@Service
public class TUserService extends ServiceImpl<TUserMapper, TUser> {
    public TUser queryByMobile(String mobile) {
        QueryWrapper<TUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.notIn("status", 2);
        queryWrapper.eq("mobile", mobile);
        queryWrapper.eq("deleted", 0);
        return this.baseMapper.selectOne(queryWrapper);
    }

    public int queryCountByMobile(String mobile) {
        QueryWrapper<TUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.notIn("status", 2);
        queryWrapper.eq("mobile", mobile);
        queryWrapper.eq("deleted", 0);
        return this.baseMapper.selectCount(queryWrapper);
    }

    @Transactional
    public boolean updateByIdAndUpdateDate(TUser user){
        user.setUpdateTime(LocalDateTime.now());
        boolean updateRows = updateById(user);
        return updateRows;
    }

    /**
     * 后台管理 -分页查询用户列表
     * @param id 用户ID
     * @param username 用户昵称 模糊查询
     * @param mobile 用户手机号 模糊查询
     * @param page 当前页
     * @param size 返回条数
     * @param sort 排序字段
     * @param order 排序规则 desc 升序 asc 降序
     * @return
     */
    public IPage<TUser> querySelective(Integer id, String username, String mobile, Integer page, Integer size, String sort, String order) {
        IPage<TUser> iPage = new Page<>(page, size);
        QueryWrapper<TUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(StringUtils.isNotBlank(username), "username", username);
        queryWrapper.eq(StringUtils.isNotBlank(mobile), "mobile", mobile);
        queryWrapper.eq("deleted", 0);
        if("desc".equals(order)){
            queryWrapper.orderByDesc(sort);
        }else{
            queryWrapper.orderByAsc(sort);
        }
        return this.baseMapper.selectPage(iPage, queryWrapper);
    }
}
