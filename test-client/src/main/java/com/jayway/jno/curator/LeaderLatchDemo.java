package com.jayway.jno.curator;

import java.lang.management.ManagementFactory;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.Participant;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeaderLatchDemo {
    
    private static final Logger logger = LoggerFactory.getLogger(LeaderLatchDemo.class);
    private static final String zooKeeperConnectString = "localhost:2181";


    public static void main(String[] args) {
        
        CuratorFramework curator = CuratorFrameworkFactory.newClient(zooKeeperConnectString, new ExponentialBackoffRetry(1000, 3));
        curator.start();
        LeaderLatch leaderLatch = new LeaderLatch(curator, "/leaderlatchtest");
        
        try {
            leaderLatch.start();
            new Thread(() -> doWork(leaderLatch)).start();
        } catch (Exception e) {
            logger.error("Caught exception in main", e);
        }        
    }
    
    private static Object doWork(LeaderLatch leaderLatch) {
        try {
            tryDoWork(leaderLatch);
        } catch (Exception e) {
            logger.error("Exception while trying to do work", e);
        }
        return null;
    }

    private static void tryDoWork(LeaderLatch leaderLatch) throws Exception {
        while (true) {
            logger.info("Waiting to become leader");
            leaderLatch.await();
            if (leaderLatch.hasLeadership()) {
                while (true) {
                    logger.info("I'm the leader!");
                    Thread.sleep(1000);
                }
            }
        }
    }
}
