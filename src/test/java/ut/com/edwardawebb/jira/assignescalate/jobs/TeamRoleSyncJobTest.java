package ut.com.edwardawebb.jira.assignescalate.jobs;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Set;

import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleActors;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.edwardawebb.jira.assignescalate.ao.SupportMember;
import com.edwardawebb.jira.assignescalate.ao.SupportTeam;
import com.edwardawebb.jira.assignescalate.ao.TeamToUser;
import com.edwardawebb.jira.assignescalate.ao.service.AssignmentService;
import com.edwardawebb.jira.assignescalate.ao.service.DefaultAssignmentService;
import com.edwardawebb.jira.assignescalate.jobs.SyncProjectTeamUsersScheduler;
import com.google.common.collect.Sets;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TeamRoleSyncJobTest.TestData.class)
public class TeamRoleSyncJobTest {
    
    //gets injected thanks to ActiveObjectsJUnitRunner.class  
    private EntityManager entityManager;
    private ActiveObjects activeObjects ;
    private AssignmentService assignmentService ;
    
    private static final Long PROJECT_ID=10002L;
    private static final String ROLE_NAME="Developers";
    
    private PluginScheduler pluginScheduler = mock(PluginScheduler.class);
    private ProjectRoleManager roleManager = mock(ProjectRoleManager.class);
    private ProjectManager projectManager = mock(ProjectManager.class);
    private Project project = mock(Project.class);
    private ProjectRole projectRole = mock(ProjectRole.class);
    private ProjectRoleActors actors = mock(ProjectRoleActors.class);
    private static final ApplicationUser user1 = new MockApplicationUser("Fallon","Fallon Dude","eddie@mail.com");
    private static final ApplicationUser user2 = new MockApplicationUser("Sam","Sam Guy","eddie@mail.com");
    private static final ApplicationUser user3 = new MockApplicationUser("Tammy","Tammy Girl","eddie@mail.com");
    private Set<ApplicationUser> users = Sets.newHashSet(user1,user2,user3);
    private String[] teamNames = {"Fallon","Sam","Tammy"};
    
    

    SyncProjectTeamUsersScheduler monitor;

    @Before
    public void before() {
        activeObjects = new TestActiveObjects(entityManager);
        assignmentService = new DefaultAssignmentService(activeObjects); 
        
        //mock out enough of user/role services so job can get list of users from "ldap"
        when(projectManager.getProjectObj(anyLong())).thenReturn(project);
        when(roleManager.getProjectRole(anyString())).thenReturn(projectRole);
        when(roleManager.getProjectRoleActors(org.mockito.Matchers.any(ProjectRole.class),org.mockito.Matchers.any(Project.class))).thenReturn(actors);
        when(actors.getApplicationUsers()).thenReturn(users );
        
        
        monitor = new SyncProjectTeamUsersScheduler(pluginScheduler, assignmentService, roleManager, projectManager);
    }
    
    @Test
    public void testTheMonitorCanCallTheAssignmentServiceWithNewUsersOnAnEmptyButExistentTeam(){
        monitor.scanAndUpdateProjectRoles();
        
        SupportTeam[] roles = assignmentService.getProjectTeams(PROJECT_ID);
        
        
        // only a single Team should exist with 3 assignments.
        assertThat(roles.length,is(1));
        assertThat(roles[0].getAssignments().length,is(3));
        
    } 
    
    
    public static class TestData implements DatabaseUpdater
    {
        @Override
        public void update(EntityManager em) throws Exception
        {   
            em.migrate(SupportTeam.class);
            em.migrate(SupportMember.class);
            em.migrate(TeamToUser.class);
 
            
            /**
             * Team one 3/5 developers
             */
            final SupportTeam todo = em.create(SupportTeam.class);
            todo.setProjectId(PROJECT_ID);
            todo.setName(ROLE_NAME);
            todo.setRole(ROLE_NAME);
            todo.save();

            
            
        }
    }
    
}