/**
 * 
 */
package com.edwardawebb.jira.assignescalate;

import java.util.List;
import java.util.Set;

import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.user.ApplicationUser;
import com.edwardawebb.jira.assignescalate.ao.SupportMember;
import com.edwardawebb.jira.assignescalate.ao.SupportTeam;
import com.edwardawebb.jira.assignescalate.jobs.ProjectTeamAssignerCallback;


/**
 * Provides means to update and retrieve config for this plugin.
 *  The project ID is the only unique handle we use, but the objects have a generated ID as well
 * 
 * We don't use the Transactional annotation because it makes unit tests impossible, 
 * implementing classes are responsible to manage transaction boundaries.
 * 
 */
public interface AssignmentService {

    /**
     * Retrieve all configured roles for the project with id.
     * @param jiraProjectId - the ID provided by JIRA
     * @return
     */
    SupportTeam[] getProjectTeams(Long jiraProjectId);

    /**
     * Retreive specific role by AO ID
     * @param teamId - the unique id assigned to this object
     * @return
     */
    SupportTeam getProjectTeam(Integer teamId);

    /**
     * 
     * @param projectId - JIRA project ID
     * @param name - the name of this assignment
     * @param projectRole - the Project role to pull users from
     * @param components 
     * @return
     */
    SupportTeam createProjectTeam(Long projectOneKey, String name, String projectRole, List<String> components);

    SupportMember assignNextAvailableAssigneeForProjectTeam(Long projectId, String name);

    SupportTeam updateProjectTeam(SupportTeam team);

    void updateUsersLinkedToTeam(Set<ApplicationUser> users, SupportTeam team);

    /**
     * Stream all available project configs.
     * @return
     */
    void loadAllProjectTeams(ProjectTeamAssignerCallback callback);

    SupportTeam updateProjectTeam(Integer teamId, List<String> activeUsers);

    Integer deleteProjectTeam(Integer teamId);

    SupportTeam[] findAllTeamsWith(Long projectId, ProjectComponent component);


    SupportMember updateUserNameIfExistingUser(String key, String username, String displayName);

}
