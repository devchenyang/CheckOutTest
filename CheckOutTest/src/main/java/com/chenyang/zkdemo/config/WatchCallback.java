package com.chenyang.zkdemo.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class WatchCallback implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {

    private ZooKeeper zk;
    private MyConf myConf;
    private CountDownLatch latch = new CountDownLatch(1);

    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public MyConf getMyConf() {
        return myConf;
    }

    public void setMyConf(MyConf myConf) {
        this.myConf = myConf;
    }

    public void aWait() {
        zk.exists("/AppConf", this, this, "ABC");
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 把从zookeeper获取到的数据设置到myConf中
    @Override
    public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {

        if (data != null) {
            myConf.setConf(new String(data));
            latch.countDown();
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {

        if (stat != null) {
            zk.getData("/AppConf", this, this, "asd");
        }
    }

    @Override
    public void process(WatchedEvent event) {

        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                // 节点创建后获取数据
                zk.getData("/AppConf", this, this, "asd");
                break;
            case NodeDeleted:

                // 节点删除后，
                // 清空配置
                myConf.setConf("");
                // 恢复countDownLatch阻塞线程
                latch = new CountDownLatch(1);

                break;
            case NodeDataChanged:
                // 节点修改后重新获取数据
                zk.getData("/AppConf", this, this, "asd");
                break;
            case NodeChildrenChanged:
                break;
            case DataWatchRemoved:
                break;
            case ChildWatchRemoved:
                break;
            case PersistentWatchRemoved:
                break;
        }
    }
}
