package com.gameduell.jira;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.gameduell.jira.authentication.Authenticator;
import com.gameduell.jira.domain.Config;
import com.gameduell.jira.domain.ExpandedIssue;
import com.gameduell.jira.util.Context;

public class JiraClientTest {
	private JiraClient jiraClient = null;
	private Config config = new Config();
	
	@Mock
	private Context context;
	
	@Mock
	private Authenticator authenticator;
	
	@Mock
	private Issue issue1, issue2, issue3;
	
	private List<Issue> issues = new ArrayList<>();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		jiraClient = new JiraClient(config, context, authenticator) {
			@Override
			Iterator<Issue> findIssues(String jql) {
				return issues.iterator();
			}
		};
	}

	@Test
	public void testFind() {
		issues.add(issue1);
		issues.add(issue2);
		issues.add(issue3);
		
		List<ExpandedIssue> expIssues = jiraClient.findWithChangelog("QUERY");
		
		Assert.assertEquals(3, expIssues.size());
	}
}
