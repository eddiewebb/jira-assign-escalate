package com.edwardawebb.jira.assignescalate.workflow;

import java.util.HashMap;
import java.util.Map;

import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

/*
This is the factory class responsible for dealing with the UI for the post-function.
This is typically where you put default values into the velocity context and where you store user input.
 */

public class AssignLevelOneSupportPostFunctionFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory{
    private static final Logger log = LoggerFactory.getLogger(AssignLevelOneSupportPostFunction.class);
    
    public static final String FIELD_TEAM="teamName";
    public static final String FIELD_COMPONENT = "componentMatch";



    @Override
    protected void getVelocityParamsForInput(Map<String, Object>velocityParams){

      
        //the default message
        velocityParams.put(FIELD_TEAM,"Enter team Name:");

    }

    @Override
    protected void getVelocityParamsForEdit(Map<String, Object>velocityParams,AbstractDescriptor descriptor){

        getVelocityParamsForInput(velocityParams);
        getVelocityParamsForView(velocityParams, descriptor);

    }

    @Override
    protected void getVelocityParamsForView(Map<String, Object>velocityParams,AbstractDescriptor descriptor){
        if(!(descriptor instanceof FunctionDescriptor))
        {
            throw new IllegalArgumentException("Descriptor must be a FunctionDescriptor.");
        }

        FunctionDescriptor functionDescriptor=(FunctionDescriptor)descriptor;

        String message=(String)functionDescriptor.getArgs().get(FIELD_TEAM);
        boolean isComponentMatch=false;
        if(functionDescriptor.getArgs().containsKey(FIELD_COMPONENT)){
            log.warn("Component check field has value: ==" + (String)functionDescriptor.getArgs().get(FIELD_COMPONENT) +"==");
            isComponentMatch = Boolean.parseBoolean((String)functionDescriptor.getArgs().get(FIELD_COMPONENT));
            log.warn("Show as matcher: " + isComponentMatch);
        }



        velocityParams.put(FIELD_TEAM,message);
        if(isComponentMatch){
            velocityParams.put(FIELD_COMPONENT,"checked");
        }
    }


    public Map<String,?>getDescriptorParams(Map<String, Object>formParams){
        Map params=new HashMap();

        // Process The map
        String message=extractSingleParam(formParams,FIELD_TEAM);
        boolean isComponentMatch=formParams.containsKey(FIELD_COMPONENT);
        log.warn("Will use comp: " + isComponentMatch);
        params.put(FIELD_TEAM,message);
        params.put(FIELD_COMPONENT, isComponentMatch);
        return params;
    }

}