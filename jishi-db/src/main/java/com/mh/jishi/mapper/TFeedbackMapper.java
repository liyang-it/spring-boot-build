package com.mh.jishi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mh.jishi.entity.TFeedback;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


import java.util.Date;
@Mapper
public interface TFeedbackMapper extends BaseMapper<TFeedback> {

    @Select("<script>" +
            " SELECT * FROM `t_feedback` f where f.deleted = 0 " +
            " <when test='startTime != null'> and f.add_time &gt;= #{startTime} </when> " +
            " <when test='endTime != null'> and f.add_time &lt;= #{endTime} </when> " +
            " <when test='userInfo != null'> and user_id in (select id from t_user u where (u.nickname like concat('%', #{userInfo}, '%') or u.id = #{userInfo})) </when> " +
            " order by f.add_time desc " +
            " </script> ")
    IPage<TFeedback>  pageQuery(IPage<TFeedback> iPage, @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("userInfo") String userInfo);

}