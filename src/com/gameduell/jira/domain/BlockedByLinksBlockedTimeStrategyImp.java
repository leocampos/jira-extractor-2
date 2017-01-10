package com.gameduell.jira.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

import org.joda.time.DateTime;

import com.atlassian.jira.rest.client.api.domain.ChangelogGroup;
import com.atlassian.jira.rest.client.api.domain.ChangelogItem;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.gameduell.jira.JiraClient;
import com.gameduell.jira.util.StartEnd;

public class BlockedByLinksBlockedTimeStrategyImp implements BlockedTimeRetrievalStrategy {
	private static final String THIS_ISSUE_IS_BLOCKED_BY = "This issue is blocked by";
	private static final String LINK = "Link";
	private JiraClient client;
	
	public BlockedByLinksBlockedTimeStrategyImp() {
		this.client = JiraClient.getCurrentInstanceJiraClient();
	}

	@Override
	public List<StartEnd> getBlockedTimes(Issue issue) {
		return join(getBlockedDates(issue));
	}

	private List<StartEnd> getBlockedDates(Issue issue) {
		List<StartEnd> blockedTimes = new ArrayList<>();
		
		for (ChangelogGroup changelogGroup : issue.getChangelog()) {
			getBlockedTimesForChangelogGroup(blockedTimes, changelogGroup);
		}
		
		return blockedTimes;
	}

	private void getBlockedTimesForChangelogGroup(List<StartEnd> blockedTimes, ChangelogGroup changelogGroup) {
		StreamSupport.stream(changelogGroup.getItems().spliterator(), true)
				.filter(item -> isBeingBlocked(item)).forEach(item -> {
					addBlockedTime(blockedTimes, changelogGroup, item);
				});
	}

	private boolean isBeingBlocked(ChangelogItem item) {
		return LINK.equalsIgnoreCase(item.getField()) && item.getToString().startsWith(THIS_ISSUE_IS_BLOCKED_BY);
	}

	private void addBlockedTime(List<StartEnd> blockedTimes, ChangelogGroup changelogGroup, ChangelogItem item) {
		blockedTimes.add(new StartEnd(changelogGroup.getCreated(), getEndDate(getBlockerIssue(item))));
	}

	protected List<StartEnd> join(List<StartEnd> blockedTimes) {
		if(blockedTimes == null || blockedTimes.size() <= 1) return blockedTimes;

		sortByStartingDates(blockedTimes);
		
		return joinSorted(blockedTimes);
	}

	private List<StartEnd> joinSorted(List<StartEnd> blockedTimes) {
		List<StartEnd> joinedDates = new ArrayList<>();
		
		StartEnd actual, next = null;
		while(blockedTimes.size() >= 1) {
			actual = getFirst(blockedTimes);
			
			if(isSingleItem(blockedTimes, actual)) {
				joinedDates.add(actual);
				return joinedDates;
			}

			if(noIntersection(actual, next = getSecond(blockedTimes))) {
				joinedDates.add(actual);
				consumeFirst(blockedTimes);
			} else
				treatIntersection(blockedTimes, actual, next);
		}
	
		return joinedDates;
	}

	private boolean isSingleItem(List<StartEnd> blockedTimes, StartEnd actual) {
		return hasntFinishedYet(actual) || isSingleItem(blockedTimes);
	}

	private void treatIntersection(List<StartEnd> blockedTimes, StartEnd actual, StartEnd next) {
		if(isNextContainedWithinFirst(actual, next)) {
			consumeSecond(blockedTimes);
		} else {
			actual.setEnd(next.getEnd());
			consumeSecond(blockedTimes);
		}
	}

	private boolean isNextContainedWithinFirst(StartEnd actual, StartEnd next) {
		return next.endsBefore(actual.getEnd());
	}

	private boolean noIntersection(StartEnd actual, StartEnd next) {
		return next.isStartAfter(actual.getEnd());
	}

	private boolean isSingleItem(List<StartEnd> blockedTimes) {
		return blockedTimes.size() == 1;
	}

	private boolean hasntFinishedYet(StartEnd actual) {
		return actual.getEnd() == null;
	}

	private void consumeSecond(List<StartEnd> blockedTimes) {
		blockedTimes.remove(1);
	}

	private void consumeFirst(List<StartEnd> blockedTimes) {
		blockedTimes.remove(0);
	}

	private StartEnd getSecond(List<StartEnd> blockedTimes) {
		return blockedTimes.get(1);
	}

	private StartEnd getFirst(List<StartEnd> blockedTimes) {
		return blockedTimes.get(0);
	}

	private void sortByStartingDates(List<StartEnd> blockedTimes) {
		blockedTimes.sort(null);
	}

	private DateTime getEndDate(ExpandedIssue blockerIssue) {
		return blockerIssue.getResolutionDate();
	}

	private ExpandedIssue getBlockerIssue(ChangelogItem item) {
		return client.findByKeyWithChangelog(item.getTo());
	}
}
