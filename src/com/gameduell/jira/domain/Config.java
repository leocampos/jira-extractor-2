package com.gameduell.jira.domain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Config {
	private static final String KEY_DATE_FORMAT = "DATE-FORMAT";
	private static final String KEY_JIRA_URL = "JIRA-URL";
	private static final String KEY_MAX_ITEMS = "MAX-ITEMS";
	private static final String KEY_PAGE_SIZE = "PAGE-SIZE";
	private static final String KEY_LOGIN = "LOGIN";
	private static final String KEY_PASSWORD = "PASSWORD";
	
	static final String DEFAULT_TIMEFORMAT = "dd/MM/yyyy HH:mm";
	static final String STRATEGY_BLOCKED_BY_LINKS = "BLOCKED_BY_LINKS";
	static final String DEFAULT_PAGE_SIZE = "50";
	static final String DEFAULT_JIRA_URL = "";
	static final String DEFAULT_MAX_NUM_OF_ITEMS = "1000";
	
	private String login = "";
	private String password;
	private Properties props = null;

	private final Logger log = Logger.getLogger("Jira-extractor");
	private DateTimeFormatter formatter = null;

	public Config(String configPath) {
		if (configPath != null)
			load(configPath);
		
		setPassword(getValueOrDefault(KEY_PASSWORD, null));
		setLogin(getValueOrDefault(KEY_LOGIN, null));
	}
	
	public Config() {
		this(null);
	}

	public DateTimeFormatter getDateTimeFormatter() {
		if (formatter == null)
			formatter = DateTimeFormat.forPattern(getDateFormat());

		return formatter;
	}

	public Integer getPageSize() {
		return Integer.parseInt(getValueOrDefault(KEY_PAGE_SIZE, DEFAULT_PAGE_SIZE));
	}

	public Integer getMaxNumberOfItems() {
		return Integer.parseInt(getValueOrDefault(KEY_MAX_ITEMS, DEFAULT_MAX_NUM_OF_ITEMS));
	}

	public URI getJiraUri() {
		return URI.create(getValueOrDefault(KEY_JIRA_URL, DEFAULT_JIRA_URL));
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

	public String getBlockedTimesStrategy() {
		return STRATEGY_BLOCKED_BY_LINKS;
	}

	public void load(String filePath) {
		props = new Properties();
		try {
			props.load(new FileInputStream(filePath));
		} catch (FileNotFoundException e) {
			getLogger().log(Level.WARNING, String.format("Config file not found: \"%s\"", filePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getValueOrDefault(String property, String defaultValue) {
		if(props == null) return defaultValue;
		
		return props.getProperty(property, defaultValue);
	}

	public String getDateFormat() {
		return getValueOrDefault(KEY_DATE_FORMAT, DEFAULT_TIMEFORMAT);
	}
}
