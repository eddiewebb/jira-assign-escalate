/**
 * 
 */
package com.edwardawebb.jira.assignescalate.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.ComponentManager;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.plugin.projectpanel.impl.AbstractProjectTabPanel;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.browse.BrowseContext;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.edwardawebb.jira.assignescalate.AssignmentService;
import com.edwardawebb.jira.assignescalate.ao.SupportTeam;
import com.edwardawebb.jira.assignescalate.ao.resources.SupportTeamResource;
/**
 * This class just provides the front end initial UI. All heavy lifting is done
 * by our custom servlet, invokes via AJS.
 * 
 */
public class SupportTeamProjectPanelTab extends AbstractProjectTabPanel {
    private static final Logger LOG = LoggerFactory.getLogger(SupportTeamProjectPanelTab.class);

    public final static String ADMIN_ROLE = "Administrators";

    private final JiraAuthenticationContext authenticationContext;
    private final ProjectRoleManager projectRoleManager;
    private final AssignmentService assignmentService;
    private final ProjectComponentManager projectComponentManager;
    


    public SupportTeamProjectPanelTab(ProjectRoleManager projectRoleManager,
            JiraAuthenticationContext authenticationContext,
            AssignmentService assignmentService,ProjectComponentManager projectComponentManager) {
        this.authenticationContext = authenticationContext;
        this.projectRoleManager = projectRoleManager;
        this.assignmentService = assignmentService;
        this.projectComponentManager = projectComponentManager;
    }

    @Override
    /**
     * only show to admins
     */
    public boolean showPanel(BrowseContext browseContext) {
        ApplicationUser user = authenticationContext.getUser();
        Project project = browseContext.getProject();
        ProjectRole projectRole = projectRoleManager.getProjectRole(ADMIN_ROLE);
        return projectRoleManager.isUserInProjectRole(user, projectRole, project);
    }

    @Override
    protected Map<String, Object> createVelocityParams(BrowseContext ctx) {
        Map<String, Object> params = super.createVelocityParams(ctx);
        params.put("projectTeams", teamsForProject(ctx));
        Project project = ctx.getProject();
        params.put("project", project);
        Collection<ProjectRole> roles = projectRoleManager.getProjectRoles();
        params.put("roles", roles);
        Collection<ProjectComponent> components = projectComponentManager.findAllForProject(project.getId());
        params.put("components", components);
        return params;
    }

    @Override
    public String getHtml(BrowseContext ctx) {
        
        return descriptor.getHtml("view", createVelocityParams(ctx));
    }

    /**
     * returns list of ApplicationUser in the configured role and project.
     * 
     * @return
     */
    private List<SupportTeamResource> teamsForProject(BrowseContext context) {

        Project project = context.getProject();
        SupportTeam[] teams = assignmentService.getProjectTeams(project.getId());

        List<SupportTeamResource> teamResources = new ArrayList<SupportTeamResource>();
        for (SupportTeam supportTeam : teams) {
            teamResources.add(SupportTeamResource.from(supportTeam));
        }
        
        return teamResources;     
        
    }



}
