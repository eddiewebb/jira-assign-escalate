package com.edwardawebb.jira.assignescalate.ao.resources;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.edwardawebb.jira.assignescalate.ao.SupportTeam;

@XmlRootElement(name = "supportTeam")
@XmlAccessorType(XmlAccessType.FIELD)
public class SupportTeamResource  {

    private Integer id;
    private String name;
    private String role;
    private long projectId;

    public static SupportTeamResource from(SupportTeam team){
        SupportTeamResource teamResource = new SupportTeamResource();
        teamResource.id = team.getID();
        teamResource.name = team.getName();
        teamResource.role = team.getRole();
        teamResource.projectId = team.getProjectId();
        return teamResource;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public long getProjectId() {
        return projectId;
    }
    
    
    
}
