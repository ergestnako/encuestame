package org.jp.core.persistence.pojo;

// Generated 07-may-2009 17:38:33 by Hibernate Tools 3.2.2.GA

/**
 * SurveyDetailId generated by hbm2java
 */
public class SurveyDetailId implements java.io.Serializable {

	private int idSd;
	private int qid;
	private int ssid;
	private int stid;

	public SurveyDetailId() {
	}

	public SurveyDetailId(int idSd, int qid, int ssid, int stid) {
		this.idSd = idSd;
		this.qid = qid;
		this.ssid = ssid;
		this.stid = stid;
	}

	public int getIdSd() {
		return this.idSd;
	}

	public void setIdSd(int idSd) {
		this.idSd = idSd;
	}

	public int getQid() {
		return this.qid;
	}

	public void setQid(int qid) {
		this.qid = qid;
	}

	public int getSsid() {
		return this.ssid;
	}

	public void setSsid(int ssid) {
		this.ssid = ssid;
	}

	public int getStid() {
		return this.stid;
	}

	public void setStid(int stid) {
		this.stid = stid;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof SurveyDetailId))
			return false;
		SurveyDetailId castOther = (SurveyDetailId) other;

		return (this.getIdSd() == castOther.getIdSd())
				&& (this.getQid() == castOther.getQid())
				&& (this.getSsid() == castOther.getSsid())
				&& (this.getStid() == castOther.getStid());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getIdSd();
		result = 37 * result + this.getQid();
		result = 37 * result + this.getSsid();
		result = 37 * result + this.getStid();
		return result;
	}

}
