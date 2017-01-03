package com.gameduell.jira.domain;

import org.joda.time.DateTime;

public class ChangelogItem {
	private DateTime created;
	private String from, to;
	private DateTime timeSpent;

	public ChangelogItem(com.atlassian.jira.rest.client.api.domain.ChangelogItem item, DateTime created) {
		this.created = created;

		setFrom(item.getFromString());
		setTo(item.getToString());
	}

	public DateTime getCreated() {
		return created;
	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to.toUpperCase();
	}
	
	public DateTime timeSpent() {
		return timeSpent;
	}
	
	public void setTimeSpent(DateTime timeSpent) {
		this.timeSpent = timeSpent;
	}
}


