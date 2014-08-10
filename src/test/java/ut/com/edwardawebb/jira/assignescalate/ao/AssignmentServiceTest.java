package ut.com.edwardawebb.jira.assignescalate.ao;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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
    private static final String FIRST_ASSIGNEE = "Fallon";
    private static final String SECOND_ASSIGNEE = "Sam";
    private static final String THIRD_ASSIGNEE = "Tammy";
    private static final String FIRST_ESCALATION = "Felix";
    private static final String SECOND_ESCALATION = "Selina";

    
   
    
    private AssignmentService assignmentService;


    @Before
    public void before() {
        activeObjects = new TestActiveObjects(entityManager);
        assignmentService = new DefaultAssignmentService(activeObjects);             
    }

    /*
     * Use cases for service
     * - Project Role can be Retreived
     * - Project Role can be created
     * - Retrieve all available assignees for Role
     * - Retrieve all available escalatees for Role
     * - Retrieve next assignee for Role
     * - Retrieve next escalatee for Role
     * - Update assignees
     * - Update escalatees
     * 
     * Missing Features
     * - Can define roles and set both assignee and escalatee pools
     * - System can pull all persons from that role into their project assignements
     * 
     * Bonus Featues
     * - Component Based Assigning
     */
    
    
    
    
    @Test
    public void testAListOfProjectRoleRulesCanBeRetrieved(){
        ProjectRole[] projectRoles = assignmentService.getProjectRoles(PROJECT_ONE_KEY);
        assertThat(projectRoles.length,is(2));
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
            me.save();
            final SupportMember moe = em.create(SupportMember.class);
            moe.setPrincipleName(SECOND_ASSIGNEE);
            moe.save();
            final SupportMember max = em.create(SupportMember.class);
            max.setPrincipleName(THIRD_ASSIGNEE);
            max.save();
            
            final ProjectRoleAssignmentMapping roleToPerson = em.create(ProjectRoleAssignmentMapping.class);
            roleToPerson.setProjectRole(todo);
            roleToPerson.setSupportMember(me);
            roleToPerson.save();
            final ProjectRoleAssignmentMapping roleToPerson2 = em.create(ProjectRoleAssignmentMapping.class);
            roleToPerson2.setProjectRole(todo);
            roleToPerson2.setSupportMember(moe);
            roleToPerson2.save();
            final ProjectRoleAssignmentMapping roleToPerson3 = em.create(ProjectRoleAssignmentMapping.class);
            roleToPerson3.setProjectRole(todo);
            roleToPerson3.setSupportMember(max);
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
            roleToPerson6.setSupportMember(me);
            roleToPerson6.save();
            final ProjectRoleAssignmentMapping roleToPerson4 = em.create(ProjectRoleAssignmentMapping.class);
            roleToPerson4.setProjectRole(role2);
            roleToPerson4.setSupportMember(selina);
            roleToPerson4.save();
            final ProjectRoleAssignmentMapping roleToPerson5 = em.create(ProjectRoleAssignmentMapping.class);
            roleToPerson5.setProjectRole(role2);
            roleToPerson5.setSupportMember(felix);
            roleToPerson5.save();
            
        }
    }

}