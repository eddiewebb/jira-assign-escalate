package com.edwardawebb.jira.assignescalate.ao;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Implementation;
import net.java.ao.Mutator;
import net.java.ao.Searchable;
import net.java.ao.schema.Table;

/**
 * Represents the actual status for an individual assigned to a project role
 *
 */
@Table("ASRLEUSR")
public interface ProjectRoleAssignmentMapping extends Entity {

    @Accessor("ROLE")
    ProjectRole getProjectRole();
    @Mutator("ROLE")
    void setProjectRole(ProjectRole projectRole);
    
    @Accessor("USER")
    SupportMember getUser();
    @Mutator("USER")
    void setUser(SupportMember supportMember);
}
