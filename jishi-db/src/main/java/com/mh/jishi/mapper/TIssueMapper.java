package com.mh.jishi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mh.jishi.entity.TIssue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.Map;
@Mapper
public interface TIssueMapper extends BaseMapper<TIssue> {

    @Select(" <script>select question, answer, DATE_FORMAT(add_time,'%Y-%m-%d %h:%m:%s') as 'addTime' from t_issue where deleted = 0  <when test = 'keyword != null'> and (question like concat('%', #{keyword}, '%') or answer like concat('%', #{keyword}, '%')) </when>order by add_time desc </script>")
    IPage<Map<String, String>> pageQueryIssue(IPage<Map<String, String>> iPage, @Param("keyword") String keyword);

}