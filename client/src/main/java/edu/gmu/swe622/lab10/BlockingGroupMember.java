package edu.gmu.swe622.lab10;

import java.util.List;

import org.apache.curator.framework.CuratorFramework;

public class BlockingGroupMember {

	private CuratorFramework curator;
	private String groupName;
	private String id;

	public BlockingGroupMember(CuratorFramework curator, String groupName, String id) {

	}

	/**
	 * Will join to groupName, using id `id`, and will not return until the
	 * group has been joined
	 */
	public void startAndBlockUntilJoined() {

	}

	/**
	 * Returns the current number of members (without caching them) in the
	 * specified group.
	 */
	public List<String> getMembers() {
		return null;
	}

	/**
	 * Leaves the group (if we are joined to it) and cleans up resources.
	 */
	public void close() {

	}

}
