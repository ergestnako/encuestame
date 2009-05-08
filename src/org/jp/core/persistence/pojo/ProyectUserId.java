package org.jp.core.persistence.pojo;

// Generated 07-may-2009 17:38:33 by Hibernate Tools 3.2.2.GA

/**
 * ProyectUserId generated by hbm2java
 */
public class ProyectUserId implements java.io.Serializable {

	private int uid;
	private int proyectId;

	public ProyectUserId() {
	}

	public ProyectUserId(int uid, int proyectId) {
		this.uid = uid;
		this.proyectId = proyectId;
	}

	public int getUid() {
		return this.uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getProyectId() {
		return this.proyectId;
	}

	public void setProyectId(int proyectId) {
		this.proyectId = proyectId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof ProyectUserId))
			return false;
		ProyectUserId castOther = (ProyectUserId) other;

		return (this.getUid() == castOther.getUid())
				&& (this.getProyectId() == castOther.getProyectId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getUid();
		result = 37 * result + this.getProyectId();
		return result;
	}

}
