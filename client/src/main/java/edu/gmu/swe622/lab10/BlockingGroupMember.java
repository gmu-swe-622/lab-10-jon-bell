package edu.gmu.swe622.lab10;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.nodes.PersistentNode;
import org.apache.curator.utils.ZKPaths;
import org.apache.zookeeper.CreateMode;

public class BlockingGroupMember {

	private CuratorFramework curator;
	private String groupName;
	private String id;
	private PersistentNode node;

	public BlockingGroupMember(CuratorFramework curator, String groupName,
			String id) {
		this.curator = curator;
		this.groupName = groupName;
		this.id = id;
	}

	/**
	 * Will join to groupName, using id `id`, and will not return until the
	 * group has been joined
	 * 
	 * @throws InterruptedException
	 */
	public void startAndBlockUntilJoined() throws InterruptedException {
		// Create a new ephepmeral node
		node = new PersistentNode(curator, //Using curator
				CreateMode.EPHEMERAL, //Ephemeral node (auto-cleaned up after session ends)
				false, //"Protection" is only needed for "sequential" nodes 
				ZKPaths.makePath(groupName, id), //Use the ZKUtils to concatenate groupName/id 
												 //to get the name of the new node. Simplifies things
												 //so we don't have to figure out if groupName ends in / or not
				CuratorFrameworkFactory.getLocalAddress() //Use default data as payload, we don't care
				);
		node.start(); //Create the node asynchronously
		node.waitForInitialCreate(30000, TimeUnit.HOURS); //Wait for 30,000 hours, or until node is created...
	}

	/**
	 * Returns the current number of members (without caching them) in the
	 * specified group.
	 * 
	 * @throws Exception
	 */
	public List<String> getMembers() throws Exception {
		return curator.getChildren().forPath(groupName);
	}

	/**
	 * Leaves the group (if we are joined to it) and cleans up resources.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		node.close();
	}

}
