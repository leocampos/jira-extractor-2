package com.gameduell.jira.domain;

import java.util.List;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.gameduell.jira.util.StartEnd;

public interface BlockedTimeRetrievalStrategy {
	public List<StartEnd> getBlockedTimes(Issue issue);
}
