package org.jp.core.persistence.pojo;

// Generated 07-may-2009 17:38:33 by Hibernate Tools 3.2.2.GA

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * SurveyType generated by hbm2java
 */
public class SurveyType implements java.io.Serializable {

	private Integer stid;
	private int version;
	private CatLocation catLocation;
	private String description;
	private String labelGraphic;
	private Date date;
	private String template;
	private String redirectPage;
	private Integer numberCopy;
	private String hash;
	private String public_;
	private Set<Surveys> surveyses = new HashSet<Surveys>(0);
	private Set<SurveyQuestionDependency> surveyQuestionDependencies = new HashSet<SurveyQuestionDependency>(
			0);
	private Set<ProyectTypeSurvey> proyectTypeSurveies = new HashSet<ProyectTypeSurvey>(
			0);
	private Set<SurveySectionType> surveySectionTypes = new HashSet<SurveySectionType>(
			0);
	private Set<SurveyDetail> surveyDetails = new HashSet<SurveyDetail>(0);

	public SurveyType() {
	}

	public SurveyType(CatLocation catLocation, String public_) {
		this.catLocation = catLocation;
		this.public_ = public_;
	}

	public SurveyType(CatLocation catLocation, String description,
			String labelGraphic, Date date, String template,
			String redirectPage, Integer numberCopy, String hash,
			String public_, Set<Surveys> surveyses,
			Set<SurveyQuestionDependency> surveyQuestionDependencies,
			Set<ProyectTypeSurvey> proyectTypeSurveies,
			Set<SurveySectionType> surveySectionTypes,
			Set<SurveyDetail> surveyDetails) {
		this.catLocation = catLocation;
		this.description = description;
		this.labelGraphic = labelGraphic;
		this.date = date;
		this.template = template;
		this.redirectPage = redirectPage;
		this.numberCopy = numberCopy;
		this.hash = hash;
		this.public_ = public_;
		this.surveyses = surveyses;
		this.surveyQuestionDependencies = surveyQuestionDependencies;
		this.proyectTypeSurveies = proyectTypeSurveies;
		this.surveySectionTypes = surveySectionTypes;
		this.surveyDetails = surveyDetails;
	}

	public Integer getStid() {
		return this.stid;
	}

	public void setStid(Integer stid) {
		this.stid = stid;
	}

	public int getVersion() {
		return this.version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public CatLocation getCatLocation() {
		return this.catLocation;
	}

	public void setCatLocation(CatLocation catLocation) {
		this.catLocation = catLocation;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLabelGraphic() {
		return this.labelGraphic;
	}

	public void setLabelGraphic(String labelGraphic) {
		this.labelGraphic = labelGraphic;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTemplate() {
		return this.template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getRedirectPage() {
		return this.redirectPage;
	}

	public void setRedirectPage(String redirectPage) {
		this.redirectPage = redirectPage;
	}

	public Integer getNumberCopy() {
		return this.numberCopy;
	}

	public void setNumberCopy(Integer numberCopy) {
		this.numberCopy = numberCopy;
	}

	public String getHash() {
		return this.hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getPublic_() {
		return this.public_;
	}

	public void setPublic_(String public_) {
		this.public_ = public_;
	}

	public Set<Surveys> getSurveyses() {
		return this.surveyses;
	}

	public void setSurveyses(Set<Surveys> surveyses) {
		this.surveyses = surveyses;
	}

	public Set<SurveyQuestionDependency> getSurveyQuestionDependencies() {
		return this.surveyQuestionDependencies;
	}

	public void setSurveyQuestionDependencies(
			Set<SurveyQuestionDependency> surveyQuestionDependencies) {
		this.surveyQuestionDependencies = surveyQuestionDependencies;
	}

	public Set<ProyectTypeSurvey> getProyectTypeSurveies() {
		return this.proyectTypeSurveies;
	}

	public void setProyectTypeSurveies(
			Set<ProyectTypeSurvey> proyectTypeSurveies) {
		this.proyectTypeSurveies = proyectTypeSurveies;
	}

	public Set<SurveySectionType> getSurveySectionTypes() {
		return this.surveySectionTypes;
	}

	public void setSurveySectionTypes(Set<SurveySectionType> surveySectionTypes) {
		this.surveySectionTypes = surveySectionTypes;
	}

	public Set<SurveyDetail> getSurveyDetails() {
		return this.surveyDetails;
	}

	public void setSurveyDetails(Set<SurveyDetail> surveyDetails) {
		this.surveyDetails = surveyDetails;
	}

}
