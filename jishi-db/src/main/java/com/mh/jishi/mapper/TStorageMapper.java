package com.mh.jishi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mh.jishi.entity.TStorage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TStorageMapper extends BaseMapper<TStorage> {
    @Select("SELECT * FROM `t_storage` where file_key = #{key} and deleted = 0 ")
    TStorage findByKey(@Param("key") String key);

}