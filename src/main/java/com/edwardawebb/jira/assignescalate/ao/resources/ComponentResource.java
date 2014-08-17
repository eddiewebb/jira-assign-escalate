package com.edwardawebb.jira.assignescalate.ao.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.atlassian.jira.bc.project.component.ProjectComponent;

public class ComponentResource {

    private long id;
    private String name;
    public ComponentResource(ProjectComponent component) {
        this.id=component.getId();
        this.name=component.getName();
    }
    public long getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public static Collection<? extends ComponentResource> listFrom(List<ProjectComponent> components) {
        List<ComponentResource> resources = new ArrayList<ComponentResource>();
        for (ProjectComponent projectComponent : components) {
            resources.add(new ComponentResource(projectComponent));
        }
        return resources;
    }
    
}
