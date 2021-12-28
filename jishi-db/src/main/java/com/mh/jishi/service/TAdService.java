package com.mh.jishi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mh.jishi.entity.TAd;
import com.mh.jishi.mapper.TAdMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 轮播图服务层
 */
@Service
public class TAdService extends ServiceImpl<TAdMapper, TAd> {
    public IPage<TAd> querySelective(String name, String content, Integer page, Integer limit, String sort, String order) {

        QueryWrapper<TAd> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(org.apache.commons.lang3.StringUtils.isNotBlank(name), "name", "%" + name + "%");
        queryWrapper.eq(org.apache.commons.lang3.StringUtils.isNotBlank(content), "content", "%" + content + "%");
        queryWrapper.eq("deleted", 0);
        IPage<TAd> iPage = new Page<>(page, limit);
        return this.baseMapper.selectPage(iPage, queryWrapper);
    }

    public void add(TAd ad) {
        ad.setAddTime(LocalDateTime.now());
        ad.setUpdateTime(LocalDateTime.now());
        save(ad);
    }

    public boolean updateByIdAndDate(TAd ad) {
        ad.setUpdateTime(LocalDateTime.now());
        return updateById(ad);
    }

    public boolean deleteByIdAndDate(Integer id) {
        boolean isUpd = true;
        TAd ad = getById(id);
        if(ad != null){
            ad.setUpdateTime(LocalDateTime.now());
            ad.setDeleted(true);
            isUpd = updateById(ad);
        }

        return isUpd;
    }

}