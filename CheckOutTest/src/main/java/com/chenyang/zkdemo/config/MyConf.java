package com.chenyang.zkdemo.config;

// 保存所有配置的实体类，也就是保存从zookeeper读到的数据的实体类
public class MyConf {
    private String conf;

    public String getConf() {
        return conf;
    }

    public void setConf(String conf) {
        this.conf = conf;
    }
}
