package com.mh.jishi.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import com.alibaba.fastjson.JSONObject;

/**
 * <h2>json字符串转json对象</h2>
 * <p>
 *   存储数据库中的格式为 json字符串， 查询出来对应java JSONObject对象
 * </p>
 *
 * @author Evan
 * @since  2022/4/7 9:59
 */
public class JsonStringTypeHandler extends BaseTypeHandler<JSONObject> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, JSONObject jsonObject, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, toStr(jsonObject));
    }

    @Override
    public JSONObject getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return toJson(resultSet.getString(s));
    }

    @Override
    public JSONObject getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return toJson(resultSet.getString(i));
    }

    @Override
    public JSONObject getNullableResult(CallableStatement callableStatement, int i) throws SQLException {

        return toJson(callableStatement.getString(i));
    }

    private String toStr(JSONObject json){
        return json.toJSONString();
    }

    private JSONObject toJson(String s){
        return JSONObject.parseObject(s);
    }
}
