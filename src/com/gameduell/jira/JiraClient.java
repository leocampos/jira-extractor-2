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
	private Context context;
	private Config config;
	private Authenticator authenticator;
	private Expandos[] expandArr = new Expandos[] { Expandos.CHANGELOG };
	private List<Expandos> expand = Arrays.asList(expandArr);

	protected JiraClient(Config config, Context context, Authenticator authenticator) {
		this.config = config;
		this.context = context;
		this.authenticator = authenticator;
		
		authenticator.setConfig(config);
		authenticator.setContext(context);
		
		context.setJiraClient(this);
	}
	
	public static JiraClient createLoggedClient() {
		return new JiraClient(new Config(), new Context(), new Authenticator()).login();
	}

	private JiraClient login() {
		authenticator.login();
		return this;
	}

	public List<Issue> find(String jql) {
		List<Issue> issues = new ArrayList<>();
		
		findIssues(jql).forEachRemaining(issues::add);
		
		return issues;
	}
	
	public List<ExpandedIssue> findWithExpandos(String jql, Expandos[] expandos) {
		List<ExpandedIssue> expIssues = new ArrayList<>();
		
		find(jql).forEach(item -> 
			expIssues.add(this.findChangelogAndPopulateIssues(item))
		);
		
		return expIssues;
	}
	
	public List<ExpandedIssue> findWithChangelog(String jql) {
		return findWithExpandos(jql, expandArr);
	}
	
	public ExpandedIssue findByKeyWithChangelog(String key) {
		List<ExpandedIssue> items = findWithChangelog("key = " + key);
		
		if(items == null || items.isEmpty()) return null;
		
		return items.get(0);
	}
	
	Iterator<Issue> findIssues(String jql) {
		return new JiraIssuesIterator(jql, config, context.getJiraRestClient().getSearchClient());
	}
	
	public ExpandedIssue findChangelogAndPopulateIssues(Issue issueWithExpando) {
		Issue issue = context.getJiraRestClient().getIssueClient().getIssue(issueWithExpando.getKey(), expand).claim();
	
		return new ExpandedIssue(issue, StrategyFactory.createBlockedTimeRetrievalStrategyInstance(config.getBlockedTimesStrategy(), context));
	}
}
