package com.gameduell.jira.domain;


import java.net.URI;
import java.util.logging.Logger;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Config {
	public static final String STRATEGY_BLOCKED_BY_LINKS = "BLOCKED_BY_LINKS";
	private static final Integer DEFAULT_PAGE_SIZE = 50;
	private static final String JIRA_URL = "";
	private String login = "";
	private String password;
	private static Integer MAX_NUM_OF_ITEMS = 1000;

	private final Logger log = Logger.getLogger("Jira-extractor");
	private DateTimeFormatter formatter = null;

	public DateTimeFormatter getDateTimeFormatter() {
		if (formatter == null)
			formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");

		return formatter;
	}
	
	public Integer getPageSize() {
		return DEFAULT_PAGE_SIZE;
	}
	
	public Integer getMaxNumberOfItems() {
		return MAX_NUM_OF_ITEMS;
	}

	public URI getJiraUri() {
		return URI.create(JIRA_URL);
	}

	public Logger getLogger() {
		return log;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPathForDirectoryForTemplateLoading() {
		return null;
	}

	public String getBlockedTimesStrategy() {
		return STRATEGY_BLOCKED_BY_LINKS;
	}
}
