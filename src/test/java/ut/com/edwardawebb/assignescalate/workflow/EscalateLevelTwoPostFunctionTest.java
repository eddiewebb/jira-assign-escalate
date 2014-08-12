package ut.com.edwardawebb.assignescalate.workflow;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.ofbiz.core.entity.GenericValue;

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
import com.edwardawebb.jira.assignescalate.workflow.EscalateLevelTwoPostFunction;

public class EscalateLevelTwoPostFunctionTest
{
    public static final String MESSAGE = "my message";

    protected EscalateLevelTwoPostFunction function;
    protected MutableIssue issue;

    @Before
    public void setup() {

        issue = createPartialMockedIssue();
        issue.setDescription("");

        function = new EscalateLevelTwoPostFunction() {
            protected MutableIssue getIssue(Map transientVars) throws DataAccessException {
                return issue;
            }
        };
    }

    @Test
    public void testNullMessage() throws Exception
    {
        Map transientVars = Collections.emptyMap();

        function.execute(transientVars,null,null);

        assertEquals("message should be empty","",issue.getDescription());
    }

    @Test
    public void testEmptyMessage() throws Exception
    {
        Map transientVars = new HashMap();
        transientVars.put("messageField","");
        function.execute(transientVars,null,null);

        assertEquals("message should be empty","",issue.getDescription());
    }

    @Test
    public void testValidMessage() throws Exception
    {
        Map transientVars = new HashMap();
        transientVars.put("messageField",MESSAGE);
        function.execute(transientVars,null,null);

        assertEquals("message not found",MESSAGE,issue.getDescription());
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
