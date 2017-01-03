package com.gameduell.jira.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.util.concurrent.Promise;
import com.gameduell.jira.domain.Config;

public class JiraIssuesIterator implements Iterator<Issue> {
	private SearchRestClient searchClient;
    private int pageSize;
    private String jql;
    private Set<String> fields;

    private int actualPage = 0;
    private int issueTotal;
    private Iterator<Issue> iterator;

    private SearchResult searchResult;
	private Integer maxNumberOfItems;
	private int actualItem = 0;
    
    public JiraIssuesIterator(String jql, Config config, SearchRestClient searchClient) {
		this.searchClient = searchClient;
		this.jql = jql;
		this.pageSize = config.getPageSize();
		this.maxNumberOfItems = config.getMaxNumberOfItems();
    }
    
    public void setSearchResult(SearchResult searchResult) {
		this.searchResult = searchResult;
	}
    
    public void setIterator(Iterator<Issue> iterator) {
		this.iterator = iterator;
	}

	@Override
    public boolean hasNext() {
    	setupIfFirstTime();
    	
    	if(isMaxExausted()) return false;
    	if(hasNextInActualPage()) return true;
    	if(isLastPage()) return false;
    	
    	turnPage();
    	search();
    	
        return hasNext();
    }

	private boolean isMaxExausted() {
		return maxNumberOfItems != null && actualItem >= maxNumberOfItems;
	}

	private void turnPage() {
		actualPage++;
	}

	private boolean isLastPage() {
		return lastIndexForPage() > issueTotal;
	}

	private int lastIndexForPage() {
		return (actualPage + 1) * pageSize;
	}

	private void setupIfFirstTime() {
		if(searchResult != null) return;
		
		search();
		issueTotal = searchResult.getTotal();
	}

    @Override
    public Issue next() {
    	if(!hasNext()) throw new NoSuchElementException();
    	
		actualItem++;
		return iterator.next();
    }


	private boolean hasNextInActualPage() {
		return (iterator.hasNext());
	}
	
	public Set<String> getFields() {
		return Collections.unmodifiableSet(fields);
	}

    private void search() {
        Promise<SearchResult> searchJql = searchClient.searchJql(jql, pageSize, actualPage * pageSize, fields);
		searchResult = searchJql.claim();
        iterator = searchResult.getIssues().iterator();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
