/**
 * 
 */
package com.edwardawebb.jira.assignescalate.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.roles.ProjectRoleManager;
import com.edwardawebb.jira.assignescalate.AssignmentService;
import com.edwardawebb.jira.assignescalate.ao.SupportTeam;
import com.edwardawebb.jira.assignescalate.ao.resources.SupportTeamResource;
import com.edwardawebb.jira.assignescalate.jobs.ProjectTeamAssignerCallback;

/**
 * BEcause the Project Tab Panel is static, we use asynchrnous scripts to interact with this REST service.
 *
 */
@Path("/team")
public class TeamConfigurationService {
    private static final Logger LOG = LoggerFactory.getLogger(TeamConfigurationService.class);

    private final AssignmentService assignmentService;

    private ProjectRoleManager roleManager;

    private ProjectManager projectManager;

    public TeamConfigurationService(AssignmentService assignmentConfigurationService,ProjectRoleManager roleManager, ProjectManager projectManager) {
        this.assignmentService = assignmentConfigurationService;
        this.roleManager = roleManager;
        this.projectManager = projectManager;
    }


    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response createNewTeam(@FormParam("prj_id") Long projectId,@FormParam("name") String name, @FormParam("role") String role){
        SupportTeam team = assignmentService.createProjectTeam(projectId, name, role);
        LOG.info("Created team " + team.getID());
        return Response.ok(SupportTeamResource.from(team)).build();
    }
    
    @POST
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/reindex")
    //TODO the responsibilities seem right, but naming and context is off using callback here.
    public Response indexTeam(@PathParam("id") Integer teamId){
        SupportTeam team = assignmentService.getProjectTeam(teamId);
        ProjectTeamAssignerCallback updater = new ProjectTeamAssignerCallback(assignmentService, roleManager, projectManager);
        updater.valueRead(team);
        return Response.ok().build();
    }
    

}
