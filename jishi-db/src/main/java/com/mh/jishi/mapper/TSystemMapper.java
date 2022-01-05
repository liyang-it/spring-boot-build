package com.mh.jishi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mh.jishi.entity.TSystem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface TSystemMapper extends BaseMapper<TSystem> {
    /**
     * 根据key 查询对应value
     * @param name
     * @return
     */
    @Select("select key_value from t_system where deleted = 0 and key_name = #{keyName}")
    String getKeyValue(@Param("keyName") String name);

    @Select(" <script> select key_name, key_value from t_system where key_name like concat(#{pre},'%') </script> ")
    List<Map<String, String>> queryKeyAndValueByLikeKey(String pre);


}