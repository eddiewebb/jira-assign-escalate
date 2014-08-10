package com.edwardawebb.jira.assignescalate.ao;

import java.util.Date;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.Mutator;

public interface AssignmentHistory extends Entity {

    ProjectRole getProjectRole();
    void setProjectRole(ProjectRole role);
    
    SupportMember getSupportMember();
    void setSupportMember();
    
    @Accessor("AS_DATE")
    public Date getAssigned();
    @Mutator("AS_DATE")
    public void setAssigned(Date assigned);
    
   
}
