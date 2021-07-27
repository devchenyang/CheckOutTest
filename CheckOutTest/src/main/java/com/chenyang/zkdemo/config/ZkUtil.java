package com.chenyang.zkdemo.config;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 获取zookeeper连接
 */
public class ZkUtil {
    private static ZooKeeper zk;
    private static String address = "192.168.175.137:2181,192.168.175.138:2181,192.168.175.139:2181,192.168.175.140:2181/testConf";
    private static DefaultWatch watch = new DefaultWatch();
    private static CountDownLatch latch = new CountDownLatch(1);

    public static ZooKeeper getZk() {
        try {
            zk = new ZooKeeper(address, 1000, watch);

            watch.setLatch(latch);

            latch.await();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return zk;
    }

}
