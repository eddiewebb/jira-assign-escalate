package com.edwardawebb.jira.assignescalate.events;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.user.UserRenamedEvent;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.util.UserManager;
import com.edwardawebb.jira.assignescalate.AssignmentService;
import com.edwardawebb.jira.assignescalate.events.UserRenameEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserRenameEventListenerImpl implements UserRenameEventListener {
    private static final Logger log = LoggerFactory.getLogger(UserRenameEventListenerImpl.class);
    private final EventPublisher eventPublisher;
    private final AssignmentService assignmentService;
    private final UserManager userManager;

    public UserRenameEventListenerImpl( AssignmentService assignmentService,EventPublisher eventPublisher, UserManager userManager) {
        this.assignmentService = assignmentService;
        this.userManager = userManager;
        this.eventPublisher = eventPublisher;
        eventPublisher.register(this);
    }

    @EventListener
    public void userRenamed(UserRenamedEvent event) {
        ApplicationUser user = userManager.getUserByName(event.getUsername());
        assignmentService.updateUserNameIfExistingUser(user.getKey(),user.getUsername(),user.getDisplayName());

        log.error("User changed! " + user.getKey() +":"+user.getUsername());
    }
}