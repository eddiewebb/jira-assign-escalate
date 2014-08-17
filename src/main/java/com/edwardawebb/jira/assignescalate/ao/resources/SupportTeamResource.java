package com.edwardawebb.jira.assignescalate.ao.resources;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.EntityNotFoundException;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.edwardawebb.jira.assignescalate.ao.SupportTeam;
import com.edwardawebb.jira.assignescalate.ao.TeamToUser;
import com.edwardawebb.jira.assignescalate.workflow.AssignLevelOneSupportPostFunction;

@XmlRootElement(name = "supportTeam")
@XmlAccessorType(XmlAccessType.FIELD)
public class SupportTeamResource  {

    private static final Logger log = LoggerFactory.getLogger(SupportTeamResource.class);
    
    

    private Integer id;
    private String name;
    private String role;
    private long projectId;

    @XmlElement(type=UserResource.class)
    private List<UserResource> users;
    

    @XmlElement(type=ComponentResource.class)
    private List<ComponentResource> components;

  

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

    
    
    public static SupportTeamResource from(SupportTeam team, ProjectComponentManager componentManager) throws EntityNotFoundException{
        SupportTeamResource teamResource = new SupportTeamResource();
        teamResource.id = team.getID();
        teamResource.name = team.getName();
        teamResource.role = team.getRole();
        teamResource.projectId = team.getProjectId();
        teamResource.users = new ArrayList<UserResource>();
        for (TeamToUser assignment : team.getAssignments()) {
            teamResource.users.add(UserResource.from(assignment));
        }
        teamResource.components=new ArrayList<ComponentResource>();
        if(null != team.getComponents()){
           List<Long> componentIds = new ArrayList<Long>();
           for (String componentKey : StringUtils.split(team.getComponents(),",")) {
               log.warn("Looking up component: " + componentKey);
               String justId = componentKey.substring(5, componentKey.length());
               log.warn("ID: " + justId);
               componentIds.add(Long.parseLong(justId));
           }
           
           teamResource.components.addAll(ComponentResource.listFrom(componentManager.getComponents(componentIds) ));
        }
        return teamResource;
    }

    public List<ComponentResource> getComponents() {
        return components;
    }
    
    
    
}
