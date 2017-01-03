package com.gameduell.jira.util;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.gameduell.jira.JiraClient;
import com.gameduell.jira.exception.NotLoggedException;

public class Context {
	private JiraRestClient client;
	private JiraClient jiraClient;

	public void setJiraRestClient(JiraRestClient client) {
		this.client = client;
	}

	public JiraRestClient getJiraRestClient() {
		if(client == null)
			throw new NotLoggedException();
		
		return client;
	}

	public void setJiraClient(JiraClient jiraClient) {
		this.jiraClient = jiraClient;
	}

	public JiraClient getJiraClient() {
		return jiraClient;
	}
}
