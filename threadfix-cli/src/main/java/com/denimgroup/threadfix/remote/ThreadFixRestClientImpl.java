////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2013 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.remote;

import com.denimgroup.threadfix.properties.PropertiesManager;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ThreadFixRestClientImpl implements ThreadFixRestClient {

    final HttpRestUtils httpRestUtils;
    final PropertiesManager propertiesManager;

	/**
	 * Default constructor that will read configuration from a local .properties file
	 */
	public ThreadFixRestClientImpl() {
        propertiesManager = new PropertiesManager();
        httpRestUtils = new HttpRestUtils(propertiesManager);
	}
	
	/**
	 * Custom constructor for when you want to use the in-memory properties
	 * 
	 * @param url URL for the ThreadFix server
	 * @param apiKey API key to use when accessing the ThreadFix server
	 */
	public ThreadFixRestClientImpl(String url, String apiKey) {
        propertiesManager = new PropertiesManager();
        propertiesManager.setMemoryKey(apiKey);
        propertiesManager.setMemoryUrl(url);
        httpRestUtils = new HttpRestUtils(propertiesManager);
	}
	
	public String createApplication(String teamId, String name, String url) {
        return httpRestUtils.httpPost("/teams/" + teamId + "/applications/new",
                new String[] { "name", "url"},
                new String[] {  name,   url});
	}
	
	public String setParameters(String appId, String frameworkType, String repositoryUrl) {
		return httpRestUtils.httpPost("/applications/" + appId + "/setParameters",
				new String[] {"frameworkType", "repositoryUrl"},
				new String[] { frameworkType,   repositoryUrl});
	}
	
	public String createTeam(String name) {
		return httpRestUtils.httpPost("/teams/new",
				new String[] {"name"},
				new String[] { name });
	}
	
	public String getRules(String wafId) {
		return httpRestUtils.httpGet("/wafs/" + wafId + "/rules");
	}

	public String searchForWafByName(String name) {
		return httpRestUtils.httpGet("/wafs/lookup", "&name=" + name);
	}
	
	public String searchForWafById(String wafId) {
		return httpRestUtils.httpGet("/wafs/" + wafId);
	}
	
	public String createWaf(String name, String type) {
		return httpRestUtils.httpPost("/wafs/new",
				new String[] {"name", "type"},
				new String[] { name,   type});
	}
	
	/**
	 * TODO - Actually implement this method.
	 * 
	 * @param appId
	 * @param wafId
	 * @return
	 */
	public String addWaf(String appId, String wafId) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getAllTeams() {
		return httpRestUtils.httpGet("/teams/");
	}
    
    public String getAllTeamsPrettyPrint() {
        final String result = httpRestUtils.httpGet("/teams/");

        final ObjectMapper objectMapper = new ObjectMapper();

        final List<Map<String, Object>> teamsData;

        try {
            teamsData = objectMapper.readValue(result, new TypeReference<List<Map<String, Object>>>() {});
        } catch (final IOException e) {
            e.printStackTrace();
            return "There was an error parsing JSON response.";
        }

        if (teamsData.isEmpty()) {
            return "These aren't the droids you're looking for.";
        } else {
            final StringBuilder outputBuilder = new StringBuilder();

            for (final Map<String, Object> teamData : teamsData) {
                final Boolean teamActive = (Boolean) teamData.get("active");
                @SuppressWarnings("unchecked")
                final List<Map<String, Object>> applications = (List<Map<String, Object>>) teamData.get("applications");

                if (teamActive && !applications.isEmpty()) {
                    final String teamName = (String) teamData.get("name");

                    for (final Map<String, Object> application : applications) {
                        final Boolean applicationActive = (Boolean) application.get("active");

                        if (applicationActive) {
                            final String applicationName = (String) application.get("name");
                            final Integer id = (Integer) application.get("id");

                            outputBuilder.append(teamName);
                            outputBuilder.append(";");
                            outputBuilder.append(applicationName);
                            outputBuilder.append(";");
                            outputBuilder.append(id);
                            outputBuilder.append("\n");
                        }
                    }
                }
            }

            outputBuilder.setLength(outputBuilder.length() - 1);
            return outputBuilder.toString();
        }
    }	

	public String searchForApplicationById(String id) {
		return httpRestUtils.httpGet("/applications/" + id);
	}

	public String searchForApplicationByName(String name, String teamName) {
		return httpRestUtils.httpGet("/applications/" + teamName + "/lookup",
				"&name=" + name);
	}
	
	public String searchForTeamById(String id) {
		return httpRestUtils.httpGet("/teams/" + id);
	}
	
	public String searchForTeamByName(String name) {
		return httpRestUtils.httpGet("/teams/lookup", "&name=" + name);
    }
	
	public void setKey(String key) {
        propertiesManager.setKey(key);
	}

	public void setUrl(String url) {
        propertiesManager.setUrl(url);
	}
	
	public void setMemoryKey(String key) {
        propertiesManager.setMemoryKey(key);
	}
	
	public void setMemoryUrl(String url) {
        propertiesManager.setMemoryUrl(url);
	}
	
	public String uploadScan(String applicationId, String filePath) {
		return httpRestUtils.httpPostFile("/applications/" + applicationId + "/upload",
                filePath, new String[]{}, new String[]{});
	}
	
	public String queueScan(String applicationId, String scannerType) {
		return httpRestUtils.httpPost("/tasks/queueScan",
				new String[] { "applicationId", "scannerType" },
				new String[] { applicationId, scannerType });
	}

	public String addAppUrl(String appId, String url) {
		return httpRestUtils.httpPost("/applications/" + appId + "/addUrl",
				new String[] {"url"},
				new String[] { url });
	}
	
	public String requestTask(String scanners, String agentConfig) {
		return httpRestUtils.httpPost("/tasks/requestTask",
				new String[] {"scanners", "agentConfig" },
				new String[] { scanners, agentConfig });
	}
	
	/**
	 * Determine if we want to pass the taskId as a parameter or if we want to REST it up
	 * @param scanQueueTaskId
	 * @param message
	 * @return
	 */
	public String taskStatusUpdate(String scanQueueTaskId, String message) {
		return httpRestUtils.httpPost("/tasks/taskStatusUpdate",
                new String[]{"scanQueueTaskId", "message"},
                new String[]{ scanQueueTaskId, message});
	}
	
	public String setTaskConfig(String appId, String scannerType, String filePath) {
		String url = "/tasks/setTaskConfig";
		String[] paramNames 	= {	"appId", "scannerType" };
		String[] paramValues 	= { appId, scannerType };
		return httpRestUtils.httpPostFile(url, filePath, paramNames, paramValues );
	}
	
	/**
	 * TODO - Determine if we want to pass the scanQueueTaskId as a parameter or if we want to REST it up
	 * @param filePath
	 * @param secureTaskKey
	 * @return
	 */
	public String completeTask(String scanQueueTaskId, String filePath, String secureTaskKey) {
		String url = "/tasks/completeTask";
		String[] paramNames 	= {	"scanQueueTaskId", "secureTaskKey" };
		String[] paramValues 	= {  scanQueueTaskId,   secureTaskKey };
	    return httpRestUtils.httpPostFile(url, filePath, paramNames, paramValues);
	}
	
	public String failTask(String scanQueueTaskId, String message, String secureTaskKey) {
		return httpRestUtils.httpPost("/tasks/failTask",
				new String[] {"scanQueueTaskId", "message", "secureTaskKey" },
				new String[] { scanQueueTaskId,	  message,   secureTaskKey });
	}

	public String addDynamicFinding(String applicationId, String vulnType, String severity,
		String nativeId, String parameter, String longDescription,
		String fullUrl, String path) {
		return httpRestUtils.httpPost("/applications/" + applicationId +
					"/addFinding",
				new String[] {"vulnType", "severity",
								"nativeId", "parameter", "longDescription",
								"fullUrl", "path" },
				new String[] {  vulnType, severity,
								nativeId, parameter, longDescription,
								fullUrl, path });
	}
	
	public String addStaticFinding(String applicationId, String vulnType, String severity,
			String nativeId, String parameter, String longDescription,
			String filePath, String column, String lineText, String lineNumber) {
		return httpRestUtils.httpPost("/applications/" + applicationId +
				"/addFinding",
				new String[] {"vulnType", "severity",
								"nativeId", "parameter", "longDescription",
								"filePath", "column", "lineText", "lineNumber"},
				new String[] {  vulnType, severity,
								nativeId, parameter, longDescription,
								filePath, column, lineText, lineNumber });
	}

}
