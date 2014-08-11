package com.edwardawebb.jira.assignescalate.ao.service;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import net.java.ao.ActiveObjectsException;
import net.java.ao.DBParam;
import net.java.ao.EntityStreamCallback;
import net.java.ao.Query;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.edwardawebb.jira.assignescalate.ao.SupportTeam;
import com.edwardawebb.jira.assignescalate.ao.TeamToUser;
import com.edwardawebb.jira.assignescalate.ao.SupportMember;
import com.edwardawebb.jira.assignescalate.jobs.SyncProjectRoleUsersMonitorImpl;
import com.google.common.collect.Lists;

public class DefaultAssignmentService implements AssignmentService {

    private final Logger logger = Logger.getLogger(DefaultAssignmentService.class);
    private ActiveObjects ao;

    public DefaultAssignmentService(ActiveObjects activeObjects) {
        this.ao=activeObjects;
    }

    @Override
    public void loadAllProjectTeams(final ProjectRoleStreamCallback callback) {
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
    public SupportTeam getProjectTeam(Integer projectRoleId) {
        return ao.get(SupportTeam.class, projectRoleId);
    }

    @Override
    public SupportTeam createProjectTeam(Long projectId, String name, String projectRole) {
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
    public SupportMember assignNextAvailableAssigneeForProjectTeam(Long projectId, String name) {
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
    }



    @Override
    public SupportTeam updateProjectTeam(SupportTeam role) {
        role.save();
        for (int i = 0; i < role.getAssignments().length; i++) {
            TeamToUser assignment = role.getAssignments()[i];
            assignment.save();
        }
        return role;
        
    }

    @Override
    public void updateUsersLinkedToRole(String[] usersInGroup, SupportTeam role) {
       
        //list of all currently assigned poeple. As we validate roles from the new list, they are 
        // removed from this this. Leftovers are ones who have left JIRA or moved out of the group.
        TeamToUser[] currentAssignments = role.getAssignments();

        //there may be existing assignments to consider, but it may be brand new.
        List<TeamToUser> leftOvers = Lists.newArrayList();
        if(null != currentAssignments){
            leftOvers = Lists.newArrayList(currentAssignments);            
        }
        
        for (int i = 0; i < usersInGroup.length; i++) {
            String name = usersInGroup[i];
            SupportMember user = findOrCreateUser(name);   
            
            TeamToUser teamToUser = findOrCreateAssignment(user,role);
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

    private SupportMember findOrCreateUser(String name) {
        SupportMember[] results = ao.find(SupportMember.class,"NAME = ?",name);
        if (results.length > 1)
        {
            throw new IllegalStateException("Application cannot have more than 1 user with same Principle Name");
        }
        if ( results.length > 0 ){
            return results[0] ;
        }else{
            SupportMember user = ao.create(SupportMember.class,new DBParam("NAME",name));
            return user;
        }
    }

    private SupportTeam findRoleByProjectIdAndName(Long projectId, String name) {
        SupportTeam[] results = ao.find(SupportTeam.class,Query.select().where("PROJECTID = ? and name = ?",projectId,name));
        if (results.length > 1)
        {
            throw new IllegalStateException("Application cannot have more than 1 blog");
        }
        return results.length > 0 ? results[0] : null;
    }



  

}
