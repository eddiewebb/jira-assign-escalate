package com.edwardawebb.jira.assignescalate.ao;

import java.util.Date;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;
import net.java.ao.Preload;
import net.java.ao.schema.Default;
import net.java.ao.schema.Table;

/**
 * Represents the actual status for an individual assigned to a project role
 *
 */
@Table("ASRLEUSR")
@Preload
public interface TeamToUser extends Entity {

    @Accessor("TEAM")
    SupportTeam getProjectRole();
    @Mutator("TEAM")
    void setProjectRole(SupportTeam projectRole);
    
    @Accessor("USER")
    SupportMember getUser();
    @Mutator("USER")
    void setUser(SupportMember supportMember);
    
    
    
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
