package com.edwardawebb.jira.assignescalate.workflow;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.edwardawebb.jira.assignescalate.AssignmentService;
import com.edwardawebb.jira.assignescalate.ao.SupportMember;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;


/*
This is the post-function class that gets executed at the end of the transition.
Any parameters that were saved in your factory class will be available in the transientVars Map.
 */

public class AssignLevelOneSupportPostFunction extends AbstractJiraFunctionProvider{
    private static final Logger log = LoggerFactory.getLogger(AssignLevelOneSupportPostFunction.class);
    private AssignmentService assignmentService;
    public static String TEAM_FIELD ="teamName";
    

    public AssignLevelOneSupportPostFunction(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @Override
    public void execute(Map transientVars, Map args, PropertySet ps) throws WorkflowException {
       
        try {
            MutableIssue issue = getIssue(transientVars);
            String teamName = (String)args.get(TEAM_FIELD);
            assignIssue(issue, teamName);
        } catch (WorkflowException e) {
            log.error("Error occurred while assigning the issue", e);
            e.printStackTrace();
        } catch (UnconfiguredWorkflowFunctionException e) {
            log.warn("A project admin needs help! issue ", e);
        }
    }

    private void assignIssue(MutableIssue issue, String teamName) throws WorkflowException, UnconfiguredWorkflowFunctionException {
        log.warn("Auto Assign to SUpport Person Post Workflow Function Running for team " + teamName);
        Long projectId = issue.getProjectId();
        SupportMember sucker = assignmentService.assignNextAvailableAssigneeForProjectTeam(projectId, teamName);
        if(null == sucker){
            throw new UnconfiguredWorkflowFunctionException();
        }
        log.warn("Assigning: " + sucker.getPrincipleName());
        issue.setAssigneeId(sucker.getPrincipleName());
    }
}