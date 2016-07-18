package ut.com.edwardawebb.jira.assignescalate.events;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.user.UserRenamedEvent;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.edwardawebb.jira.assignescalate.AssignmentService;
import com.edwardawebb.jira.assignescalate.ao.service.DefaultAssignmentService;
import com.edwardawebb.jira.assignescalate.events.UserRenameEventListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.edwardawebb.jira.assignescalate.events.UserRenameEventListenerImpl;

import static org.mockito.Mockito.*;

/**
 * @since 3.5
 */
public class UserRenameEventListenerImplTest {

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void testUserNameUpdateEvent() {
        String oldUserName = "henrylast";
        String newUserName = "hlast";
        //UserRenameEventListenerImpl testClass = new UserRenameEventListenerImpl();

        //mock the change event created by jira
        UserRenamedEvent event = mock(UserRenamedEvent.class);
        when(event.getOldUserName()).thenReturn(oldUserName);
        when(event.getUsername()).thenReturn(newUserName);
        EventPublisher mockPublished = mock(EventPublisher.class);

        //mock jira user returned bymock service
        ApplicationUser indecisiveUser = mock(ApplicationUser.class);
        when(indecisiveUser.getKey()).thenReturn(oldUserName);
        when(indecisiveUser.getUsername()).thenReturn(newUserName);
        UserManager userManager= mock(UserManager.class);
        when(userManager.getUserByName(any(String.class))).thenReturn(indecisiveUser);


        //verify against our mocked service
        DefaultAssignmentService assignmentService = mock(DefaultAssignmentService.class);

        UserRenameEventListener listener = new UserRenameEventListenerImpl(assignmentService,mockPublished,userManager);
        listener.userRenamed(event);

        verify(userManager).getUserByName(newUserName);
        verify(assignmentService).updateUserNameIfExistingUser(eq(oldUserName), eq(newUserName), any(String.class));

    }

}
