package com.edwardawebb.jira.assignescalate.ao;

import java.util.Date;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.ManyToMany;
import net.java.ao.Mutator;
import net.java.ao.Preload;
import net.java.ao.schema.Default;
import net.java.ao.schema.Table;


/**
 * This is a person, they take FTO and get assigned across many project roles.
 *
 * 
 *
 */
@Table("USER")
@Preload
public interface SupportMember extends Entity {

    
    @Accessor("NAME")
    public String getPrincipleName();
    @Mutator("NAME")
    public void setPrincipleName(String name);
    
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
    

    @ManyToMany(value = ProjectRoleAssignmentMapping.class,through="getProjectRole",reverse="getUser")
    ProjectRole[] getProjectRoles();
}
