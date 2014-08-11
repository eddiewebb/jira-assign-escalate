package com.edwardawebb.jira.assignescalate.jobs;

import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.edwardawebb.jira.assignescalate.ao.service.AssignmentService;
import com.edwardawebb.jira.assignescalate.ao.service.ProjectRoleStreamCallback;

public class SyncProjectRoleUsersMonitorImpl implements LifecycleAware {
    
    private static final String JOB_NAME = SyncProjectRoleUsersMonitorImpl.class.getName() + ":job";
    static final String KEY = SyncProjectRoleUsersMonitorImpl.class.getName() + ":instance";
    private final Logger logger = Logger.getLogger(SyncProjectRoleUsersMonitorImpl.class);
    private final PluginScheduler pluginScheduler;  // provided by SAL
    private final AssignmentService assignmentService;
    private final ProjectRoleManager roleManager;
    private final ProjectManager projectManager;

    
    
    private long interval = 1000L * 60 * 60 * 24;      // default job interval (1 day)
    private Date lastRun;

    
    

    
    public SyncProjectRoleUsersMonitorImpl(PluginScheduler pluginScheduler, AssignmentService assignmentService,
            ProjectRoleManager roleManager, ProjectManager projectManager) {
        this.pluginScheduler = pluginScheduler;
        this.assignmentService = assignmentService;
        this.roleManager = roleManager;
        this.projectManager = projectManager;
    }

    @Override
    public void onStart() {
        reschedule( interval);
    }
 
    public void reschedule(long interval) {
        this.interval = interval;
         
        pluginScheduler.scheduleJob(
                JOB_NAME,                   // unique name of the job
                SyncProjectRoleUsersJob.class,     // class of the job
                new HashMap<String,Object>() {{
                    put(KEY, SyncProjectRoleUsersMonitorImpl.this);
                }},                         // data that needs to be passed to the job
                new Date(),                 // the time the job is to start
                interval);                  // interval between repeats, in milliseconds
        logger.info(String.format("JIRA Assign & Escalate ProjectRole<>User mapping  task scheduled to run every %dms", interval));
    }

    public void setLastRun(Date date) {
        this.lastRun = date;
        
    }

    public void scanAndUpdateProjectRoles() {
        logger.info("Assign & Escalate Team Sync STARTING - All Teams & Projects");
        long start = System.currentTimeMillis();
        ProjectRoleStreamCallback callback = new ProjectRoleStreamCallback(assignmentService, roleManager, projectManager);
        assignmentService.loadAllProjectTeams(callback);
        long stop = System.currentTimeMillis();
        long duration = (stop - start);
        logger.info("Assign & Escalate TEAM Sync COMPLETED in " + duration + "ms");
    }
}
