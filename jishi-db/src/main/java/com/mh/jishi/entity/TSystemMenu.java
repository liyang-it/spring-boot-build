package com.mh.jishi.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.mh.jishi.mybatis.JsonStringTypeHandler;
import org.springframework.format.annotation.DateTimeFormat;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 系统管理 - 菜单管理信息表
 * </p>
 *
 * @author Evan
 * @since 2022-04-07
 */
@TableName(value = "t_system_menu", autoResultMap = true)
@ApiModel(value = "SystemMenu对象", description = "系统管理 - 菜单管理信息表")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TSystemMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("父级菜单id， 0表示没有上级")
    private Integer parent;

    @ApiModelProperty("子级菜单列表")
    @TableField(exist = false)
    private List<TSystemMenu> children;


    @ApiModelProperty("菜单名称")
    private String name;

    @ApiModelProperty("路径")
    private String path;

    @ApiModelProperty("组件，一级菜单固定 Layout")
    private String component;

    @ApiModelProperty("重定向访问路径")
    private String redirect;

    @ApiModelProperty("固定1")
    private Boolean alwaysShow;

    @ApiModelProperty("标题对象{title: ‘’, icon: ‘’} json字符串格式存储")
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private JSONObject meta;

    @ApiModelProperty("数据版本，每次更新都+1，用于乐观锁更新数据")
    private Integer version;

    @ApiModelProperty("是否隐藏， true隐藏")
    private Boolean hidden;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    // redis 序列号
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime addTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    // redis 序列号
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updateTime;

    @ApiModelProperty("1删除， 0 正常")
    private Boolean deleted;

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public List<TSystemMenu> getChildren() {
        return children;
    }

    public void setChildren(List<TSystemMenu> children) {
        this.children = children;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getParent() {
        return parent;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }
    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }
    public Boolean getAlwaysShow() {
        return alwaysShow;
    }

    public void setAlwaysShow(Boolean alwaysShow) {
        this.alwaysShow = alwaysShow;
    }

    public JSONObject getMeta() {
        return meta;
    }

    public void setMeta(JSONObject meta) {
        this.meta = meta;
    }

    public LocalDateTime getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "TSystemMenu{" +
            "id=" + id +
            ", parent=" + parent +
            ", name=" + name +
            ", path=" + path +
            ", component=" + component +
            ", redirect=" + redirect +
            ", alwaysShow=" + alwaysShow +
            ", meta=" + meta +
            ", addTime=" + addTime +
            ", updateTime=" + updateTime +
            ", deleted=" + deleted +
        "}";
    }
}
