package com.edwardawebb.jira.assignescalate.ao.service;

import net.java.ao.Query;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.edwardawebb.jira.assignescalate.ao.ProjectRole;

public class DefaultAssignmentService implements AssignmentService {

    private ActiveObjects ao;

    public DefaultAssignmentService(ActiveObjects activeObjects) {
        this.ao=activeObjects;
    }

    @Override
    public ProjectRole[] getProjectRoles(Long projectId) {
        return ao.find(ProjectRole.class,Query.select().where("projectId = ?",projectId));
    }

    @Override
    public ProjectRole getProjectRole(Long projectId, String role) {
        ProjectRole[] results = ao.find(ProjectRole.class,Query.select().where("projectId = ? and projectRole = '?'",projectId,role));
        if ( null != results && results.length == 1 ) {
            return results[0];
        }else{
            return null;
        }
    }

}
