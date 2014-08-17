package com.edwardawebb.jira.assignescalate.ao.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import com.edwardawebb.jira.assignescalate.ao.SupportTeam;
import com.edwardawebb.jira.assignescalate.ao.TeamToUser;

@XmlRootElement(name = "supportTeam")
@XmlAccessorType(XmlAccessType.FIELD)
public class SupportTeamResource  {


    private Integer id;
    private String name;
    private String role;
    private long projectId;

    @XmlElement(type=UserResource.class)
    private List<UserResource> users;
    
    private List<String> componentIds;

    public static SupportTeamResource from(SupportTeam team){
        SupportTeamResource teamResource = new SupportTeamResource();
        teamResource.id = team.getID();
        teamResource.name = team.getName();
        teamResource.role = team.getRole();
        teamResource.projectId = team.getProjectId();
        teamResource.users = new ArrayList<UserResource>();
        for (TeamToUser assignment : team.getAssignments()) {
            teamResource.users.add(UserResource.from(assignment));
        }
        if(null != team.getComponents()){
            teamResource.componentIds=Arrays.asList(StringUtils.split(team.getComponents()));
        }else{
            teamResource.componentIds=new ArrayList<String>();
        }
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

    public List<UserResource> getUsers() {
        return users;
    }

    public void setUsers(List<UserResource> users) {
        this.users = users;
    }

    public List<String> getComponentIds() {
        return componentIds;
    }
    
    
    
}
