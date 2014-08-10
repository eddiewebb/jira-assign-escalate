/**
 * 
 */
package com.edwardawebb.jira.assignescalate.ao.service;

import com.edwardawebb.jira.assignescalate.ao.ProjectRole;
import com.edwardawebb.jira.assignescalate.ao.SupportMember;


/**
 * Provides means to update and retrieve config for this plugin.
 *  The project ID is the only unique handle we use, but the objects have a generated ID as well
 * @author n0158588
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
    ProjectRole[] getProjectRoles(Long jiraProjectId);

    /**
     * Retreive specific role by AO ID
     * @param projectRoleId - the unique id assigned to this object
     * @return
     */
    ProjectRole getProjectRole(Integer projectRoleId);

    /**
     * 
     * @param projectId
     * @param name - the name of this assignment
     * @param projectRole - the Project role to pull users from
     * @return
     */
    ProjectRole createProjectRole(Long projectOneKey, String name, String projectRole);

    SupportMember assignNextAvailableAssigneeForProjectRole(Long projectId, String name);

    ProjectRole updateProjectRole(ProjectRole role);

   
   

}
