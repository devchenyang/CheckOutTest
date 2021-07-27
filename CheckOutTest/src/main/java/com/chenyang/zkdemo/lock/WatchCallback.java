package com.chenyang.zkdemo.lock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class WatchCallback implements Watcher, AsyncCallback.StringCallback, AsyncCallback.Children2Callback, AsyncCallback.StatCallback {

    private ZooKeeper zk;
    private String threadName;
    private CountDownLatch latch = new CountDownLatch(1);
    private String pathName;

    public void tryLock() {
        try {
            zk.create("/lock", threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL, this, "abc");

            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unlock() {
        try {
            zk.delete(pathName, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processResult(int rc, String path, Object ctx, Stat stat) {

    }

    @Override
    public void processResult(int rc, String path, Object ctx, List<String> children, Stat stat) {

//        System.out.println(threadName + " look nodes......");
//        for (String child : children) {
//            System.out.println(child);
//        }

        // 获取所有节点后排序
        Collections.sort(children);
        int i = children.indexOf(pathName.substring(1));

        // 如果节点是第一个则latch减1
        if (i == 0) {
            try {
                // 防止业务处理太快其它线程监听不到锁释放，也可以辅助实现可重入锁
                zk.setData("/", threadName.getBytes(), -1);

                System.out.println(threadName + " I am first");
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 不是第一个则监听上一个节点
        } else {
            zk.exists("/" + children.get(i - 1), this, this, "sdf");
        }


    }

    @Override
    public void processResult(int rc, String path, Object ctx, String name) {

        if (name != null) {
            System.out.println(threadName + "create node" + name);
            pathName = name;
            zk.getChildren("/", false, this, "asd");
        }
    }

    @Override
    public void process(WatchedEvent event) {

        switch (event.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zk.getChildren("/", false, this, "asd");
                break;
            case NodeDataChanged:
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


    public ZooKeeper getZk() {
        return zk;
    }

    public void setZk(ZooKeeper zk) {
        this.zk = zk;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getPathName() {
        return pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

}
