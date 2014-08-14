/**
 * 
 */
package com.edwardawebb.jira.assignescalate.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edwardawebb.jira.assignescalate.AssignmentService;
import com.edwardawebb.jira.assignescalate.ao.SupportTeam;

/**
 * BEcause the Project Tab Panel is static, we use asynchrnous scripts to interact with this REST service.
 *
 */
@Path("/teams")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TeamConfigurationService {
    private static final Logger LOG = LoggerFactory.getLogger(TeamConfigurationService.class);

    private final AssignmentService assignmentService;

    public TeamConfigurationService(AssignmentService assignmentConfigurationService) {
        this.assignmentService = assignmentConfigurationService;
    }


    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("/")
    public Response createNewTeam(@FormParam("prj_id") Long projectId,@FormParam("name") String name, @FormParam("role") String role){
        SupportTeam team = assignmentService.createProjectTeam(projectId, name, role);
        return Response.ok(team).build();
    }

}
