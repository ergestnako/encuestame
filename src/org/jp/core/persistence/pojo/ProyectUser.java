package org.jp.core.persistence.pojo;

// Generated 07-may-2009 17:38:33 by Hibernate Tools 3.2.2.GA

import java.util.Date;

/**
 * ProyectUser generated by hbm2java
 */
public class ProyectUser implements java.io.Serializable {

	private ProyectUserId id;
	private Proyect proyect;
	private SecUsers secUsers;
	private Date dateNew;

	public ProyectUser() {
	}

	public ProyectUser(ProyectUserId id, Proyect proyect, SecUsers secUsers,
			Date dateNew) {
		this.id = id;
		this.proyect = proyect;
		this.secUsers = secUsers;
		this.dateNew = dateNew;
	}

	public ProyectUserId getId() {
		return this.id;
	}

	public void setId(ProyectUserId id) {
		this.id = id;
	}

	public Proyect getProyect() {
		return this.proyect;
	}

	public void setProyect(Proyect proyect) {
		this.proyect = proyect;
	}

	public SecUsers getSecUsers() {
		return this.secUsers;
	}

	public void setSecUsers(SecUsers secUsers) {
		this.secUsers = secUsers;
	}

	public Date getDateNew() {
		return this.dateNew;
	}

	public void setDateNew(Date dateNew) {
		this.dateNew = dateNew;
	}

}
