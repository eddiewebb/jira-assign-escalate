package com.edwardawebb.jira.assignescalate.workflow;

import java.util.HashMap;
import java.util.Map;

import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;

/*
This is the factory class responsible for dealing with the UI for the post-function.
This is typically where you put default values into the velocity context and where you store user input.
 */

public class AssignLevelOneSupportPostFunctionFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory{

    public static final String FIELD_TEAM="teamName";
    public static final String FIELD_COMPONENT = "componentMatch";



    @Override
    protected void getVelocityParamsForInput(Map<String, Object>velocityParams){

      
        //the default message
        velocityParams.put(FIELD_TEAM,"Enter team Name:");
        velocityParams.put(FIELD_COMPONENT,"Match Components first?");

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
        boolean isComponentMatch=(Boolean)functionDescriptor.getArgs().get(FIELD_COMPONENT);



        velocityParams.put(FIELD_TEAM,message);
        velocityParams.put(FIELD_COMPONENT,isComponentMatch);
    }


    public Map<String,?>getDescriptorParams(Map<String, Object>formParams){
        Map params=new HashMap();

        // Process The map
        String message=extractSingleParam(formParams,FIELD_TEAM);
        boolean isComponentMatch=Boolean.getBoolean(extractSingleParam(formParams,FIELD_COMPONENT));
        params.put(FIELD_TEAM,message);
        params.put(FIELD_COMPONENT, isComponentMatch);
        return params;
    }

}