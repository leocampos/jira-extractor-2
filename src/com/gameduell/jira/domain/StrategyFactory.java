package com.gameduell.jira.domain;

public class StrategyFactory {
	public static BlockedTimeRetrievalStrategy createBlockedTimeRetrievalStrategyInstance(String type) {
		if(Config.STRATEGY_BLOCKED_BY_LINKS.equalsIgnoreCase(type))
			return new BlockedByLinksBlockedTimeStrategyImp();
		
		return null;
	}
}
