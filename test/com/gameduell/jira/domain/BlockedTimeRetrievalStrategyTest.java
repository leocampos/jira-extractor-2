package com.gameduell.jira.domain;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gameduell.jira.util.Context;
import com.gameduell.jira.util.StartEnd;

import static com.gameduell.jira.TestUtil.*;

public class BlockedTimeRetrievalStrategyTest {
	@Mock
	private Context context;
	
	private BlockedByLinksBlockedTimeStrategyImp imp;
	
	@Before
	public void setup() {
		 MockitoAnnotations.initMocks(this);
		 imp = new BlockedByLinksBlockedTimeStrategyImp(context);
	}
	
	@Test
	public void emptyListShouldBeKeptUnchanged() {
		List<StartEnd> blockedTimes = new ArrayList<>();
		
		 Assert.assertSame(blockedTimes, imp.join(blockedTimes));
	}

	@Test
	public void listOfOneItemShouldBeKeptUnchanged() {
		List<StartEnd> blockedTimes = new ArrayList<>();
		blockedTimes.add(new StartEnd(null, null));
		
		 Assert.assertSame(blockedTimes, imp.join(blockedTimes));
	}
	
	@Test
	public void listOfNoOverlappingTimesShouldBeKeptUnchanged() {
		List<StartEnd> blockedTimes = new ArrayList<>();
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(1), createDateTimeUsingDay(2)));
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(3), createDateTimeUsingDay(4)));
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(5), createDateTimeUsingDay(6)));
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(7), createDateTimeUsingDay(8)));
		
		 Assert.assertEquals(blockedTimes.size(), imp.join(blockedTimes).size());
	}
	
	@Test
	public void returnJustOneItemIfTheFirstEndsDuringTheSecond() {
		List<StartEnd> blockedTimes = new ArrayList<>();
		int startDay = 1;
		int endDay = 4;
		
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(startDay), createDateTimeUsingDay(3)));
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(2), createDateTimeUsingDay(endDay)));
		
		blockedTimes = imp.join(blockedTimes);
		
		 Assert.assertEquals(1, blockedTimes.size());
		 Assert.assertEquals(startDay, blockedTimes.get(0).getStart().getDayOfMonth());
		 Assert.assertEquals("End Day", endDay, blockedTimes.get(0).getEnd().getDayOfMonth());
	}
	
	@Test
	public void returnJustOneItemIfTheSecondIsEnclosedWithinFirst() {
		List<StartEnd> blockedTimes = new ArrayList<>();
		int startDay = 1;
		int endDay = 4;
		
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(startDay), createDateTimeUsingDay(endDay)));
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(2), createDateTimeUsingDay(3)));
		
		blockedTimes = imp.join(blockedTimes);
		
		 Assert.assertEquals(1, blockedTimes.size());
		 Assert.assertEquals(startDay, blockedTimes.get(0).getStart().getDayOfMonth());
		 Assert.assertEquals("End Day", endDay, blockedTimes.get(0).getEnd().getDayOfMonth());
	}
	
	@Test
	public void returnTwoItemsIfTheThirdAndFourthAreEnclosedWithinSecond() {
		List<StartEnd> blockedTimes = new ArrayList<>();
		int startDay = 1;
		int endDay = 7;
		
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(startDay), createDateTimeUsingDay(2)));
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(3), createDateTimeUsingDay(endDay)));
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(4), createDateTimeUsingDay(6)));
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(5), createDateTimeUsingDay(6)));
		
		blockedTimes = imp.join(blockedTimes);
		
		 Assert.assertEquals("Number of items", 2, blockedTimes.size());
		 Assert.assertEquals(startDay, blockedTimes.get(0).getStart().getDayOfMonth());
		 Assert.assertEquals("End Day", endDay, blockedTimes.get(1).getEnd().getDayOfMonth());
	}
	
	@Test
	public void returnJustOneItemIfFirstItemHasNotFinished() {
		List<StartEnd> blockedTimes = new ArrayList<>();
		int startDay = 1;
		
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(startDay), null));
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(2), createDateTimeUsingDay(3)));
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(3), createDateTimeUsingDay(4)));
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(3), createDateTimeUsingDay(5)));
		
		blockedTimes = imp.join(blockedTimes);
		
		 Assert.assertEquals(1, blockedTimes.size());
		 Assert.assertEquals(startDay, blockedTimes.get(0).getStart().getDayOfMonth());
		 Assert.assertNull("End Day", blockedTimes.get(0).getEnd());
	}
	
	@Test
	public void returnTwoItemsIfSecondItemHasNotFinished() {
		List<StartEnd> blockedTimes = new ArrayList<>();
		int startDay = 3;
		
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(1), createDateTimeUsingDay(2)));
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(startDay), null));
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(4), createDateTimeUsingDay(5)));
		blockedTimes.add(new StartEnd(createDateTimeUsingDay(5), createDateTimeUsingDay(6)));
		
		blockedTimes = imp.join(blockedTimes);
		
		 Assert.assertEquals(2, blockedTimes.size());
		 Assert.assertEquals(startDay, blockedTimes.get(1).getStart().getDayOfMonth());
		 Assert.assertNull("End Day", blockedTimes.get(1).getEnd());
	}
}
