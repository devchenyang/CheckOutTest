package com.chenyang.zkdemo;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public class App {
    public static void main(String[] args) throws Exception {
        CountDownLatch cd = new CountDownLatch(1);

        ZooKeeper zk = new ZooKeeper("192.168.175.137:2181,192.168.175.138:2181,192.168.175.139:2181,192.168.175.140:2181",
                3000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                Event.KeeperState state = event.getState();
                Event.EventType type = event.getType();
                String path = event.getPath();
                System.out.println("new zk watch：" + event.toString());

                switch (state) {
                    case Unknown:
                        break;
                    case Disconnected:
                        break;
                    case NoSyncConnected:
                        break;
                    case SyncConnected:
                        System.out.println("connected");
                        cd.countDown();
                        break;
                    case AuthFailed:
                        break;
                    case ConnectedReadOnly:
                        break;
                    case SaslAuthenticated:
                        break;
                    case Expired:
                        break;
                    case Closed:
                        break;
                }

                switch (type) {
                    case None:
                        break;
                    case NodeCreated:
                        break;
                    case NodeDeleted:
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
        });

        cd.await();

        ZooKeeper.States state = zk.getState();
        switch (state) {
            case CONNECTING:
                System.out.println("ing......");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("ed......");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }

        String pathName = zk.create("/aaa", "olddata".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        Stat stat = new Stat();
        byte[] data = zk.getData("/aaa", new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("getData watch:" + event.toString());
                try {
                    zk.getData("/aaa", true, stat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, stat);

        System.out.println(new String(data, StandardCharsets.UTF_8));

        Stat stat1 = zk.setData("/aaa", "newdata".getBytes(StandardCharsets.UTF_8), 0);
        Stat stat2 = zk.setData("/aaa", "newdata01".getBytes(StandardCharsets.UTF_8), stat1.getVersion());

        System.out.println("---------async start---------");
        zk.getData("/aaa", false, new AsyncCallback.DataCallback() {
            @Override
            public void processResult(int rc, String path, Object ctx, byte[] data, Stat stat) {
                System.out.println("------async call back-------");
                System.out.println(ctx);
                System.out.println(new String(data));

            }
        }, "abc");


        Thread.sleep(33333333);
    }
}
