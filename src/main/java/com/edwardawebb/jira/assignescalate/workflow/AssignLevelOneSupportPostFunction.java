package com.edwardawebb.jira.assignescalate.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.edwardawebb.jira.assignescalate.AssignmentService;
import com.edwardawebb.jira.assignescalate.ao.SupportMember;
import com.edwardawebb.jira.assignescalate.ao.SupportTeam;
import com.google.common.collect.Lists;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;


/*
This is the post-function class that gets executed at the end of the transition.
Any parameters that were saved in your factory class will be available in the transientVars Map.
 */

public class AssignLevelOneSupportPostFunction extends AbstractJiraFunctionProvider{
    private static final Logger log = LoggerFactory.getLogger(AssignLevelOneSupportPostFunction.class);
    private AssignmentService assignmentService;
    

    public AssignLevelOneSupportPostFunction(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @Override
    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
       
        try {
            MutableIssue issue = getIssue(transientVars);
            assignIssue(args,issue);
        } catch (WorkflowException e) {
            log.error("Error occurred while assigning the issue", e);
            e.printStackTrace();
        } catch (UnconfiguredWorkflowFunctionException e) {
            log.warn("A project admin needs help! issue ", e);
        }
    }

    private void assignIssue(Map args, MutableIssue issue) throws WorkflowException, UnconfiguredWorkflowFunctionException {
        log.warn("Auto Assign to SUpport Person Post Workflow Function Running for team " + issue);
        if(null == issue.getAssigneeId() || issue.getAssigneeId() == "" || isOverrideAllowed(args)) {
            Long projectId = issue.getProjectId();
            String teamName = getAppropriateTeam(args, issue, projectId);
            log.warn("Grabbing next person from " + teamName);
            SupportMember sucker = assignmentService.assignNextAvailableAssigneeForProjectTeam(projectId, teamName);
            if (null == sucker) {
                log.error("No available members or team does not exist!!  teamName:" + teamName + " for project: " + projectId);
                throw new UnconfiguredWorkflowFunctionException();
            }
            log.warn("Assigning: " + sucker.getPrincipleName());
            issue.setAssigneeId(sucker.getPrincipleName());
        }else{
            log.debug("SKipping assignment as assignee is not empty and override not set.");
        }
    }

    private boolean isOverrideAllowed(Map args) {
        return Boolean.valueOf((String)args.get(AssignLevelOneSupportPostFunctionFactory.FIELD_OVERRIDE)) == true;
    }

    private String getAppropriateTeam(Map args, MutableIssue issue, Long projectId) {
        String fallbackTeam = (String) args.get(AssignLevelOneSupportPostFunctionFactory.FIELD_TEAM);
        
        if(args.containsKey(AssignLevelOneSupportPostFunctionFactory.FIELD_COMPONENT)){
            boolean isMatching = Boolean.parseBoolean((String)args.get(AssignLevelOneSupportPostFunctionFactory.FIELD_COMPONENT));
            if (isMatching){
                log.warn("Attempring component match, using fallback of:" + fallbackTeam);
               return attemptTeamForComponentsOf(projectId, issue,fallbackTeam);
            }
        }
        
        return fallbackTeam;
    }

    private String attemptTeamForComponentsOf(Long projectId, MutableIssue issue, String fallbackTeam) {
        Collection<ProjectComponent> components = issue.getComponentObjects();
        if(components.size() == 0) {
            log.debug("Looking for component macth,but no components define on ticket");
            return fallbackTeam;
        }
        List<SupportTeam> matchingTeams = new ArrayList<SupportTeam>();
        for (ProjectComponent component : components) {
            SupportTeam[] teams = assignmentService.findAllTeamsWith(projectId, component);
            if (null != teams){
                matchingTeams.addAll(Arrays.asList(teams));
            }
        }
        if(matchingTeams.size() != 1){
            log.warn("Too many or not enough support teams found for: {}. count: {}",components,matchingTeams.size());
            return fallbackTeam;
        }else{
            log.warn("Returning mtching team {} for components {}",matchingTeams.get(0),components);
            return matchingTeams.get(0).getName();
        }
        
        
        
    }

}