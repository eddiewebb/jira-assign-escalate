package com.edwardawebb.jira.assignescalate.events;

import com.atlassian.jira.event.user.UserRenamedEvent;

public interface UserRenameEventListener {

        void userRenamed(UserRenamedEvent event);
}