package com.gameduell.jira;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.atlassian.jira.rest.client.api.IssueRestClient.Expandos;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.gameduell.jira.authentication.Authenticator;
import com.gameduell.jira.domain.Config;
import com.gameduell.jira.domain.ExpandedIssue;
import com.gameduell.jira.domain.StrategyFactory;
import com.gameduell.jira.util.Context;
import com.gameduell.jira.util.JiraIssuesIterator;

public class JiraClient {
	private Config config;
	private Authenticator authenticator;
	private Expandos[] expandArr = new Expandos[] { Expandos.CHANGELOG };
	private List<Expandos> expand = Arrays.asList(expandArr);
	private static ThreadLocal<Context> contextLocal = new ThreadLocal<>();

	protected JiraClient(Config config, Context context, Authenticator authenticator) {
		this.config = config;
		contextLocal.set(context);
		this.authenticator = authenticator;
		
		authenticator.setConfig(config);
		authenticator.setContext(context);
		
		context.setJiraClient(this);
	}
	
	public static JiraClient createLoggedClientWithConfiguration(String configurationFilePath) {
		return new JiraClient(new Config(configurationFilePath), new Context(), new Authenticator()).login();
	}
	
	public static JiraClient createLoggedClient() {
		return new JiraClient(new Config(), new Context(), new Authenticator()).login();
	}

	private JiraClient login() {
		authenticator.login();
		return this;
	}

	public List<ExpandedIssue> find(String jql) {
		List<ExpandedIssue> expIssues = new ArrayList<>();
		
		findIssues(jql).forEachRemaining(item -> 
			expIssues.add(new ExpandedIssue(item, StrategyFactory.createBlockedTimeRetrievalStrategyInstance(config.getBlockedTimesStrategy())))
		);
		
		return expIssues;
	}
	
	public List<ExpandedIssue> findWithExpandos(String jql, List<Expandos> expandos) {
		List<ExpandedIssue> expIssues = find(jql);
		
		expIssues.forEach(item -> item.findAndPopulateChangelog(expandos));

		return expIssues;
	}
	
	public List<ExpandedIssue> findWithChangelog(String jql) {
		return findWithExpandos(jql, expand);
	}
	
	public ExpandedIssue findByKeyWithChangelog(String key) {
		List<ExpandedIssue> items = findWithChangelog("key = " + key);
		
		if(items == null || items.isEmpty()) return null;
		
		return items.get(0);
	}
	
	Iterator<Issue> findIssues(String jql) {
		return new JiraIssuesIterator(jql, config, JiraClient.getContext().getJiraRestClient().getSearchClient());
	}

	public static Context getContext() {
		return contextLocal.get();
	}

	public static JiraClient getCurrentInstanceJiraClient() {
		return getContext().getJiraClient();
	}
}
