package com.jayway.jno.zookeeper;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MembershipDemo {
	
    private static final Logger logger = LoggerFactory.getLogger(MembershipDemo.class);
	private static final int ZOOKEEPER_SESSION_TIMEOUT_MILLIS = 500;
	private static final String zooKeeperConnectString = "localhost:2181";
	private ZooKeeper zooKeeper;
    private final String clientName;
	
	public MembershipDemo(String clientName) {
	    this.clientName = clientName;
		try {
			zooKeeper = new ZooKeeper(zooKeeperConnectString, ZOOKEEPER_SESSION_TIMEOUT_MILLIS, new ZooKeeperWatcher());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
	    if (args.length == 1) {
	        String clientName = args[0];
	        logger.info("Starting client: {}", clientName);
	        MembershipDemo application = new MembershipDemo(clientName);
	        application.run();	        
	    }
	    else {
	        System.err.println("Bad number of args, no client name given");
	        System.exit(1);
	    }
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

    private void registerSelfAsMember() {
        try {
            if (zooKeeper.exists("/members", false) == null) {
                zooKeeper.create("/members", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);                
            }
            zooKeeper.create("/members/" + clientName, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } 
    }

    Watcher membershipWatcher = e -> {
        logger.info("Membership changed");
        getAndWatchMembers();
    };

    public void getAndWatchMembers() {
        try {
            List<String> members = zooKeeper.getChildren("/members", membershipWatcher);
            logger.info("Members: {}", members);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
	private class ZooKeeperWatcher implements Watcher {

		@Override
		public void process(WatchedEvent e) {
		    logger.debug("ZooKeeperWatcher got event: {}", e);
		    if (e.getType() == EventType.None && e.getState() == KeeperState.SyncConnected) {
	            logger.info("Registering self as member");
		        registerSelfAsMember();
		        getAndWatchMembers();
		    }
		}
	}

}
