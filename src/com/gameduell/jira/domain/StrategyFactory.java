package com.gameduell.jira.domain;

import com.gameduell.jira.util.Context;

public class StrategyFactory {
	public static BlockedTimeRetrievalStrategy createBlockedTimeRetrievalStrategyInstance(String type, Context context) {
		if(Config.STRATEGY_BLOCKED_BY_LINKS.equalsIgnoreCase(type))
			return new BlockedByLinksBlockedTimeStrategyImp(context);
		
		return null;
	}
}
