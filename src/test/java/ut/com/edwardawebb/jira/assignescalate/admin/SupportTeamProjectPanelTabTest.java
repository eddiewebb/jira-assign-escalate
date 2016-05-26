package ut.com.edwardawebb.jira.assignescalate.admin;


import com.atlassian.crowd.model.authentication.AuthenticationContext;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.mock.MockPermissionManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.security.MockAuthenticationContext;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.browse.BrowseContext;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.MockUserManager;
import com.atlassian.query.Query;
import com.atlassian.sal.api.user.UserManager;
import com.edwardawebb.jira.assignescalate.AssignmentService;
import com.edwardawebb.jira.assignescalate.admin.SupportTeamProjectPanelTab;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SupportTeamProjectPanelTabTest {

    PermissionManager mockPermissionManager = new MockPermissionManager();
    @Mock
    private UserManager mockUserManager;

    private Project project = mock(Project.class);
    private BrowseContext context = mock(BrowseContext.class);
    private ApplicationUser user = mock(ApplicationUser.class);

    //constructor wont accept {@ApplicationUser} so we use #setLoggedInUser in #setup()
    JiraAuthenticationContext mockAuthenticationContext = new MockAuthenticationContext(null);


    @Before
    public void setup(){
        new MockComponentWorker().init();
        mockAuthenticationContext.setLoggedInUser(user);
        when(context.getProject()).thenReturn(project);

    }

    @Test
    @Ignore
    public void testThatAllAdminsAreShownPanel(){
        SupportTeamProjectPanelTab tab = new SupportTeamProjectPanelTab( mockPermissionManager,
                 mockAuthenticationContext,
                 null, null, mockUserManager,null);

        boolean isAllowed = tab.showPanel(context);
        assertTrue("Admin could not see panel", isAllowed);
    }


    @Test
    @Ignore
    public void testThatNonAdminsAreNotShownPanel(){
        SupportTeamProjectPanelTab tab = new SupportTeamProjectPanelTab( mockPermissionManager,
                mockAuthenticationContext,
                null, null, mockUserManager,null);

        boolean isAllowed = tab.showPanel(context);
        assertFalse("Admin could not see panel", isAllowed);
    }
}
