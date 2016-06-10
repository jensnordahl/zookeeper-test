package com.jayway.jno.zookeeper;

import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class MembershipDemo {
	
	private static final int ZOOKEEPER_SESSION_TIMEOUT_MILLIS = 2000;
	private ZooKeeper zooKeeper;
	private String zooKeeperConnectString = "localhost:2181";
	
	public MembershipDemo() {
		try {
			zooKeeper = new ZooKeeper(zooKeeperConnectString, ZOOKEEPER_SESSION_TIMEOUT_MILLIS, new ZooKeeperWatcher());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		MembershipDemo application = new MembershipDemo();
		application.run();
	}

	private void run() {
		try {
			while (true) {
				Thread.sleep(10000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class ZooKeeperWatcher implements Watcher {

		@Override
		public void process(WatchedEvent e) {
			System.out.println("Event: " + e);
			
		}
		
	}
}
