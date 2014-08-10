package com.edwardawebb.jira.assignescalate.ao.service;

import java.util.Date;

import net.java.ao.ActiveObjectsException;
import net.java.ao.DBParam;
import net.java.ao.Query;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.edwardawebb.jira.assignescalate.ao.AssignmentHistory;
import com.edwardawebb.jira.assignescalate.ao.ProjectRole;
import com.edwardawebb.jira.assignescalate.ao.ProjectRoleAssignmentMapping;
import com.edwardawebb.jira.assignescalate.ao.SupportMember;

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
    public ProjectRole getProjectRole(Integer projectRoleId) {
        return ao.get(ProjectRole.class, projectRoleId);
    }

    @Override
    public ProjectRole createProjectRole(Long projectId, String name, String projectRole) {
        ProjectRole existingrole = findRoleByProjectIdAndName(projectId, name);
        if ( null == existingrole ){
            // good, does not exist
        
            final ProjectRole role = ao.create(ProjectRole.class, new DBParam("NAME",name),
                    new DBParam("ROLE",projectRole),new DBParam("PROJECTID", projectId));
            return role;
        }else{
            throw new ActiveObjectsException("Role names are unique to each project");
        }
    }

    @Override
    public SupportMember assignNextAvailableAssigneeForProjectRole(Long projectId, String name) {
        ProjectRole role = findRoleByProjectIdAndName(projectId,name);
        
        SupportMember[] members = ao.find(SupportMember.class,Query.select()
                .alias(ProjectRoleAssignmentMapping.class, "am")
                .alias(SupportMember.class, "sm")
                .join(ProjectRoleAssignmentMapping.class,"sm.ID = USERID")
                .where("ROLEID = ? and HIDE = 0 and ASSIGN = 1", role.getID()).order("LASTDATE"));        
        
        final SupportMember next = members.length > 0 ? members[0] : null;
        if(null != next){
            next.setLastAssigned(new Date());
            next.save();
        }
        
        return next;
    }

   

    private ProjectRole findRoleByProjectIdAndName(Long projectId, String name) {
        ProjectRole[] results = ao.find(ProjectRole.class,Query.select().where("PROJECTID = ? and name = ?",projectId,name));
        
        if (results.length > 1)
        {
            throw new IllegalStateException("Application cannot have more than 1 blog");
        }

        return results.length > 0 ? results[0] : null;
    }

  

}
