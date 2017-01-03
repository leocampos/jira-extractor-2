package com.gameduell.jira.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.time.DateTime;

public class StatusHistory {
	private static final String RESOLVED_STATUS = "RESOLVED";
	private static final String CLOSED_STATUS = "CLOSED";

	private List<ChangelogItem> items;
	private DateTime resolutionDate;
	private Set<String> CLOSED_STATUSES = Stream.of(CLOSED_STATUS, RESOLVED_STATUS).collect(Collectors.toSet());
	private ExpandedIssue issue;
	
	private Map<String, List<DateTime>> timesInStatus = null;
	private Map<String, Long> timeSpentInStatus = null;

	public StatusHistory(List<ChangelogItem> items, ExpandedIssue expandedIssue) {
		this.items = items;
		this.issue = expandedIssue;
	}

	public StatusHistory(ExpandedIssue expandedIssue) {
		this(new ArrayList<>(), expandedIssue);
	}

	public boolean add(ChangelogItem e) {
		return items.add(e);
	}

	public boolean contains(Object o) {
		return items.contains(o);
	}

	public Iterator<ChangelogItem> iterator() {
		return items.iterator();
	}

	public int size() {
		return items.size();
	}
	
	public DateTime getResolutionDate() {
		if(resolutionDate != null) return resolutionDate;
		if(items == null || items.isEmpty()) return null;
		if(!isIssueResolved()) return null;
		
		resolutionDate = items.get(items.size()-1).getCreated();
		
		return resolutionDate;
	}

	private boolean isIssueResolved() {
		return CLOSED_STATUSES.contains(issue.getStatusName().toUpperCase());
	}

	public DateTime getFirstTimeInStatus(String statusName) {
		prepareDataStructure();
		
		if(timesInStatus == null || !timesInStatus.containsKey(statusName.toUpperCase())) return null;
		
		return timesInStatus.get(statusName.toUpperCase()).get(0);
	}

	private void prepareDataStructure() {
		if(isAlreadyPrepared()) return;
		
		createTimesInStatusDataStructure();
		createTimeSpentInEachStatusStructure();
	}

	private void createTimeSpentInEachStatusStructure() {
		timeSpentInStatus = new HashMap<>();
		
		if(items == null || items.isEmpty()) return;
		DateTime previousStatusTime = issue.getCreationDate();
		long timeSpent = 0;
		
		for (ChangelogItem changelogItem : items) {
			timeSpent = changelogItem.getCreated().getMillis() - previousStatusTime.getMillis();
			
			if(timeSpentInStatus.containsKey(changelogItem.getTo()))
				timeSpent += timeSpentInStatus.get(changelogItem.getTo());
			
			timeSpentInStatus.put(changelogItem.getTo(), timeSpent);
			
			previousStatusTime = changelogItem.getCreated();
		}
	}

	private void createTimesInStatusDataStructure() {
		if(items == null || items.isEmpty()) return;
		
		timesInStatus = new HashMap<>();
		for (ChangelogItem changelogItem : items) {
			addTimeForStatus(changelogItem);
		}
	}

	private void addTimeForStatus(ChangelogItem changelogItem) {
		if(isNewStatus(changelogItem))
			timesInStatus.put(changelogItem.getTo(), new ArrayList<>());
		
		timesInStatus.get(changelogItem.getTo()).add(changelogItem.getCreated());
	}

	private boolean isNewStatus(ChangelogItem item) {
		return !timesInStatus.containsKey(item.getTo().toUpperCase());
	}

	private boolean isAlreadyPrepared() {
		return timesInStatus != null;
	}
}
