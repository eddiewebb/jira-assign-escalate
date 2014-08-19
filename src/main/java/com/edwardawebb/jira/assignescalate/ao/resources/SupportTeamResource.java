package com.edwardawebb.jira.assignescalate.ao.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.edwardawebb.jira.assignescalate.ao.SupportTeam;
import com.edwardawebb.jira.assignescalate.ao.TeamToUser;

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

    
    
    public static SupportTeamResource from(SupportTeam team, ProjectComponentManager componentManager) {
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
               log.debug("Looking up component: " + componentKey);
               String justId = componentKey.substring(5, componentKey.length());
               log.debug("ID: " + justId);
               Long id = Long.parseLong(justId);
               log.debug("as long:" + id);
               componentIds.add(id);
           }
           try{
               List<ProjectComponent> componentList = componentManager.getComponents(componentIds);
               log.debug("Found {} matching components from JIRA manager",componentList.size());
               Collection<ComponentResource> componentResources = ComponentResource.listFrom(componentList);
               log.debug("which have been converted to COmponentResources: {}", componentResources);
               teamResource.components.addAll(componentResources);
           }catch(Exception e){
               log.error("Error converting JIRA Components to simple ComponnentResource",e);
           }
        }
        return teamResource;
    }

    public List<ComponentResource> getComponents() {
        return components;
    }
    
    
    
}
