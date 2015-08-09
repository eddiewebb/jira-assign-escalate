package ut.com.edwardawebb.jira.assignescalate.ao;


import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.jdbc.Jdbc;
import net.java.ao.test.jdbc.NonTransactional;
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



/**
 * Must run all methods that interact with service as @NonTransactional 
 * or otherwise the multiple layers of transactions cause issues.
 * Also http://grepcode.com/file/repo1.maven.org/maven2/net.java.dev.activeobjects/activeobjects-test/0.23.0/net/java/ao/test/jdbc/DynamicJdbcConfiguration.java#DynamicJdbcConfiguration.0jdbcSupplier
 * has all the databtase types and cobnection info needed in maven arguments.
 * @author Eddie Webb
 *
 */
@RunWith(ActiveObjectsJUnitRunner.class)
@Data(AssignmentServiceAOTezt.ConfigAssigmentTestData.class)
@Jdbc(net.java.ao.test.jdbc.DynamicJdbcConfiguration.class)
public class AssignmentServiceAOTezt {
    
    //gets injected thanks to ActiveObjectsJUnitRunner.class  
    private EntityManager entityManager;

    private ActiveObjects activeObjects ;

    private static final Long PROJECT_ONE_KEY = 10000L;
    private static final Long PROJECT_ZETA_KEY = 90000L;
    private static final String ROLE_ONE = "Support";
    private static final String ROLE_TWO = "Experts";
    private static final String ROLE_THREE = "Used by Tests";
    private static final String ROLE_ZETA = "Totally Noobs";
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
    private static List<String> componentIds = new ArrayList<String>();
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
        componentIds.clear();
        componentIds.add("10000");
        componentIds.add("10001");
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
    @NonTransactional
    public void testAListOfProjectRoleRulesCanBeRetrieved(){
        SupportTeam[] projectRoles = assignmentService.getProjectTeams(PROJECT_ONE_KEY);
        assertThat(projectRoles.length,is(2));
    }
    
    
    
    @Test
    @NonTransactional
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
    @NonTransactional
    //@Ignore("If you want this test to pass, comment out the ao.executeInTransaction of service, not compatible with unit testing but needed for prod use.")
    public void testAProjectConfigCanBeCreated(){
       SupportTeam role = assignmentService.createProjectTeam(PROJECT_ONE_KEY,ROLE_THREE,"Admins",componentIds);
       assertThat(role.getName(),is(ROLE_THREE));
       assertThat(role.getRole(),is("Admins"));
       assertThat(role.getProjectId(),is(PROJECT_ONE_KEY));       

       SupportTeam queriedRole = assignmentService.getProjectTeam(role.getID());
       assertThat(queriedRole.getName(),is(ROLE_THREE));
       assertThat(queriedRole.getRole(),is("Admins"));
       assertThat(queriedRole.getProjectId(),is(PROJECT_ONE_KEY));
       assertThat(queriedRole.getComponents(),is("COMP-10000,COMP-10001"));
       
       adHocRole = queriedRole;
    }
    
    @Test(expected=net.java.ao.ActiveObjectsException.class)

    @NonTransactional
    //@Ignore("If you want this test to pass, comment out the ao.executeInTransaction of service, not compatible with unit testing but needed for prod use.")
    public void testARoleWithSameNameAndProjectCanNotBeCreated(){
        SupportTeam role = assignmentService.createProjectTeam(PROJECT_ONE_KEY,ROLE_THREE,"Admins",null);
        role = assignmentService.createProjectTeam(PROJECT_ONE_KEY,ROLE_THREE,"Admins",null);
        assertThat(role.getID(),nullValue());
    }

    @Test
    @NonTransactional
    //@Ignore("If you want this test to pass, comment out the ao.executeInTransaction of service, not compatible with unit testing but needed for prod use.")
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
    @NonTransactional
    //@Ignore("If you want this test to pass, comment out the ao.executeInTransaction of service, not compatible with unit testing but needed for prod use.")
    public void testTheNextAssigneeCanBeRetrieved() throws InterruptedException{
        SupportMember nextGuy = assignmentService.assignNextAvailableAssigneeForProjectTeam(PROJECT_ONE_KEY,ROLE_ONE);
        assertThat(nextGuy,notNullValue());
        assertThat(nextGuy.getPrincipleName(),notNullValue());
        //second assignee is missing an assigned date, should be oldest
         assertThat(nextGuy.getPrincipleName(),is(SECOND_ASSIGNEE));
         //mysql uses DATETIME so we need atleast 1 second diff in order.
         Thread.sleep(1500L);
         nextGuy = assignmentService.assignNextAvailableAssigneeForProjectTeam(PROJECT_ONE_KEY,ROLE_ONE);
         assertThat(nextGuy.getPrincipleName(),is(FIRST_ASSIGNEE));
         //mysql uses DATETIME so we need atleast 1 second diff in order.
         Thread.sleep(1500L);
         nextGuy = assignmentService.assignNextAvailableAssigneeForProjectTeam(PROJECT_ONE_KEY,ROLE_ONE);
          assertThat(nextGuy.getPrincipleName(),is(SECOND_ASSIGNEE));
    }
    
    
    /** 
     * i.e Bryan goes on FTO, Mike is heads-down on a secret project
     */
    @Test
    @NonTransactional
   // @Ignore("If you want this test to pass, comment out the ao.executeInTransaction of service, not compatible with unit testing but needed for prod use.")
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
    @NonTransactional
    //@Ignore("If you want this test to pass, comment out the ao.executeInTransaction of service, not compatible with unit testing but needed for prod use.")
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
    @NonTransactional
   // @Ignore("If you want this test to pass, comment out the ao.executeInTransaction of service, not compatible with unit testing but needed for prod use.")
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
    @NonTransactional
    //@Ignore("If you want this test to pass, comment out the ao.executeInTransaction of service, not compatible with unit testing but needed for prod use.")
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
    
    /**
     * AEFJ-18 reported that postgres treats null dates > a real date. 
     * So after the first assignee of a team is poplualted they are awys on the hook
     * @throws SQLException 
     * @throws InterruptedException 
     */
    @Test
    @NonTransactional
    public void testThatNullDatesDoNotBreakAssignment() throws SQLException, InterruptedException{
        
        // create a new dataset with all new team
        final SupportTeam todo = entityManager.create(SupportTeam.class);
        todo.setProjectId(PROJECT_ZETA_KEY);
        todo.setName(ROLE_ZETA);
        todo.setRole("Developers");
        todo.save();

        final SupportMember me = entityManager.create(SupportMember.class);
        me.setPrincipleName(FIRST_ASSIGNEE);
        me.save();
        final SupportMember moe = entityManager.create(SupportMember.class);
        moe.setPrincipleName(SECOND_ASSIGNEE);
        moe.save();
        final SupportMember max = entityManager.create(SupportMember.class);
        max.setPrincipleName(THIRD_ASSIGNEE);
        max.save();
        
        // all dates left empty! only 2 assignable
        final TeamToUser roleToPerson = entityManager.create(TeamToUser.class);
        roleToPerson.setAssignable(true);
        roleToPerson.setProjectRole(todo);
        roleToPerson.setUser(me);
        roleToPerson.save();
        final TeamToUser roleToPerson2 = entityManager.create(TeamToUser.class);
        roleToPerson2.setAssignable(true);
        roleToPerson2.setProjectRole(todo);
        roleToPerson2.setUser(moe);
        roleToPerson2.save();
        final TeamToUser roleToPerson3 = entityManager.create(TeamToUser.class);
        roleToPerson3.setProjectRole(todo);
        roleToPerson3.setUser(max);
        roleToPerson3.save();
        
        //we won't know who we'll get first, as long as they rotate
        String lastPick="";
        
        //first pull, could be anyone, as long as its someone.
        SupportMember nextGuy = assignmentService.assignNextAvailableAssigneeForProjectTeam(PROJECT_ZETA_KEY,ROLE_ZETA);
        assertThat(nextGuy,notNullValue());
        assertThat(nextGuy.getPrincipleName(),notNullValue());
        lastPick = nextGuy.getPrincipleName();
        
        //second assignee must not be the first!
         Thread.sleep(1500L);
         nextGuy = assignmentService.assignNextAvailableAssigneeForProjectTeam(PROJECT_ZETA_KEY,ROLE_ZETA);
         assertThat(nextGuy.getPrincipleName(),not(lastPick));
         lastPick = nextGuy.getPrincipleName();
         
         //3rd assignment should be back to the first
         Thread.sleep(1500L);
         nextGuy = assignmentService.assignNextAvailableAssigneeForProjectTeam(PROJECT_ZETA_KEY,ROLE_ZETA);
          assertThat(nextGuy.getPrincipleName(),not(lastPick));
        
        
        
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
            roleToPerson.setLastAssigned(new Date(1000000L));
            roleToPerson.setProjectRole(todo);
            roleToPerson.setUser(me);
            roleToPerson.save();
            final TeamToUser roleToPerson2 = em.create(TeamToUser.class);
            roleToPerson2.setAssignable(true);
            roleToPerson2.setLastAssigned(new Date(1L));
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