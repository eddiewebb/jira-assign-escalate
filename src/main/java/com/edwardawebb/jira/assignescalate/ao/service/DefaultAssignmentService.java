package com.edwardawebb.jira.assignescalate.ao.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import net.java.ao.ActiveObjectsException;
import net.java.ao.DBParam;
import net.java.ao.EntityStreamCallback;
import net.java.ao.Query;

import org.apache.log4j.Logger;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.user.ApplicationUser;
import com.edwardawebb.jira.assignescalate.AssignmentService;
import com.edwardawebb.jira.assignescalate.ao.SupportMember;
import com.edwardawebb.jira.assignescalate.ao.SupportTeam;
import com.edwardawebb.jira.assignescalate.ao.TeamToUser;
import com.edwardawebb.jira.assignescalate.jobs.ProjectTeamAssignerCallback;
import com.google.common.collect.Lists;

public class DefaultAssignmentService implements AssignmentService {

    private final Logger logger = Logger.getLogger(DefaultAssignmentService.class);
    private ActiveObjects ao;

    public DefaultAssignmentService(ActiveObjects activeObjects) {
        this.ao=activeObjects;
    }

    @Override
    public void loadAllProjectTeams(final ProjectTeamAssignerCallback callback) {
        ao.stream(SupportTeam.class, new EntityStreamCallback<SupportTeam,Integer>(){
            @Override
            public void onRowRead(SupportTeam t){
                callback.valueRead(t);
            }
        });
    }
    
    @Override
    public SupportTeam[] getProjectTeams(Long projectId) {
        return ao.find(SupportTeam.class,Query.select().where("projectId = ?",projectId));
    }

    @Override
    public SupportTeam getProjectTeam(Integer teamId) {
        return ao.get(SupportTeam.class, teamId);
    }

    @Override
    public SupportTeam createProjectTeam(Long projectId, String name, String projectRole) {
        logger.warn("Request for new team: " + name + " id " + projectId);
        SupportTeam existingrole = findRoleByProjectIdAndName(projectId, name);
        if ( null == existingrole ){
            // good, does not exist
        
            final SupportTeam role = ao.create(SupportTeam.class, new DBParam("NAME",name),
                    new DBParam("ROLE",projectRole),new DBParam("PROJECTID", projectId));
            return role;
        }else{
            throw new ActiveObjectsException("Role names are unique to each project");
        }
    }


    @Override
    public Integer deleteProjectTeam(Integer teamId) {
        logger.warn("Deleteing Project Team:" + teamId);
        int middles = ao.deleteWithSQL(TeamToUser.class, "TEAMID = ?", teamId);
        logger.warn("removed " + middles + " team assignments");
        int impact = ao.deleteWithSQL(SupportTeam.class, "ID = ?", teamId);
        logger.warn("removed " + impact + " team");
        
        return impact;
    }


    @Override
    public SupportMember assignNextAvailableAssigneeForProjectTeam(final Long projectId, final String name) {
      // return ao.executeInTransaction(new TransactionCallback<SupportMember>(){

      //      @Override
      //      public SupportMember doInTransaction() {
                SupportTeam role = findRoleByProjectIdAndName(projectId,name);
                
                SupportMember[] members = ao.find(SupportMember.class,Query.select()
                        .alias(TeamToUser.class, "am")
                        .alias(SupportMember.class, "sm")
                        .join(TeamToUser.class,"sm.ID = USERID")
                        .where("TEAMID = ? and HIDE = 0 and ASSIGN = 1", role.getID()).order("LASTDATE"));        
                
                final SupportMember next = members.length > 0 ? members[0] : null;
                if(null != next){
                    TeamToUser[] history = ao.find(TeamToUser.class,Query.select()
                            .where("TEAMID = ? and USERID = ?", role.getID(),next.getID()));
                    history[0].setLastAssigned(new Date());
                    history[0].save();
                }
                
                return next;
       //     }
            
       // });
        
    }



    @Override
    public SupportTeam updateProjectTeam(final SupportTeam role) {
            
                role.save();
                for (int i = 0; i < role.getAssignments().length; i++) {
                    TeamToUser assignment = role.getAssignments()[i];
                    assignment.save();
                }
                return role;
           
        
    }

    @Override
    public SupportTeam updateProjectTeam(final Integer teamId, final List<String> activeUsers) {
//       return ao.executeInTransaction(new TransactionCallback<SupportTeam>(){
//            @Override
//            public SupportTeam doInTransaction() {
                SupportTeam team = getProjectTeam(teamId);
                for (int i = 0; i < team.getAssignments().length; i++) {
                    logger.warn("Activating " + team.getAssignments()[i].getUser() + "? " + activeUsers.contains(team.getAssignments()[i].getUser().getPrincipleName()));
                    team.getAssignments()[i].setAssignable(activeUsers.contains(team.getAssignments()[i].getUser().getPrincipleName()));
                    
                    team.getAssignments()[i].save();
                    logger.warn("Saved assignment as:" + team.getAssignments()[i].isAssignable());
                }
                return team;
//            }
//        });
        
    }
    
    @Override
    public void updateUsersLinkedToTeam(final Set<ApplicationUser> latestUsers,final SupportTeam role) {
//        ao.executeInTransaction(new TransactionCallback<Object>(){
//            @Override
//            public Object doInTransaction() {
              //list of all currently assigned poeple. As we validate roles from the new list, they are 
                // removed from this this. Leftovers are ones who have left JIRA or moved out of the group.
                TeamToUser[] currentAssignments = role.getAssignments();

                //there may be existing assignments to consider, but it may be brand new.
                List<TeamToUser> leftOvers = Lists.newArrayList();
                if(null != currentAssignments){
                    leftOvers = Lists.newArrayList(currentAssignments);            
                }
                
                for (ApplicationUser user : latestUsers) {
                    SupportMember teamMember = findOrCreateUser(user.getKey(), user.getName(),user.getDisplayName());   
                    
                    TeamToUser teamToUser = findOrCreateAssignment(teamMember,role);
                    if(teamToUser.isHidden()){
                        //previously existent but hidden, show it
                        teamToUser.setHidden(false);
                        teamToUser.save();
                    }
                    leftOvers.remove(teamToUser);
                }                                   
                
                for (TeamToUser defunctAssignment : leftOvers) {
                    defunctAssignment.setHidden(true);
                    defunctAssignment.save();
                }       
//                return null;
//            }
//            
//        });
        
    }

   

    private TeamToUser findOrCreateAssignment(SupportMember user, SupportTeam role) {
        TeamToUser[] results = ao.find(TeamToUser.class,"USERID = ? AND TEAMID = ?",user.getID(),role.getID());
        if (results.length > 1)
        {
            throw new IllegalStateException("Application cannot have more than 1 Assingment mapping between a project Role and User.");
        }
       if ( results.length > 0 ){
           return results[0] ;
       }else{
           TeamToUser mapping = ao.create(TeamToUser.class,
                   new DBParam("USERID",user.getID()),
                   new DBParam("TEAMID",role.getID()));
           return mapping;
       }
    }

    private SupportMember findOrCreateUser(String key, String name, String displayName) {
        SupportMember[] results = ao.find(SupportMember.class,"KEY = ? AND NAME LIKE ?",key,name);
        if (results.length > 1)
        {
            throw new IllegalStateException("Application cannot have more than 1 user with same Principle Name");
        }
        if ( results.length > 0 ){
            return results[0] ;
        }else{
            SupportMember user = ao.create(SupportMember.class,new DBParam("KEY",key),new DBParam("NAME",name),new DBParam("DISPLAY",displayName));
            return user;
        }
    }

    private SupportTeam findRoleByProjectIdAndName(Long projectId, String name) {
        SupportTeam[] results = ao.find(SupportTeam.class,Query.select().where("PROJECTID = ? and name = ?",projectId,name));
        if (results.length > 1)
        {
            throw new IllegalStateException("Application cannot have more than 1 team per project with marching names");
        }
        return results.length > 0 ? results[0] : null;
    }


  

}
