package com.gameduell.jira.util;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

public class StartEnd implements Comparable<StartEnd> {
	private DateTime start, end;

	public StartEnd(DateTime start, DateTime end) {
		this.start = start;
		this.end = end;
	}

	public DateTime getStart() {
		return start;
	}

	public void setStart(DateTime start) {
		this.start = start;
	}

	public DateTime getEnd() {
		return end;
	}

	public void setEnd(DateTime end) {
		this.end = end;
	}
	
	public Long getTimeElapsed() {
		if(end == null)
			return DateTimeUtils.currentTimeMillis() - start.getMillis();
		
		return end.getMillis() - start.getMillis();
	}
	
	@Override
	public String toString() {
		return String.format("FROM %s TO %s", start, end);
	}

	@Override
	public int compareTo(StartEnd other) {
		return start.compareTo(other.start);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StartEnd other = (StartEnd) obj;
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}

	public boolean isStartAfter(DateTime date) {
		if(start == null) return false;
		
		return start.isAfter(date);
	}

	public boolean endsBefore(DateTime date) {
		if(end == null) return false;
		return end.isBefore(date);
	}
}
