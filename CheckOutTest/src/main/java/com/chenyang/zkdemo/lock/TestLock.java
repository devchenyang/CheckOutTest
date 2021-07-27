package com.chenyang.zkdemo.lock;

import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLock {
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
    public void lock() {

        for (int i = 0; i < 10; i++) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    WatchCallback watchCallback = new WatchCallback();
                    watchCallback.setZk(zk);
                    String threadName = Thread.currentThread().getName();
                    watchCallback.setThreadName(threadName);

                    // 抢锁
                    watchCallback.tryLock();

                    // 处理业务
                    System.out.println(threadName + " is working......");

                    // 防止业务处理太快其它线程监听不到锁释放
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // 释放锁
                    watchCallback.unlock();

                }
            }).start();

        }

        while (true) {

        }


    }
}
