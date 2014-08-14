package it.com.edwardawebb.jira.assignescalate.rest;

import java.util.HashSet;
import java.util.Set;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.RestClient;
import org.apache.wink.client.handlers.BasicAuthSecurityHandler;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.junit.Before;

public class AbstractEndpointTest {
    private static final String USERNAME="admin";
    private final static String PASSWORD="admin";
    
    protected ClientConfig clientConfig;
    protected  RestClient client;
    String baseUrl = System.getProperty("baseurl");
    String resourceUrlTeam = baseUrl + "/rest/support-team/1.0/";
    
    @Before
    public void setup(){

        javax.ws.rs.core.Application app = new javax.ws.rs.core.Application() {
            public Set<Class<?>> getClasses() {
                Set<Class<?>> classes = new HashSet<Class<?>>();
                classes.add(JacksonJaxbJsonProvider.class);
                return classes;
            }

        };
        //create auth handler
        clientConfig = new ClientConfig();
        clientConfig.applications(app);
        BasicAuthSecurityHandler basicAuthSecurityHandler = new BasicAuthSecurityHandler();
        basicAuthSecurityHandler.setUserName(USERNAME);
        basicAuthSecurityHandler.setPassword(PASSWORD); 
        clientConfig.handlers(basicAuthSecurityHandler);
        //create client usin auth   
        client = new RestClient(clientConfig);
      
    }
    

}
