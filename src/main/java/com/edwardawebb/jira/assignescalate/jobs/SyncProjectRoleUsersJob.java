package com.edwardawebb.jira.assignescalate.jobs;

import java.util.Date;
import java.util.Map;

import com.atlassian.sal.api.scheduling.PluginJob;

/**
 * THis class will query back-end user manager for current users in the configured role for each known 
 * @ProjectRole. The users are then added to @ProjectRoleAssignmentMapping as needed.
 *
 */
public class SyncProjectRoleUsersJob implements PluginJob {

    @Override
    public void execute(Map<String, Object> jobDataMap) {
        final SyncProjectRoleUsersMonitorImpl monitor = (SyncProjectRoleUsersMonitorImpl)jobDataMap.get(SyncProjectRoleUsersMonitorImpl.KEY);
        assert monitor != null;
        
        monitor.scanAndUpdateProjectRoles();
        monitor.setLastRun(new Date());

    }

}
