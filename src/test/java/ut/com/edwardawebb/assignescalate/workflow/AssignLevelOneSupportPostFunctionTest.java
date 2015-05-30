package ut.com.edwardawebb.assignescalate.workflow;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.java.ao.EntityManager;
import net.java.ao.RawEntity;

import org.junit.Before;
import org.junit.Test;
import org.ofbiz.core.entity.GenericValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueImpl;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.label.LabelManager;
import com.atlassian.jira.issue.security.IssueSecurityLevelManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.edwardawebb.jira.assignescalate.AssignmentService;
import com.edwardawebb.jira.assignescalate.ao.SupportMember;
import com.edwardawebb.jira.assignescalate.ao.SupportTeam;
import com.edwardawebb.jira.assignescalate.ao.TeamToUser;
import com.edwardawebb.jira.assignescalate.workflow.AssignLevelOneSupportPostFunction;
import com.edwardawebb.jira.assignescalate.workflow.AssignLevelOneSupportPostFunctionFactory;

/**
 * See @AssignmentServiceTest for validation of round-robin, etc. 
 * That logic is outside the workflow functions themselves.
 */
public class AssignLevelOneSupportPostFunctionTest
{
    private static final Logger log = LoggerFactory.getLogger(AssignLevelOneSupportPostFunctionTest.class);
     public static final String MESSAGE = "my message";
    public static final SupportMember ASSIGNEE = new AssignLevelOneSupportPostFunctionTest.MockSupportMember("eddie", "Eddie Webb", 0);
    public static final SupportMember COMPONENT_ASSIGNEE = new AssignLevelOneSupportPostFunctionTest.MockSupportMember("joey", "Joey Other Guy", 0);

    protected ProjectComponent componentToMatch = mock(ProjectComponent.class);
    protected AssignLevelOneSupportPostFunction function;
    protected MutableIssue issue;
    protected AssignmentService assignmentService;
    private static ArrayList<ProjectComponent> components;

    @Before
    public void setup() {

        components = new ArrayList<ProjectComponent>();
         components.add(componentToMatch);

        issue = createPartialMockedIssue();
        issue.setDescription("");

        assignmentService = mock(AssignmentService.class);
        when(assignmentService.assignNextAvailableAssigneeForProjectTeam(0L, "Level One")).thenReturn(ASSIGNEE);
        when(assignmentService.assignNextAvailableAssigneeForProjectTeam(0L, "Component Team")).thenReturn(COMPONENT_ASSIGNEE);
        when(assignmentService.findAllTeamsWith(0L, components.get(0))).thenReturn(getComponentTeams(1));
        function = new AssignLevelOneSupportPostFunction(assignmentService) {
            @Override
            protected MutableIssue getIssue(Map transientVars) throws DataAccessException {
                log.warn("returning fake issufor testing!");
                issue.getComponentObjects();
                return issue;
            }
        };
    }

    private SupportTeam[] getComponentTeams(int i) {
        List<SupportTeam> teams = new ArrayList<SupportTeam>();
        while (teams.size() < i) {
            teams.add(new AssignLevelOneSupportPostFunctionTest.MockSupportTeam(2,"Component Team","Developers"));
        }
        return teams.toArray(new SupportTeam[teams.size()]);
    }

    @Test
    public void testAsigneeIsAppliedViaWorkflowBasedOnTeamName() throws Exception
    {
        Map args = new HashMap();
        args.put("teamName","Level One");
        function.execute(null,args,null);
       assertThat(issue.getAssigneeId(),is(ASSIGNEE.getPrincipleName()));
    }
    
    @Test
    public void testAsigneeIsAppliedViaWorkflowBasedOnComponent() throws Exception
    {

        ArrayList<ProjectComponent> components = new ArrayList<ProjectComponent>();
        components.add(componentToMatch);
        when(issue.getComponentObjects()).thenReturn(components);
        Map args = new HashMap();
        args.put("teamName","Level One");// Yes, wrong name!  we want compoent match to overide.
        args.put(AssignLevelOneSupportPostFunctionFactory.FIELD_COMPONENT,"true");
        function.execute(null,args,null);

         assertThat(issue.getAssigneeId(),is(COMPONENT_ASSIGNEE.getPrincipleName()));
    }

    private MutableIssue createPartialMockedIssue() {
        GenericValue genericValue = mock(GenericValue.class);
        IssueManager issueManager = mock(IssueManager.class);
        ProjectManager projectManager = mock(ProjectManager.class);
        VersionManager versionManager = mock(VersionManager.class);
        IssueSecurityLevelManager issueSecurityLevelManager = mock(IssueSecurityLevelManager.class);
        ConstantsManager constantsManager = mock(ConstantsManager.class);
        SubTaskManager subTaskManager = mock(SubTaskManager.class);
        AttachmentManager attachmentManager = mock(AttachmentManager.class);
        LabelManager labelManager = mock(LabelManager.class);
        ProjectComponentManager projectComponentManager = mock(ProjectComponentManager.class);
        UserManager userManager = mock(UserManager.class);

        //The issue will use some of these depening on how it is interogated by the function
        when(userManager.getUserByKey("eddie")).thenReturn(new MockApplicationUser("eddie","eddie","Eddie Webb"));
        when(userManager.getUserByKeyEvenWhenUnknown("eddie")).thenReturn(new MockApplicationUser("eddie","eddie","Eddie Webb"));
        when(userManager.getUserByKey("joey")).thenReturn(new MockApplicationUser("joey","joey"));
        when(userManager.getUserByKeyEvenWhenUnknown("joey")).thenReturn(new MockApplicationUser("joey","joey","JOey"));
        
        

        ArrayList<ProjectComponent> components = new ArrayList<ProjectComponent>();
        components.add(componentToMatch);
    
        when(projectComponentManager.containsName(anyString(), any(Long.class))).thenReturn(true);
        when(projectComponentManager.findComponentsByIssue(any(Issue.class))).thenReturn(components);
        
        MutableIssue mockedIssue = spy( new IssueImpl(genericValue, issueManager, projectManager, versionManager, issueSecurityLevelManager, constantsManager, subTaskManager, attachmentManager, labelManager, projectComponentManager, userManager,null));
        when(mockedIssue.getComponentObjects()).thenReturn(components);
        
        return mockedIssue;
    }

    private static class MockSupportMember implements SupportMember{
        private String principleName;
        private String displayName;
        private int id;
        
        
        public MockSupportMember(String principleName, String displayName, int id) {
            this.principleName = principleName;
            this.displayName = displayName;
            this.id = id;
        }

        public MockSupportMember(int i, String displayName2, String string) {
            // TODO Auto-generated constructor stub
        }

        @Override
        public int getID() {
            return id;
        }

        @Override
        public void init() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void save() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public EntityManager getEntityManager() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <X extends RawEntity<Integer>> Class<X> getEntityType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public String getJiraKey() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void setJiraKey(String key) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public String getPrincipleName() {
           return principleName;
        }

        @Override
        public void setPrincipleName(String name) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public String getDisplayName() {
            return displayName;
        }

        @Override
        public void setDisplayName(String name) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public SupportTeam[] getProjectRoles() {
            // TODO Auto-generated method stub
            return null;
        }
        
    }

    private static class MockSupportTeam implements SupportTeam{
        private String name;
        private int id;
        private String role;
        
        public MockSupportTeam(int id, String name, String role) {
            this.id = id;
            this.name = name;
            this.role = role;
        }
        @Override
        public void init() {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void save() {
            // TODO Auto-generated method stub
            
        }
        @Override
        public EntityManager getEntityManager() {
            // TODO Auto-generated method stub
            return null;
        }
        @Override
        public <X extends RawEntity<Integer>> Class<X> getEntityType() {
            // TODO Auto-generated method stub
            return null;
        }
        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public int getID() {
            return id;
        }
        @Override
        public long getProjectId() {
            // TODO Auto-generated method stub
            return 0;
        }
        @Override
        public void setProjectId(long projectId) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public String getName() {
            return name;
        }
        @Override
        public void setName(String name) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public String getRole() {
            return role;
        }
        @Override
        public void setRole(String projectRole) {
            // TODO Auto-generated method stub
            
        }
        @Override
        public TeamToUser[] getAssignments() {
            // TODO Auto-generated method stub
            return null;
        }
        @Override
        public String getComponents() {
            // TODO Auto-generated method stub
            return null;
        }
        @Override
        public void setComponents(String name) {
            // TODO Auto-generated method stub
            
        }
    }
}
