package ut.com.edwardawebb.jira.assignescalate.ao;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.java.ao.EntityManager;
import net.java.ao.test.converters.NameConverters;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.edwardawebb.jira.assignescalate.AssignmentService;
import com.edwardawebb.jira.assignescalate.ao.SupportMember;
import com.edwardawebb.jira.assignescalate.ao.SupportTeam;
import com.edwardawebb.jira.assignescalate.ao.TeamToUser;
import com.edwardawebb.jira.assignescalate.ao.service.DefaultAssignmentService;
import com.google.common.collect.Sets;

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
    private static final ApplicationUser user1 = new MockApplicationUser("Fallon","Fallon Dude","eddie@mail.com");
    private static final ApplicationUser user2 = new MockApplicationUser("Sam","Sam Guy","eddie@mail.com");
    private static final ApplicationUser user3 = new MockApplicationUser("Tammy","Tammy Girl","eddie@mail.com");
    private static final ApplicationUser user4 = new MockApplicationUser("Ivan","Ivan Man","eddie@mail.com");
    private static final ApplicationUser user5 = new MockApplicationUser("Ali","Ali Lady","eddie@mail.com");
    private static final ApplicationUser user6 = new MockApplicationUser("Felix","Felix Foreigner","eddie@mail.com");
    private static final ApplicationUser user7 = new MockApplicationUser("Selina","Selina Standy","eddie@mail.com");
    
    private static Set<ApplicationUser> allDevelopers;

   private SupportTeam adHocRole;
    
    private AssignmentService assignmentService;


    @Before
    public void before() {
        allDevelopers=new HashSet<ApplicationUser>();
        allDevelopers.add(user1);
        allDevelopers.add(user2);
        allDevelopers.add(user3);
        allDevelopers.add(user4);
        allDevelopers.add(user5);
        allDevelopers.add(user6);
        allDevelopers.add(user7);
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
     * - ON HOLD - Retrieve next escalatee for Role
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
        SupportTeam[] projectRoles = assignmentService.getProjectTeams(PROJECT_ONE_KEY);
        assertThat(projectRoles.length,is(2));
    }
    
    
    
    @Test
    public void testASpecificProjectRoleRulesCanBeRetrieved(){
        SupportTeam[] projectRoles = assignmentService.getProjectTeams(PROJECT_ONE_KEY);
        assertThat(projectRoles.length,is(2));
        
        for (int i = 0; i < projectRoles.length; i++) {
            SupportTeam baseline = projectRoles[i];
            SupportTeam projectRole = assignmentService.getProjectTeam(baseline.getID());
            assertThat(projectRole,notNullValue());
        }        
    }

    @Test
    public void testAProjectConfigCanBeCreated(){
       SupportTeam role = assignmentService.createProjectTeam(PROJECT_ONE_KEY,ROLE_THREE,"Admins");
       assertThat(role.getName(),is(ROLE_THREE));
       assertThat(role.getRole(),is("Admins"));
       assertThat(role.getProjectId(),is(PROJECT_ONE_KEY));       

       SupportTeam queriedRole = assignmentService.getProjectTeam(role.getID());
       assertThat(queriedRole.getName(),is(ROLE_THREE));
       assertThat(queriedRole.getRole(),is("Admins"));
       assertThat(queriedRole.getProjectId(),is(PROJECT_ONE_KEY));
       
       adHocRole = queriedRole;
    }
    @Test(expected=net.java.ao.ActiveObjectsException.class)
    public void testARoleWithSameNameAndProjectCanNotBeCreated(){
        SupportTeam role = assignmentService.createProjectTeam(PROJECT_ONE_KEY,ROLE_THREE,"Admins");
        role = assignmentService.createProjectTeam(PROJECT_ONE_KEY,ROLE_THREE,"Admins");
        assertThat(role.getID(),nullValue());
    }

    @Test
    public void testTheSupportPoolCanBeRetreivedForAProjectRole(){
        SupportTeam[] projectRoles = assignmentService.getProjectTeams(PROJECT_ONE_KEY);
        assertThat(projectRoles.length,is(2));
        SupportTeam projectRole=null;
        for (int i = 0; i < projectRoles.length; i++) {
            SupportTeam baseline = projectRoles[i];
             projectRole = assignmentService.getProjectTeam(baseline.getID());
            assertThat(projectRole,notNullValue());
            break;
        }       
        
        assertThat(projectRole.getAssignments().length,is(3));
        adHocRole=projectRole;
    }
    
    @Test
    public void testTheNextAssigneeCanBeRetrieved(){
        SupportMember nextGuy = assignmentService.assignNextAvailableAssigneeForProjectTeam(PROJECT_ONE_KEY,ROLE_ONE);
        assertThat(nextGuy,notNullValue());
        assertThat(nextGuy.getPrincipleName(),notNullValue());
        //second assignee is missing an assigned date, should be oldest
         assertThat(nextGuy.getPrincipleName(),is(SECOND_ASSIGNEE));
         nextGuy = assignmentService.assignNextAvailableAssigneeForProjectTeam(PROJECT_ONE_KEY,ROLE_ONE);
         assertThat(nextGuy.getPrincipleName(),is(FIRST_ASSIGNEE));
         nextGuy = assignmentService.assignNextAvailableAssigneeForProjectTeam(PROJECT_ONE_KEY,ROLE_ONE);
          assertThat(nextGuy.getPrincipleName(),is(SECOND_ASSIGNEE));
    }
    
    
    /** 
     * i.e Bryan goes on FTO, Mike is heads-down on a secret project
     */
    @Test
    public void testThatUserRosterAvailabilityCanBeUpdated(){
        SupportTeam role = assignmentService.getProjectTeam(1);
        assertThat(role.getAssignments().length,is(3));
        
        TeamToUser[] team = role.getAssignments();        
        int assignableCount=0;
        for (int i = 0; i < team.length; i++) {
            TeamToUser person = team[i];
            if(person.isAssignable()){
                assignableCount++;
                person.setAssignable(false);
            }else{
                person.setAssignable(true);
            }
        }
        assertThat(assignableCount,is(2));
        //save
        assignmentService.updateProjectTeam(role);
        
        //now check it worked
         role = assignmentService.getProjectTeam(1);
        assertThat(role.getAssignments().length,is(3));
        TeamToUser[] newteam = role.getAssignments();
        assignableCount=0;
        for (int i = 0; i < newteam.length; i++) {
            TeamToUser person = newteam[i];
            if(person.isAssignable()){
                assignableCount++;
                person.setAssignable(false);
            }else{
                person.setAssignable(true);
            }
        }
        assertThat(assignableCount,is(1));
        
    }
    
    /**
     * Users get added to LDAP/AD overnight.
     */
    @Test
    public void testThatNewUsersOfGroupCanBeAdded(){
        SupportTeam role = assignmentService.getProjectTeam(1);
        assertThat(role.getAssignments().length,is(3));

        assignmentService.updateUsersLinkedToTeam(allDevelopers,role);
        role = assignmentService.getProjectTeam(1);
        assertThat(role.getAssignments().length,is(7));
    }
    
    
    /**
     * Users get removed from LDAP/AD overnight.
     */
    @Test
    public void testThatFormerUsersOfGroupCanBeHidden(){
        SupportTeam role = assignmentService.getProjectTeam(1);
        assertThat(role.getAssignments().length,is(3));

        assignmentService.updateUsersLinkedToTeam(allDevelopers,role);
        role = assignmentService.getProjectTeam(1);
        assertThat(role.getAssignments().length,is(7));

        Set<ApplicationUser> someDevelopers = Sets.newHashSet(allDevelopers);
        someDevelopers.remove(user1);
        someDevelopers.remove(user2);
        someDevelopers.remove(user3);
        
        assignmentService.updateUsersLinkedToTeam(someDevelopers,role);
        role = assignmentService.getProjectTeam(1);
        assertThat(role.getAssignments().length,is(4));
    }
    
    @Test
    public void testThatFormerUsersOfRoleCanReapear(){
        //baseline full team of 7
        SupportTeam role = assignmentService.getProjectTeam(1);
        assertThat(role.getAssignments().length,is(3));

        assignmentService.updateUsersLinkedToTeam(allDevelopers,role);
        role = assignmentService.getProjectTeam(1);
        assertThat(role.getAssignments().length,is(7));

        //remove some folks
        Set<ApplicationUser> someDevelopers = Sets.newHashSet(allDevelopers);
        someDevelopers.remove(user1);
        someDevelopers.remove(user2);
        someDevelopers.remove(user3);
        
        assignmentService.updateUsersLinkedToTeam(someDevelopers,role);
        role = assignmentService.getProjectTeam(1);
        assertThat(role.getAssignments().length,is(4));
        
        //re-add everyone
        assignmentService.updateUsersLinkedToTeam(allDevelopers,role);
        role = assignmentService.getProjectTeam(1);
        assertThat(role.getAssignments().length,is(7));
        
    }
    
    @Test
    public void testThatNewUsersOfRoleCanBeReEnabled(){
        
    }
    
    
    

    
    public static class ConfigAssigmentTestData implements DatabaseUpdater
    {
        
        @Override
        public void update(EntityManager em) throws Exception
        {   
            em.migrate(SupportTeam.class);
            em.migrate(SupportMember.class);
            em.migrate(TeamToUser.class);
 
            
            /**
             * Team one 3/5 developers
             */
            final SupportTeam todo = em.create(SupportTeam.class);
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
            
            final TeamToUser roleToPerson = em.create(TeamToUser.class);
            roleToPerson.setAssignable(true);
            roleToPerson.setLastAssigned(new Date(10L));
            roleToPerson.setProjectRole(todo);
            roleToPerson.setUser(me);
            roleToPerson.save();
            final TeamToUser roleToPerson2 = em.create(TeamToUser.class);
            roleToPerson2.setAssignable(true);
            roleToPerson2.setLastAssigned(new Date(0L));
            roleToPerson2.setProjectRole(todo);
            roleToPerson2.setUser(moe);
            roleToPerson2.save();
            final TeamToUser roleToPerson3 = em.create(TeamToUser.class);
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
            
            
            final SupportTeam role2 = em.create(SupportTeam.class);
            role2.setProjectId(PROJECT_ONE_KEY);
            role2.setName(ROLE_TWO);
            role2.setRole("Developers");
            role2.save();
            
            final TeamToUser roleToPerson6 = em.create(TeamToUser.class);
            roleToPerson6.setProjectRole(role2);
            roleToPerson6.setUser(me);
            roleToPerson6.save();
            final TeamToUser roleToPerson4 = em.create(TeamToUser.class);
            roleToPerson4.setProjectRole(role2);
            roleToPerson4.setUser(selina);
            roleToPerson4.save();
            final TeamToUser roleToPerson5 = em.create(TeamToUser.class);
            roleToPerson5.setProjectRole(role2);
            roleToPerson5.setUser(felix);
            roleToPerson5.save();
            
        }
    }

}