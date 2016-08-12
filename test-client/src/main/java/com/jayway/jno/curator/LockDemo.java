package com.jayway.jno.curator;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockDemo {
    
    private static final Logger logger = LoggerFactory.getLogger(LockDemo.class);
    private static final String zooKeeperConnectString = "localhost:2181";


    public static void main(String[] args) {
        
        CuratorFramework curator = CuratorFrameworkFactory.newClient(zooKeeperConnectString, new ExponentialBackoffRetry(1000, 3));
        curator.start();
        InterProcessMutex mutex = new InterProcessMutex(curator, "/mutexdemo2");
        
        try {
            new Thread(() -> doWork(mutex)).start();
        } catch (Exception e) {
            logger.error("Caught exception in main", e);
        }        
    }
    
    private static Object doWork(InterProcessMutex mutex) {
        try {
            tryDoWork(mutex);
        } catch (Exception e) {
            logger.error("Exception while trying to do work", e);
        }
        return null;
    }

    private static void tryDoWork(InterProcessMutex mutex) throws Exception {
        while (true) {
            logger.info("Trying to acquire lock");
            mutex.acquire();
            try {
                for (int i=0; i<10; i++) {
                    logger.info("I got the lock!");
                    Thread.sleep(1000);
                }                
            }
            finally {
                logger.info("Releasing lock");
                mutex.release();                
            }
            
            for (int i=0; i<5; i++) {
                logger.info("Working outside the lock!");
                Thread.sleep(1000);
            }
        }
    }
}
