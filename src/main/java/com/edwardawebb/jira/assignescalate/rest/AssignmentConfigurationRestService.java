/**
 * 
 */
package com.edwardawebb.jira.assignescalate.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edwardawebb.jira.assignescalate.ao.service.AssignmentService;

/**
 * BEcause the Project Tab Panel is static, we use asynchrnous scripts to interact with this REST service.
 *
 * @author n0158588
 * 
 */
@Path("/configurations")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssignmentConfigurationRestService {
    private static final Logger LOG = LoggerFactory.getLogger(AssignmentConfigurationRestService.class);

    private final AssignmentService assignmentConfigurationService;

    public AssignmentConfigurationRestService(AssignmentService assignmentConfigurationService) {
        super();
        this.assignmentConfigurationService = assignmentConfigurationService;
    }



}
