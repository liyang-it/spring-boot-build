package com.mh.jishi;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * <h2>Mybatis 表 实体类、mapper、controller生成</h2>
 * 文档：<a href="https://baomidou.com/pages/779a6e/#%E4%BD%BF%E7%94%A8">文档地址</a>
 * 代码需要依赖  Maven Freemarker引擎模板
 *
 * </p>
 *
 * @author Evan
 * @since 2022/3/26   14:59
 */
public class MyBatisAutoGenerator {
    public static void main(String[] args) {
        // 要生成的表列表
        List<String> includeTables = new LinkedList<>();
        includeTables.add("t_sn_order_record");
        includeTables.add("t_sn_order_record_box");

        FastAutoGenerator.create("jdbc:mysql://127.0.0.1:3306/pms?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&verifyServerCertificate=false&useSSL=false&rewriteBatchedStatements=true", "root", "123456")
                .globalConfig(builder -> {
                    builder.author("Evan") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("F:\\mybatis-out"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.mh") // 设置父包名
                            .moduleName("com") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "F:\\mybatis-out")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude(includeTables) // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();

    }
}