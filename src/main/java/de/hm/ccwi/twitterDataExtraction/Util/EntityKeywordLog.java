package de.hm.ccwi.twitterDataExtraction.Util;

import java.util.List;

public class EntityKeywordLog {

	private String text;

	private List<String> entityList;

	private List<String> keywordList;

	public EntityKeywordLog(String text, List<String> entityList, List<String> keywordList) {
		super();
		this.text = text;
		this.entityList = entityList;
		this.keywordList = keywordList;
	}

	public synchronized void addToEntityList(String entity) {
		this.entityList.add(entity);
	}

	public synchronized void addToKeywordList(String keyword) {
		this.keywordList.add(keyword);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<String> getEntityList() {
		return entityList;
	}

	public void setEntityList(List<String> entityList) {
		this.entityList = entityList;
	}

	public List<String> getKeywordList() {
		return keywordList;
	}

	public void setKeywordList(List<String> keywordList) {
		this.keywordList = keywordList;
	}

}
