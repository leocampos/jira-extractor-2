package com.gameduell.jira.domain;

import static com.gameduell.jira.TestUtil.createDateTimeUsingDay;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.gameduell.jira.util.StartEnd;

public class ExpandedIssueTest {
	private ExpandedIssue expandedIssue;
	
	@Mock
	private Issue issue;
	
	@Mock
	private BlockedTimeRetrievalStrategy blockedTimeRetrievalStrategy;
	
	@Before
	public void setup() {
		 MockitoAnnotations.initMocks(this);
		 expandedIssue = new ExpandedIssue(issue, blockedTimeRetrievalStrategy);
	}
	

	@Test
	public void itemWithNoHistoryOfBeingBlockedShouldReturnZeroBlockedTime() {
		Assert.assertEquals(new Long(0), expandedIssue.getTotalBlockedTimeInMillis());
	}
	
	@Test
	public void itemWithOneItemBlockingShouldReturnTheTimeOfThatBlocker() {
		List<StartEnd> blockedTimes = new ArrayList<>();
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(1), createDateTimeUsingDay(2)));
		
		Mockito.when(blockedTimeRetrievalStrategy.getBlockedTimes(issue)).thenReturn(blockedTimes);
		
		Assert.assertEquals(new Long(daysInMillis(1)), expandedIssue.getTotalBlockedTimeInMillis());
	}
	
	
	@Test
	public void itemSeveralItemsBlockingShouldReturnTheSumOfTheTimeBlocked() {
		List<StartEnd> blockedTimes = new ArrayList<>();
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(1), createDateTimeUsingDay(2)));
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(3), createDateTimeUsingDay(4)));
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(5), createDateTimeUsingDay(8)));
		
		Mockito.when(blockedTimeRetrievalStrategy.getBlockedTimes(issue)).thenReturn(blockedTimes);
		
		Assert.assertEquals(new Long(daysInMillis(5)), expandedIssue.getTotalBlockedTimeInMillis());
	}


	private int daysInMillis(int days) {
		return days * 24 * 60 * 60 * 1000;
	}
}
 