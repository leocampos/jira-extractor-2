package com.gameduell.jira;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import com.gameduell.jira.domain.ExpandedIssue;

/**
 * @author lcam
 * this is just an example class, not really part of the library.
 */
public class Client {
	private static final String IN_PROGRESS = "IN PROGRESS";
	private static final Pattern datePattern = Pattern.compile("(\\d{4}-\\d{2}-\\d{2})T(\\d{2}:\\d{2}):\\d{2}\\.\\d{3}\\+\\d{2}:\\d{2}");
	private static final String SURE_BLOCKED_ISSUES = "project = SURE AND issueFunction in linkedIssuesOf(\"status IN (Closed, Resolved)\", blocks)";

	public static void main(String[] args) {
		JiraClient jiraClient = JiraClient.createLoggedClient();

		List<ExpandedIssue> expIssues = jiraClient.findWithChangelog(SURE_BLOCKED_ISSUES);
		expIssues.forEach(issue -> {
			System.out.println(issue.getBlockedTimes());
		});

		expIssues.forEach(issue -> System.out.println(String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s", format(issue.getCreationDate()),
				format(issue.getHistory().getFirstTimeInStatus(IN_PROGRESS)), format(issue.getResolutionDate()), issue.getKey(),
				issue.getValue("Story Points"), issue.getValue("Actual Effort"),
				issue.getLabels(), issue.getIssueTypeName(), issue.getReporterName())));
		
		System.exit(0);
	}

	private static String format(DateTime date) {
		if(date == null) return "";
		
		String unformatedDate = date.toString();
		Matcher matcher = datePattern.matcher(unformatedDate);
		
		if(matcher.matches())
			return matcher.group(1) + " " + matcher.group(2);
		
		
		return unformatedDate;
	}
}