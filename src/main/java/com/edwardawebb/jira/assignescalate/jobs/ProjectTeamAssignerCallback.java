package com.edwardawebb.jira.assignescalate.jobs;

import java.util.Set;

import org.apache.log4j.Logger;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.security.roles.ProjectRoleActors;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.atlassian.jira.user.ApplicationUser;
import com.edwardawebb.jira.assignescalate.AssignmentService;
import com.edwardawebb.jira.assignescalate.ao.SupportTeam;

public class ProjectTeamAssignerCallback {

    // YES, this is not the same class, and allows our log statements to fall under the same as the job.
    private final Logger logger = Logger.getLogger(SyncProjectTeamUsersScheduler.class);
    
    private AssignmentService assignmentService;
    private ProjectRoleManager roleManager;
    private ProjectManager projectManager;
    

    public ProjectTeamAssignerCallback(AssignmentService assignmentService, ProjectRoleManager roleManager, ProjectManager projectManager) {
        this.assignmentService = assignmentService;
        this.roleManager = roleManager;
        this.projectManager = projectManager;
    }

    public void valueRead(final SupportTeam readOnlyProjectRole) {
       long start = System.currentTimeMillis();
       String roleToScan = readOnlyProjectRole.getRole();   
       logger.info("==>Scanning Team: " + readOnlyProjectRole.getName() + ", for projectID:" + readOnlyProjectRole.getProjectId());
       Project project = projectManager.getProjectObj(readOnlyProjectRole.getProjectId());
       if( null != project ){
           ProjectRole projectRole = roleManager.getProjectRole(roleToScan);
           assert projectRole!= null;
           ProjectRoleActors lastestActors = roleManager.getProjectRoleActors(projectRole, project);
           Set<ApplicationUser> users = lastestActors.getApplicationUsers();
           logger.info("==>" + users.size() + " users in role " + roleToScan + " to consider");
           
           assignmentService.updateUsersLinkedToTeam(users, readOnlyProjectRole);
       }else{
           logger.error("Could not retrieve project for Support Team: " + readOnlyProjectRole.getID() +":"+readOnlyProjectRole.getName());
       }
       long stop = System.currentTimeMillis();
       long duration=stop-start;
       logger.info("==>Completed Team: " + readOnlyProjectRole.getName() + ", for projectID:" + readOnlyProjectRole.getProjectId() + " in " + duration + "ms");
       
    }
    

}
