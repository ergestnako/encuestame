package org.jp.core.persistence.pojo;

// Generated 07-may-2009 17:38:33 by Hibernate Tools 3.2.2.GA

/**
 * SecGroupUser generated by hbm2java
 */
public class SecGroupUser implements java.io.Serializable {

	private SecGroupUserId id;
	private SecGroups secGroups;
	private SecUsers secUsers;
	private Boolean state;

	public SecGroupUser() {
	}

	public SecGroupUser(SecGroupUserId id, SecGroups secGroups,
			SecUsers secUsers) {
		this.id = id;
		this.secGroups = secGroups;
		this.secUsers = secUsers;
	}

	public SecGroupUser(SecGroupUserId id, SecGroups secGroups,
			SecUsers secUsers, Boolean state) {
		this.id = id;
		this.secGroups = secGroups;
		this.secUsers = secUsers;
		this.state = state;
	}

	public SecGroupUserId getId() {
		return this.id;
	}

	public void setId(SecGroupUserId id) {
		this.id = id;
	}

	public SecGroups getSecGroups() {
		return this.secGroups;
	}

	public void setSecGroups(SecGroups secGroups) {
		this.secGroups = secGroups;
	}

	public SecUsers getSecUsers() {
		return this.secUsers;
	}

	public void setSecUsers(SecUsers secUsers) {
		this.secUsers = secUsers;
	}

	public Boolean getState() {
		return this.state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

}
