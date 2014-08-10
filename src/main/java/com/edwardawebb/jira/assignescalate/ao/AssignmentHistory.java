package com.edwardawebb.jira.assignescalate.ao;

import java.util.Date;

import net.java.ao.Accessor;
import net.java.ao.Mutator;
import net.java.ao.schema.Default;

public interface AssignmentHistory {

    @Default("FALSE")
    @Accessor("AS_CAN")
    public boolean isAssignable();
    @Mutator("AS_CAN")
    public void setAssignable(boolean isAssignable);   
    
    
    // users that are no longer in role or system
    @Default("FALSE")
    @Accessor("HIDE")
    public boolean isHidden();
    @Mutator("HIDE")
    public void setHidden(boolean isHidden);
    
    @Accessor("AS_COUNT")
    @Default("0")
    public long getAssignmentCount();
    @Mutator("AS_COUNT")
    public void setAssignmentCount(long newCount);
     
    /*
     * anytime an issue they are assigned to is re-assigned through this plugins' workflows.
     */
    @Accessor("DINGS")
    @Default("0")
    public long getEscalatedOnCount();
    @Mutator("DINGS")
    public void setEscalatedOnCount(long newCount);

    @Accessor("AS_DATE")
    public Date getLastAssigned();
    @Mutator("AS_DATE")
    public void setLastAssigned(Date assigned);

}
