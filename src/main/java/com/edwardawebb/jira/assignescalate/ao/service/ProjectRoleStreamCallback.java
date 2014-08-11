package com.edwardawebb.jira.assignescalate.ao.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleActors;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.edwardawebb.jira.assignescalate.ao.SupportTeam;
import com.edwardawebb.jira.assignescalate.jobs.SyncProjectRoleUsersMonitorImpl;

public class ProjectRoleStreamCallback {

    // YES, this is not the same class, and allows our log statements to fall under the same as the job.
    private final Logger logger = Logger.getLogger(SyncProjectRoleUsersMonitorImpl.class);
    
    private AssignmentService assignmentService;
    private ProjectRoleManager roleManager;
    private ProjectManager projectManager;
    

    public ProjectRoleStreamCallback(AssignmentService assignmentService, ProjectRoleManager roleManager, ProjectManager projectManager) {
        this.assignmentService = assignmentService;
        this.roleManager = roleManager;
        this.projectManager = projectManager;
    }

    public void valueRead(final SupportTeam readOnlyProjectRole) {
       long start = System.currentTimeMillis();
       String roleToScan = readOnlyProjectRole.getRole();   
       logger.info("==>Scanning Team: " + readOnlyProjectRole.getName() + ", for projectID:" + readOnlyProjectRole.getProjectId());
       List<String> allUserNamesInGroup = new ArrayList<String>();
       Project project = projectManager.getProjectObj(readOnlyProjectRole.getProjectId());
       ProjectRole projectRole = roleManager.getProjectRole(roleToScan);
       assert projectRole!= null;
       ProjectRoleActors lastestActors = roleManager.getProjectRoleActors(projectRole, project);
       Set<User> users = lastestActors.getUsers();
       logger.info("==>" + users.size() + " users in role " + roleToScan + " to consider");
       for (User user : lastestActors.getUsers()) {
           allUserNamesInGroup.add(user.getName());
       }   
       assignmentService.updateUsersLinkedToRole(allUserNamesInGroup.toArray(new String[allUserNamesInGroup.size()]), readOnlyProjectRole);
       long stop = System.currentTimeMillis();
       long duration=stop-start;
       logger.info("==>Completed Team: " + readOnlyProjectRole.getName() + ", for projectID:" + readOnlyProjectRole.getProjectId() + " in " + duration + "ms");
       
    }
    

}
