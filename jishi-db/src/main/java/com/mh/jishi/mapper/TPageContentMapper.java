package com.mh.jishi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mh.jishi.entity.TPageContent;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.mapstruct.Mapper;

/**
 * @Author Lizr
 * @Description 页面内容mapper
 * @CreateTime 2021-12-28 下午 5:14
 **/
@Mapper
public interface TPageContentMapper extends BaseMapper<TPageContent> {
    @Select("select count(id) from t_page_content where keyword = #{keyword} ")
    int queryDefaultKey(@Param("keyword") String keyword);
}
