package com.example.demo.datasource;

public class DataSourceContextHolder {

    public static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<String>();

    public static void setDataSourceType(String dsType) {
        CONTEXT_HOLDER.set(dsType);
    }

    public static String getDataSourceType() {
        return CONTEXT_HOLDER.get();
    }

    public static void removeDataSourceType() {
        CONTEXT_HOLDER.remove();
    }

}