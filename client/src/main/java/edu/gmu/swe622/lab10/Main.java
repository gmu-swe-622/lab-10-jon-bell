package edu.gmu.swe622.lab10;

import java.util.HashMap;
import java.util.Scanner;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.ZKPaths;

public class Main {

	private static CuratorFramework curator;

	public static void main(String[] args) throws Exception {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		curator = CuratorFrameworkFactory
				.newClient("localhost:9000,localhost:9001,localhost:9002,localhost:9003,localhost:9004", retryPolicy);
		curator.start();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				curator.close();
			}
		}));

		HashMap<String, BlockingGroupMember> memberships = new HashMap<>();
		Scanner s = new Scanner(System.in);

		System.out.println("Enter the ID you want to use: ");
		String id = s.nextLine();
		System.out.println("Enter command (j for join group, x for exit group, l for list group ):");
		while (s.hasNextLine()) {
			String op = s.nextLine();
			switch (op) {
			case "j":
				System.out.println("What group (make sure it starts with /)?");
				String group = s.nextLine();
				try {
					BlockingGroupMember grp = new BlockingGroupMember(curator, group, id);
					grp.startAndBlockUntilJoined();
					memberships.put(group, grp);
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println("Unable to leave group");
				}
				break;
			case "x":
				System.out.println("What group (make sure it starts with /)?");
				group = s.nextLine();
				try {
					memberships.get(group).close();
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println("Unable to leave group");
				}
				System.out.println("Left group");
				break;
			case "l":
				System.out.println("What group (make sure it starts with /)?");
				group = s.nextLine();
				BlockingGroupMember grp = new BlockingGroupMember(curator, group, id);
				System.out.println("Members: " + grp.getMembers());
				break;
			}
			System.out.println("Enter command (j for join group, x for exit group, l for list group ):");
		}
		s.close();
	}

}
