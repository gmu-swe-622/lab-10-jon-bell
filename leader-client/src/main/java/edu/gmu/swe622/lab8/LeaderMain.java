package edu.gmu.swe622.lab8;

import java.util.Scanner;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class LeaderMain {

	private static CuratorFramework curator;

	public static void main(String[] args) throws Exception {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		curator = CuratorFrameworkFactory.newClient("localhost:9000,localhost:9001,localhost:9002,localhost:9003,localhost:9004", retryPolicy);
		curator.start();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				curator.close();
			}
		}));

		Scanner s = new Scanner(System.in);
		System.out.println("Enter command (s for stall, w for wait to be leader):");
		while (s.hasNextLine()) {
			String op = s.nextLine();
			switch (op) {
			case "s":
				break;
			case "w":
				for (Thread t : Thread.currentThread().getAllStackTraces().keySet()) {
					if (t != Thread.currentThread()) {
						t.suspend();
					}
				}
				System.out.println("Sleeping for 50*1000 msec");
				Thread.currentThread().sleep(50 * 1000);
				System.out.println("Woke up");
				for (Thread t : Thread.currentThread().getAllStackTraces().keySet()) {
					if (t != Thread.currentThread()) {
						t.resume();
					}
				}
				break;
			}
		}
		s.close();
	}


}
