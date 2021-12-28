package com.mh.jishi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mh.jishi.entity.TUser;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

/**
 * @Author Lizr
 * @Description 用户mapper
 * @CreateTime 2021-12-27 下午 3:50
 **/
@Mapper
public interface TUserMapper extends BaseMapper<TUser> {

}
