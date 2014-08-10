package com.edwardawebb.jira.assignescalate.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;
import java.util.Map;
import com.atlassian.jira.issue.MutableIssue;


/*
This is the post-function class that gets executed at the end of the transition.
Any parameters that were saved in your factory class will be available in the transientVars Map.
 */

public class EscalateLevelTwoPostFunction extends AbstractJiraFunctionProvider{
    private static final Logger log = LoggerFactory.getLogger(EscalateLevelTwoPostFunction.class);
    public static final String FIELD_MESSAGE="messageField";

    public void execute(Map transientVars,Map args,PropertySet ps)throws WorkflowException{
        MutableIssue issue=getIssue(transientVars);
        String message = (String)transientVars.get(FIELD_MESSAGE);

        if(null == message) {
            message = "";
        }

        issue.setDescription(issue.getDescription() + message);
    }
}