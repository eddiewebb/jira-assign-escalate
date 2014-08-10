package com.edwardawebb.jira.assignescalate.ao;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

/**
 * Represents the actual status for an individual assigned to a project role
 *
 */
@Table("ASRLEUSR")
public interface ProjectRoleAssignmentMapping extends Entity {

    ProjectRole getProjectRole();
    void setProjectRole(ProjectRole projectRole);
    
    SupportMember getSupportMember();
    void setSupportMember(SupportMember projectRole);
   
}
