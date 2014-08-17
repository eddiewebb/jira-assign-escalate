package it.com.edwardawebb.jira.assignescalate.rest;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.junit.Test;

import com.edwardawebb.jira.assignescalate.ao.resources.SupportTeamResource;

public class AssignmentServiceRestTest extends AbstractEndpointTest {
    
    /**
     * - Use Cases
     *  create new team
     *  -> trigger team index
     *  update team assignments
     *  load team users
     *  delete team
     *  
     *  ON HOLD
     *  load teams (currenly do full inital load, no need)
     */
    private Integer teamId;
    
    @Test
    public void testANewSupportTeamCanBeCreated(){
        StringBuilder formData = new StringBuilder();
        formData.append("prj_id=10001");
        formData.append("&name=Level One Team - Awesome");
        formData.append("&role=Developers");
        ClientResponse response = client.resource(resourceUrlTeam + "/team").contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_JSON).post(formData.toString());
        assertEquals("Could not create team",200,response.getStatusCode());
        SupportTeamResource team = response.getEntity(SupportTeamResource.class);
        
        assertThat(team.getId(),not(is(0)));
        teamId=team.getId();
        
    } 
    
    
    @Test
    public void testANewSupportTeamCanBeReindexed(){
        
        
        
        String endpoint = resourceUrlTeam + "/team/" + 1 + "/reindex";
        ClientResponse response = client.resource(endpoint).post("1");
        assertEquals("Could not renidex team on " + endpoint,200,response.getStatusCode());
    }

    @Test
    public void testTeamUsersCanBeRetrieved(){
        ClientResponse response = client.resource(resourceUrlTeam + "/team/1").get();
        assertEquals("Could not retrieve team",200,response.getStatusCode());
        SupportTeamResource team = response.getEntity(SupportTeamResource.class); 
        assertThat(team.getUsers(),notNullValue());
        
    }
}
