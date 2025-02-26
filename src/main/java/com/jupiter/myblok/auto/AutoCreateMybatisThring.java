package com.jupiter.myblok.auto;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.Arrays;
import java.util.List;

public class AutoCreateMybatisThring {
    public static void main(String[] args) {
        //创建对象
        AutoGenerator autoGenerator = new AutoGenerator();
        //数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        // 数据库类型
        dataSourceConfig.setDbType(DbType.MYSQL);
        // TODO:url、用户名、密码
        dataSourceConfig.setUrl("jdbc:mysql://localhost/blok?useUnicode=true&characterEncoding=UTF-8");
        dataSourceConfig.setUsername("root");
        dataSourceConfig.setPassword("0000");
        //同样地，这里要注意mysql驱动的版本5.x
        dataSourceConfig.setDriverName("com.mysql.cj.jdbc.Driver");
        autoGenerator.setDataSource(dataSourceConfig);

        //全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOutputDir(System.getProperty("user.dir")+"/src/main/java");
        globalConfig.setOpen(false);
        //设置作者
        globalConfig.setAuthor("jupiter");
        //去掉IService的I
//        globalConfig.setServiceName("%sService");
        autoGenerator.setGlobalConfig(globalConfig);

        //包信息
        PackageConfig packageConfig = new PackageConfig();
        //TODO:
        packageConfig.setParent("com.jupiter.myblok");
        packageConfig.setEntity("pojo.PO");
        //有时候把持久层记为dao或repository
        packageConfig.setMapper("mapper");
        packageConfig.setService(" ");
        packageConfig.setServiceImpl(" ");
        packageConfig.setController(" ");
        autoGenerator.setPackageInfo(packageConfig);

        //策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        //TODO:设置要映射的表名（字符串用逗号隔开，多表）
        strategyConfig.setInclude("admin_table");
        //设置生成实体类属性驼峰式命名
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
        //给实体类加lombok注解
        strategyConfig.setEntityLombokModel(true);
        strategyConfig.setLogicDeleteFieldName("deleted");
        //自动填充配置
        TableFill tableFill1 = new TableFill("create_time", FieldFill.INSERT);
        TableFill tableFill2 = new TableFill("update_time", FieldFill.INSERT_UPDATE);
        List<TableFill> list = Arrays.asList(tableFill1, tableFill2);
        strategyConfig.setTableFillList(list);
        //乐观锁
        strategyConfig.setVersionFieldName("version");
        autoGenerator.setStrategy(strategyConfig);

        //启动
        autoGenerator.execute();
//        TODO:记得生成好了自己加一下mapping注解
//        TODO:可选择加入 @AllArgsConstructor
    }
}
