/**
 * 
 */
package com.edwardawebb.jira.assignescalate.admin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.plugin.projectpanel.impl.AbstractProjectTabPanel;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.browse.BrowseContext;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.edwardawebb.jira.assignescalate.AssignmentService;
import com.edwardawebb.jira.assignescalate.ao.SupportTeam;
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

    private UserManager userManager;

    public SupportTeamProjectPanelTab(UserManager userManager, ProjectRoleManager projectRoleManager,
            JiraAuthenticationContext authenticationContext,
            AssignmentService assignmentService) {
        this.authenticationContext = authenticationContext;
        this.projectRoleManager = projectRoleManager;
        this.assignmentService = assignmentService;
        this.userManager = userManager;
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
        Project project = ctx.getProject();
        Map<String, Object> params = super.createVelocityParams(ctx);
        params.put("projectTeams", teamsForProject(ctx));
        params.put("project", project);
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
    private List<SupportTeam> teamsForProject(BrowseContext context) {

        Project project = context.getProject();
        SupportTeam[] teams = assignmentService.getProjectTeams(project.getId());
        
        
        return Arrays.asList(teams);     
        
    }



}
