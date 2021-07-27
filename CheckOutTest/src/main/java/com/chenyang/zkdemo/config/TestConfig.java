package com.chenyang.zkdemo.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestConfig {

    private ZooKeeper zk;

    @Before
    public void conn() {
       zk = ZkUtil.getZk();
    }

    @After
    public void close() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getConf() {
        // 获取节点前，先判断是否存在

        WatchCallback watchCallback = new WatchCallback();
        watchCallback.setZk(zk);
        MyConf myConf = new MyConf();
        watchCallback.setMyConf(myConf);

        watchCallback.aWait();

        // 1.节点不存在

        // 2.节点已存在

        // 3.节点修改

        while (true) {

            if ("".equals(myConf.getConf())) {
                System.out.println("node is missing......");
                watchCallback.aWait();
            } else {
                System.out.println(myConf.getConf());
            }

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
