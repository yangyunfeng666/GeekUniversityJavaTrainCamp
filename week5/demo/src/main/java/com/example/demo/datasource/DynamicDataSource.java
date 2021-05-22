package com.example.demo.datasource;

import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

@Component("DynamicDataSource")
public class DynamicDataSource extends AbstractRoutingDataSource {

  public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
    super.setDefaultTargetDataSource(defaultTargetDataSource);
    super.setTargetDataSources(targetDataSources);
    super.afterPropertiesSet();
  }

  @Override
  protected Object determineCurrentLookupKey() {
    return DataSourceContextHolder.getDataSourceType();
  }

}
