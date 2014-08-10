/**
 * 
 */
package com.edwardawebb.jira.assignescalate.ao.service;

import com.edwardawebb.jira.assignescalate.ao.ProjectRole;


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
     * @param projectId
     * @return
     */
    ProjectRole[] getProjectRoles(Long projectId);

    ProjectRole getProjectRole(Long projectId, String role);
   
   

}
