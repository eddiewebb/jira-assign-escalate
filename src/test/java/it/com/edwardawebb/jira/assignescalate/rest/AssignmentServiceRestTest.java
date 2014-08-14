package it.com.edwardawebb.jira.assignescalate.rest;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.junit.Test;

import com.edwardawebb.jira.assignescalate.ao.SupportTeam;

public class AssignmentServiceRestTest extends AbstractEndpointTest {
    
    /**
     * - Use Cases
     *  create new team
     *  -> trigger team index
     *  update team assignments
     *  delete team
     *  
     *  ON HOLD
     *  load teams (currenly do full inital load, no need)
     */
    
    
    @Test
    public void testANewSupportTeamCanBeCreated(){
        StringBuilder formData = new StringBuilder();
        formData.append("prj_id=10001");
        formData.append("&name=Level One Team - Awesome");
        formData.append("&role=Developers");
        ClientResponse response = client.resource(resourceUrlTeam).contentType(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_JSON).post(formData.toString());
        assertEquals("Could not create token needed for test",200,response.getStatusCode());
        SupportTeam team = response.getEntity(SupportTeam.class);
        
        assertThat(team.getID(),not(is(0)));
        
    }

}
