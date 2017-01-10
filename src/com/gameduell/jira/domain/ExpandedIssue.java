package com.gameduell.jira.domain;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.api.IssueRestClient.Expandos;
import com.atlassian.jira.rest.client.api.domain.Attachment;
import com.atlassian.jira.rest.client.api.domain.BasicComponent;
import com.atlassian.jira.rest.client.api.domain.BasicPriority;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.api.domain.BasicVotes;
import com.atlassian.jira.rest.client.api.domain.BasicWatchers;
import com.atlassian.jira.rest.client.api.domain.ChangelogGroup;
import com.atlassian.jira.rest.client.api.domain.Comment;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.IssueField;
import com.atlassian.jira.rest.client.api.domain.IssueLink;
import com.atlassian.jira.rest.client.api.domain.TimeTracking;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.Version;
import com.atlassian.jira.rest.client.api.domain.Worklog;
import com.gameduell.jira.JiraClient;
import com.gameduell.jira.util.StartEnd;

public class ExpandedIssue {
	private Issue issue;
	private static final String STATUS = "STATUS";
	
	private StatusHistory history;
	private BlockedTimeRetrievalStrategy blockedTimeRetrievalStrategy;
	private List<StartEnd> blockedTimes;
	private Long totalBlockedTime;
	
	public ExpandedIssue(Issue issue, BlockedTimeRetrievalStrategy blockedTimeRetrievalStrategy) {
		super();
		this.issue = issue;
		this.blockedTimeRetrievalStrategy = blockedTimeRetrievalStrategy;
	}
	
	
	public void findAndPopulateChangelog(List<Expandos> expand) {
		issue = JiraClient.getContext().getJiraRestClient().getIssueClient().getIssue(getKey(), expand).claim();
	}

	public ExpandedIssue(Issue issue) {
		this(issue, null);
	}

	public boolean equals(Object arg0) {
		return issue.equals(arg0);
	}

	public Iterable<Version> getAffectedVersions() {
		return issue.getAffectedVersions();
	}

	public User getAssignee() {
		return issue.getAssignee();
	}

	public Iterable<Attachment> getAttachments() {
		return issue.getAttachments();
	}

	public URI getAttachmentsUri() {
		return issue.getAttachmentsUri();
	}

	public Iterable<Comment> getComments() {
		return issue.getComments();
	}

	public URI getCommentsUri() {
		return issue.getCommentsUri();
	}

	public Iterable<BasicComponent> getComponents() {
		return issue.getComponents();
	}

	public DateTime getCreationDate() {
		return issue.getCreationDate();
	}

	public String getDescription() {
		return issue.getDescription();
	}

	public DateTime getDueDate() {
		return issue.getDueDate();
	}

	public Iterable<String> getExpandos() {
		return issue.getExpandos();
	}

	private IssueField getFieldByName(String arg0) {
		return issue.getFieldByName(arg0);
	}

	public Iterable<IssueField> getFields() {
		return issue.getFields();
	}

	private Iterable<Version> getFixVersions() {
		return issue.getFixVersions();
	}
	
	public boolean hasFixedVersion(String version) {
		return StreamSupport.stream(getFixVersions().spliterator(), true).anyMatch(v -> {
			return v.getName().equalsIgnoreCase(version);
		});
	}

	public Long getId() {
		return issue.getId();
	}

	public Iterable<IssueLink> getIssueLinks() {
		return issue.getIssueLinks();
	}

	public String getIssueTypeName() {
		return issue.getIssueType().getName();
	}

	public String getKey() {
		return issue.getKey();
	}

	public Set<String> getLabels() {
		return issue.getLabels();
	}

	public BasicPriority getPriority() {
		return issue.getPriority();
	}

	public BasicProject getProject() {
		return issue.getProject();
	}

	private User getReporter() {
		return issue.getReporter();
	}
	
	public URI getSelf() {
		return issue.getSelf();
	}

	public String getStatusName() {
		return issue.getStatus().getName();
	}

	public String getSummary() {
		return issue.getSummary();
	}

	public TimeTracking getTimeTracking() {
		return issue.getTimeTracking();
	}

	public URI getTransitionsUri() {
		return issue.getTransitionsUri();
	}

	public DateTime getUpdateDate() {
		return issue.getUpdateDate();
	}

	public BasicVotes getVotes() {
		return issue.getVotes();
	}

	public URI getVotesUri() {
		return issue.getVotesUri();
	}

	public BasicWatchers getWatchers() {
		return issue.getWatchers();
	}

	public URI getWorklogUri() {
		return issue.getWorklogUri();
	}

	public Iterable<Worklog> getWorklogs() {
		return issue.getWorklogs();
	}

	public int hashCode() {
		return issue.hashCode();
	}

	public String toString() {
		return issue.toString();
	}
	
	public StatusHistory getHistory() {
		if(history == null) {			
			history = new StatusHistory(this);
			
			for (ChangelogGroup changelogGroup : issue.getChangelog()) {
				StreamSupport.stream(changelogGroup.getItems().spliterator(), true)
					.filter(item -> STATUS.equalsIgnoreCase(item.getField()))
					.forEach(item -> {
						history.add(new ChangelogItem(item, changelogGroup.getCreated()));
					});
			}
		}
		
		return history;
	}
	
	public List<StartEnd> getBlockedTimes() {
		if(blockedTimes == null)
			blockedTimes = blockedTimeRetrievalStrategy.getBlockedTimes(issue);
		
		return blockedTimes;
	}
	
	public Long getTotalBlockedTimeInMillis() {
		if(totalBlockedTime == null)
			totalBlockedTime = StreamSupport.stream(getBlockedTimes().spliterator(), true).mapToLong(StartEnd::getTimeElapsed).sum();
		
		return totalBlockedTime;
	}
	
	public DateTime getResolutionDate() {
		return getHistory().getResolutionDate();
	}

	public Object $(String name) {
		return this.getFieldByName(name).getValue();
	}

	public Object getValue(String key) {
		return getFieldByName(key).getValue();
	}
	
	public Sprint getSprint() {
		return new Sprint(getFieldByName("Sprint"));
	}

	public String getReporterName() {
		return getReporter().getName();
	}
}
