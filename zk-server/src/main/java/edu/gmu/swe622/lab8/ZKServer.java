package edu.gmu.swe622.lab8;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.curator.test.InstanceSpec;
import org.apache.curator.test.TestingCluster;
import org.apache.curator.test.TestingServer;
import org.apache.curator.test.TestingZooKeeperServer;

/**
 * Note - the lock server now just runs ZooKeeper - all locking happens by the
 * client talking to ZooKeeper
 * 
 * @author jon
 *
 */
public class ZKServer {

	public static void main(String[] args) throws Exception {

		HashMap<Integer, InstanceSpec> servers = new HashMap<Integer, InstanceSpec>();
		ArrayList<InstanceSpec> specs = new ArrayList<InstanceSpec>();
		for (int i = 0; i < 5; i++) {
			specs.add(new InstanceSpec(null, 9000 + i, -1, -1, true, -1, -1, -1));
		}
		HashMap<InstanceSpec, Collection<InstanceSpec>> i2i = new HashMap<InstanceSpec, Collection<InstanceSpec>>();
		for (InstanceSpec s : specs) {
			i2i.put(s, specs);
		}
		TestingCluster tc = new TestingCluster(i2i);
		tc.start();
		System.out.println("Running ZooKeepers:");
		for (TestingZooKeeperServer ts : tc.getServers()) {
			System.out.println(ts.getInstanceSpec().getConnectString());
			servers.put(ts.getInstanceSpec().getPort(), ts.getInstanceSpec());
		}
		System.out.println("Enter command (K for kill, H for heal): ");
		Scanner s = new Scanner(System.in);
		while (s.hasNextLine()) {
			switch (s.nextLine()) {
			case "K":
				System.out.println("Enter port of ZK server to kill: ");
				int port = s.nextInt();
				InstanceSpec spec = servers.get(port);
				if (spec == null)
					System.err.println("Invalid server");
				else
					tc.killServer(spec);
				break;
			case "H":
				System.out.println("Enter port of ZK server to heal: ");
				port = s.nextInt();
				spec = servers.get(port);
				if (spec == null)
					System.err.println("Invalid server");
				else
					tc.restartServer(spec);
				break;
			}
			System.out.println("Enter command (K for kill, H for heal): ");
		}
	}
}
