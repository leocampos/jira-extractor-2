package com.gameduell.jira.domain;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.atlassian.jira.rest.client.api.domain.IssueField;

public class Sprint {
	private IssueField sprint;

	public Sprint(IssueField sprint) {
		this.sprint = sprint;
	}
	
	public String getName() {
		Object value = sprint.getValue();
		
		if(value instanceof JSONArray) {
			JSONArray val = (JSONArray)value;
			StringBuilder sprintNames = new StringBuilder();
			
			try {
				for(int i = 0; i < val.length(); i++) {
					if(!(val.get(i) instanceof JSONObject))
						continue;
					
					sprintNames.append(val.getJSONObject(i).get("name"));
					sprintNames.append(",");
				}
				
				return val.join(",");
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return sprint.getName();
	}
}
