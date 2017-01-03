package com.gameduell.jira;

import org.joda.time.DateTime;

public class TestUtil {
	public static DateTime createDateTimeUsingDay(int day) {
		return new DateTime(2016, 12, day, 11, 17, 0, 0);
	}
}
