package ut.com.edwardawebb.jira.assignescalate.ao;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;

import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.edwardawebb.jira.assignescalate.ao.ProjectRole;
import com.edwardawebb.jira.assignescalate.ao.ProjectRoleAssignmentMapping;
import com.edwardawebb.jira.assignescalate.ao.SupportMember;
import com.edwardawebb.jira.assignescalate.ao.service.AssignmentService;
import com.edwardawebb.jira.assignescalate.ao.service.DefaultAssignmentService;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(AssignmentServiceTest.ConfigAssigmentTestData.class)
public class AssignmentServiceTest {
    
    //gets injected thanks to ActiveObjectsJUnitRunner.class  
    private EntityManager entityManager;

    private ActiveObjects activeObjects ;
    
    private static final Long PROJECT_ONE_KEY = 10000L;
    private static final String ROLE_ONE = "Support";
    private static final String ROLE_TWO = "Experts";
    private static final String ROLE_THREE = "Used by Tests";
    private static final String FIRST_ASSIGNEE = "Fallon";
    private static final String SECOND_ASSIGNEE = "Sam";
    private static final String THIRD_ASSIGNEE = "Tammy";
    private static final String FIRST_ESCALATION = "Felix";
    private static final String SECOND_ESCALATION = "Selina";

   private ProjectRole adHocRole;
    
    private AssignmentService assignmentService;


    @Before
    public void before() {
        activeObjects = new TestActiveObjects(entityManager);
        assignmentService = new DefaultAssignmentService(activeObjects);             
    }

    /*
     * Use cases for service
     * - A list of Project Role can be Retreived
     * - Project Role can be Retreived
     * - Project Role can be created
     * - A duplicate named role can not
     * - Retrieve all available assignees for Role
     * - ON HOLD - Retrieve all available escalatees for Role
     * - Retrieve next assignee for Role
     * - Retrieve next escalatee for Role
     * - Update assignees
     * - Update escalatees
     * 
     * Missing Features
     * - Can define roles and set both assignee and escalatee pools
     * - System can pull all persons from that role into their project assignements
     * - Validated provided role exists
     * 
     * Bonus Featues
     * - Component Based Assigning
     */
    
    
    
    
    @Test
    public void testAListOfProjectRoleRulesCanBeRetrieved(){
        ProjectRole[] projectRoles = assignmentService.getProjectRoles(PROJECT_ONE_KEY);
        assertThat(projectRoles.length,is(2));
    }
    
    
    @Test
    public void testASpecificProjectRoleRulesCanBeRetrieved(){
        ProjectRole[] projectRoles = assignmentService.getProjectRoles(PROJECT_ONE_KEY);
        assertThat(projectRoles.length,is(2));
        
        for (int i = 0; i < projectRoles.length; i++) {
            ProjectRole baseline = projectRoles[i];
            ProjectRole projectRole = assignmentService.getProjectRole(baseline.getID());
            assertThat(projectRole,notNullValue());
        }        
    }

    @Test
    public void testAProjectConfigCanBeCreated(){
       ProjectRole role = assignmentService.createProjectRole(PROJECT_ONE_KEY,ROLE_THREE,"Admins");
       assertThat(role.getName(),is(ROLE_THREE));
       assertThat(role.getRole(),is("Admins"));
       assertThat(role.getProjectId(),is(PROJECT_ONE_KEY));       

       ProjectRole queriedRole = assignmentService.getProjectRole(role.getID());
       assertThat(queriedRole.getName(),is(ROLE_THREE));
       assertThat(queriedRole.getRole(),is("Admins"));
       assertThat(queriedRole.getProjectId(),is(PROJECT_ONE_KEY));
       
       adHocRole = queriedRole;
    }
    @Test(expected=net.java.ao.ActiveObjectsException.class)
    public void testARoleWithSameNameAndProjectCanNotBeCreated(){
        ProjectRole role = assignmentService.createProjectRole(PROJECT_ONE_KEY,ROLE_THREE,"Admins");
        role = assignmentService.createProjectRole(PROJECT_ONE_KEY,ROLE_THREE,"Admins");
        assertThat(role.getID(),nullValue());
    }

    @Test
    public void testTheSupportPoolCanBeRetreivedForAProjectRole(){
        ProjectRole[] projectRoles = assignmentService.getProjectRoles(PROJECT_ONE_KEY);
        assertThat(projectRoles.length,is(2));
        ProjectRole projectRole=null;
        for (int i = 0; i < projectRoles.length; i++) {
            ProjectRole baseline = projectRoles[i];
             projectRole = assignmentService.getProjectRole(baseline.getID());
            assertThat(projectRole,notNullValue());
            break;
        }       
        
        assertThat(projectRole.getAssignees().length,is(3));
        adHocRole=projectRole;
    }
    
    @Test
    public void testTheNextAssigneeCanBeRetrieved(){
        SupportMember nextGuy = assignmentService.assignNextAvailableAssigneeForProjectRole(PROJECT_ONE_KEY,ROLE_ONE);
        assertThat(nextGuy,notNullValue());
        assertThat(nextGuy.getPrincipleName(),notNullValue());
        //second assignee is missing an assigned date, should be oldest
         assertThat(nextGuy.getPrincipleName(),is(SECOND_ASSIGNEE));
         nextGuy = assignmentService.assignNextAvailableAssigneeForProjectRole(PROJECT_ONE_KEY,ROLE_ONE);
         assertThat(nextGuy.getPrincipleName(),is(FIRST_ASSIGNEE));
         nextGuy = assignmentService.assignNextAvailableAssigneeForProjectRole(PROJECT_ONE_KEY,ROLE_ONE);
          assertThat(nextGuy.getPrincipleName(),is(SECOND_ASSIGNEE));
    }
    
    
    

    
    public static class ConfigAssigmentTestData implements DatabaseUpdater
    {
        @Override
        public void update(EntityManager em) throws Exception
        {   
            em.migrate(ProjectRole.class);
            em.migrate(SupportMember.class);
            em.migrate(ProjectRoleAssignmentMapping.class);
 
            
            /**
             * Team one 3/5 developers
             */
            final ProjectRole todo = em.create(ProjectRole.class);
            todo.setProjectId(PROJECT_ONE_KEY);
            todo.setName(ROLE_ONE);
            todo.setRole("Developers");
            todo.save();

            final SupportMember me = em.create(SupportMember.class);
            me.setPrincipleName(FIRST_ASSIGNEE);
            me.setAssignable(true);
            me.setLastAssigned(new Date());
            me.save();
            final SupportMember moe = em.create(SupportMember.class);
            moe.setPrincipleName(SECOND_ASSIGNEE);
            moe.setAssignable(true);
            moe.save();
            final SupportMember max = em.create(SupportMember.class);
            max.setPrincipleName(THIRD_ASSIGNEE);
            max.save();
            
            final ProjectRoleAssignmentMapping roleToPerson = em.create(ProjectRoleAssignmentMapping.class);
            roleToPerson.setProjectRole(todo);
            roleToPerson.setUser(me);
            roleToPerson.save();
            final ProjectRoleAssignmentMapping roleToPerson2 = em.create(ProjectRoleAssignmentMapping.class);
            roleToPerson2.setProjectRole(todo);
            roleToPerson2.setUser(moe);
            roleToPerson2.save();
            final ProjectRoleAssignmentMapping roleToPerson3 = em.create(ProjectRoleAssignmentMapping.class);
            roleToPerson3.setProjectRole(todo);
            roleToPerson3.setUser(max);
            roleToPerson3.save();
            
            /**
             * Team 2, 3/5 developers
             * 
             */


            final SupportMember felix = em.create(SupportMember.class);
            felix.setPrincipleName(FIRST_ESCALATION);
            felix.save();
            final SupportMember selina = em.create(SupportMember.class);
            selina.setPrincipleName(SECOND_ESCALATION);
            selina.save();
            
            
            final ProjectRole role2 = em.create(ProjectRole.class);
            role2.setProjectId(PROJECT_ONE_KEY);
            role2.setName(ROLE_TWO);
            role2.setRole("Developers");
            role2.save();
            
            final ProjectRoleAssignmentMapping roleToPerson6 = em.create(ProjectRoleAssignmentMapping.class);
            roleToPerson6.setProjectRole(role2);
            roleToPerson6.setUser(me);
            roleToPerson6.save();
            final ProjectRoleAssignmentMapping roleToPerson4 = em.create(ProjectRoleAssignmentMapping.class);
            roleToPerson4.setProjectRole(role2);
            roleToPerson4.setUser(selina);
            roleToPerson4.save();
            final ProjectRoleAssignmentMapping roleToPerson5 = em.create(ProjectRoleAssignmentMapping.class);
            roleToPerson5.setProjectRole(role2);
            roleToPerson5.setUser(felix);
            roleToPerson5.save();
            
        }
    }

}