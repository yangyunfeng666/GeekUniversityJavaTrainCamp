package com.example.demo.datasource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.default-datasource")
    public DataSource defaultDataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.target-datasources.datasource1")
    public DataSource dataSource1() {
        return DruidDataSourceBuilder.create().build();
    }


    @Bean
    @Primary
    public DataSource dynamicDataSource(DataSource defaultDataSource, DataSource dataSource1) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.DEFAULT_DATASOURCE.name(), defaultDataSource);
        targetDataSources.put(DataSourceType.DATASOURCE1.name(), dataSource1);
        return new DynamicDataSource(defaultDataSource, targetDataSources);
    }
}