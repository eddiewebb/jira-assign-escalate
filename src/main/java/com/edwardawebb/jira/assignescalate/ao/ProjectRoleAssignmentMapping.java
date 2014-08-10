package com.edwardawebb.jira.assignescalate.ao;

import java.util.Date;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Implementation;
import net.java.ao.Mutator;
import net.java.ao.Searchable;
import net.java.ao.schema.Default;
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
    
    
    
    /*
     * how to make these first clss values
     * 
     */
    @Default("FALSE")
    @Accessor("ASSIGN")
    public boolean isAssignable();
    @Mutator("ASSIGN")
    public void setAssignable(boolean isAssignable);   
    

    @Accessor("LASTDATE")
    public Date getLastAssigned();
    @Mutator("LASTDATE")
    public void setLastAssigned(Date assigned);
    
    // users that are no longer in role or system
    @Default("FALSE")
    @Accessor("HIDE")
    public boolean isHidden();
    @Mutator("HIDE")
    public void setHidden(boolean isHidden);
}
