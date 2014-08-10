/**
 * 
 */
package com.edwardawebb.jira.assignescalate.admin;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.plugin.projectpanel.impl.AbstractProjectTabPanel;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.browse.BrowseContext;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleActors;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.edwardawebb.jira.assignescalate.ao.service.AssignmentService;
/**
 * This class just provides the front end initial UI. All heavy lifting is done
 * by our custom servlet, invokes via AJS.
 * 
 * @author n0158588
 * 
 */
public class SupportDutyProjectTabPanel extends AbstractProjectTabPanel {
    private static final Logger LOG = LoggerFactory.getLogger(SupportDutyProjectTabPanel.class);

    public final static String ADMIN_ROLE = "Administrators";
    public final static String SUPPORT_ROLE = "Developers";
    public final static String ASSIGNABLE_USERS = "selectedPeople";
    public final static String LEVEL_ONE_PEOPLE = "people";
    public final static String LEVEL_TWO_PEOPLE = "leveltwo";

    private final UserManager userManager;
    private final JiraAuthenticationContext authenticationContext;
    private final ProjectRoleManager projectRoleManager;
    private final AssignmentService assignmentConfigurationService;

    public SupportDutyProjectTabPanel(UserManager userManager, ProjectRoleManager projectRoleManager,
            JiraAuthenticationContext authenticationContext,
            AssignmentService assignmentConfigurationService) {
        this.authenticationContext = authenticationContext;
        this.userManager = userManager;
        this.projectRoleManager = projectRoleManager;
        this.assignmentConfigurationService = assignmentConfigurationService;
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
    private Collection<ApplicationUser> usersInSupportRole(BrowseContext context) {

        Project project = context.getProject();
        ProjectRole projectRole = projectRoleManager.getProjectRole(SUPPORT_ROLE);
        ProjectRoleActors projectRoleActors = projectRoleManager.getProjectRoleActors(projectRole, project);
        
        Set<ApplicationUser> supportMembers = projectRoleActors.getApplicationUsers();
        LOG.debug("Available Users: {}",supportMembers );
        return supportMembers;
    }



}
