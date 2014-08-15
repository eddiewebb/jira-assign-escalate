package ut.com.edwardawebb.assignescalate.workflow;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.ofbiz.core.entity.GenericValue;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.issue.AttachmentManager;
import com.atlassian.jira.issue.IssueImpl;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.label.LabelManager;
import com.atlassian.jira.issue.security.IssueSecurityLevelManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.user.util.UserManager;
import com.edwardawebb.jira.assignescalate.AssignmentService;
import com.edwardawebb.jira.assignescalate.ao.SupportMember;
import com.edwardawebb.jira.assignescalate.workflow.AssignLevelOneSupportPostFunction;

public class AssignLevelOneSupportPostFunctionTest
{
    public static final String MESSAGE = "my message";
    public static final SupportMember ASSIGNEE = mock(SupportMember.class);

    protected AssignLevelOneSupportPostFunction function;
    protected MutableIssue issue;
    protected AssignmentService assignmentService;

    @Before
    public void setup() {

        issue = createPartialMockedIssue();
        issue.setDescription("");
        assignmentService = mock(AssignmentService.class);
        when(assignmentService.assignNextAvailableAssigneeForProjectTeam(anyLong(), anyString())).thenReturn(ASSIGNEE);

        function = new AssignLevelOneSupportPostFunction(assignmentService) {
            protected MutableIssue getIssue(Map transientVars) throws DataAccessException {
                return issue;
            }
        };
    }

    @Test
    public void testNewAssignee() throws Exception
    {
        Map args = new HashMap();
        args.put("teamName","Level One");
        function.execute(null,args,null);

       assertThat(issue.getAssigneeId(),is(ASSIGNEE.getPrincipleName()));
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

        return new IssueImpl(genericValue, issueManager, projectManager, versionManager, issueSecurityLevelManager, constantsManager, subTaskManager, attachmentManager, labelManager, projectComponentManager, userManager,null);
    }

}
