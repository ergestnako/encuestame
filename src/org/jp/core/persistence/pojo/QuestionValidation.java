package org.jp.core.persistence.pojo;

// Generated 07-may-2009 17:38:33 by Hibernate Tools 3.2.2.GA

/**
 * QuestionValidation generated by hbm2java
 */
public class QuestionValidation implements java.io.Serializable {

	private Integer idValidation;
	private Questions questions;
	private QuestionValidationType questionValidationType;
	private String params;

	public QuestionValidation() {
	}

	public QuestionValidation(Questions questions,
			QuestionValidationType questionValidationType, String params) {
		this.questions = questions;
		this.questionValidationType = questionValidationType;
		this.params = params;
	}

	public Integer getIdValidation() {
		return this.idValidation;
	}

	public void setIdValidation(Integer idValidation) {
		this.idValidation = idValidation;
	}

	public Questions getQuestions() {
		return this.questions;
	}

	public void setQuestions(Questions questions) {
		this.questions = questions;
	}

	public QuestionValidationType getQuestionValidationType() {
		return this.questionValidationType;
	}

	public void setQuestionValidationType(
			QuestionValidationType questionValidationType) {
		this.questionValidationType = questionValidationType;
	}

	public String getParams() {
		return this.params;
	}

	public void setParams(String params) {
		this.params = params;
	}

}
