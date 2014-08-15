package com.edwardawebb.jira.assignescalate.ao.resources;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.edwardawebb.jira.assignescalate.ao.SupportMember;
import com.edwardawebb.jira.assignescalate.ao.TeamToUser;


@XmlRootElement(name = "supportTeam")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserResource {
    
    @XmlElement(required = true, nillable = false)
    private String principleName;
    @XmlElement
    private String displayName;
    @XmlElement
    private Boolean assignable;
    
    
    private UserResource(String principleName2, String displayName2, boolean assignable2) {
       this.principleName=principleName2;
       this.displayName=displayName2;
       this.assignable=assignable2;
    }




    public static UserResource from(TeamToUser assignment){
        SupportMember user = assignment.getUser();
        return new UserResource(user.getPrincipleName(), user.getDisplayName(), assignment.isAssignable());
    }
    
    
    
    
    public String getPrincipleName() {
        return principleName;
    }
    public void setPrincipleName(String principleName) {
        this.principleName = principleName;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public Boolean getAssignable() {
        return assignable;
    }
    public void setAssignable(Boolean assignable) {
        this.assignable = assignable;
    }
    
    
    

}
