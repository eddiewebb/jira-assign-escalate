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
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.transaction.TransactionCallback;
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
        this.ao = activeObjects;
    }

    @Override
    public void loadAllProjectTeams(final ProjectTeamAssignerCallback callback) {
        ao.stream(SupportTeam.class, new EntityStreamCallback<SupportTeam, Integer>() {
            @Override
            public void onRowRead(SupportTeam t) {
                callback.valueRead(t);
            }
        });
    }

    @Override
    public SupportTeam[] getProjectTeams(Long projectId) {
        try{
            return ao.find(SupportTeam.class, Query.select().where("PROJECTID = ?", projectId));
        }catch (ActiveObjectsException aoException){
            logger.error("exception using uppercase lowercase colums as fallback");
            return ao.find(SupportTeam.class, Query.select().where("projectId = ?", projectId));
        }
    }

    @Override
    public SupportTeam getProjectTeam(Integer teamId) {
        return ao.get(SupportTeam.class, teamId);
    }

    @Override
    public SupportTeam createProjectTeam(Long projectId, String name, String projectRole, List<String> components) {

        logger.warn("Request for new team: " + name + " id " + projectId);
        SupportTeam existingTeam = findSupportTeamByProjectIdAndName(projectId, name);

        if (null == existingTeam) {
            // good, does not exist
            StringBuilder componentIds = new StringBuilder();
            if (null != components && components.size() > 0) {
                for (String id : components) {
                    componentIds.append("COMP-").append(id).append(",");
                }
                componentIds.deleteCharAt(componentIds.length() - 1);
            }
            final SupportTeam role = ao.create(SupportTeam.class, new DBParam("NAME", name), new DBParam("ROLE",
                    projectRole), new DBParam("PROJECTID", projectId), new DBParam("CMPNTS", componentIds.toString()));
            return role;
        } else {
            throw new ActiveObjectsException("Team names are unique to each project");
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
       
                final SupportTeam role = findSupportTeamByProjectIdAndName(projectId, name);
                if (null != role) {
                    //some engines :cough: postgres :cough: do funny soritng on dates with null, trating them larger then dates
                    // See AEFJ-18
                    SupportMember[] members = {};
                    try{
                        members = ao.find(
                            SupportMember.class,
                            Query.select().alias(TeamToUser.class, "am").alias(SupportMember.class, "sm")
                                    .join(TeamToUser.class, "sm.ID = USERID")
                                    .where("TEAMID = ? and HIDE = ? and ASSIGN = ? and LASTDATE IS NULL", role.getID(),Boolean.FALSE,Boolean.TRUE)
                                   );

                        logger.debug("There are " + members.length +" assignables with null dates");
                    }catch(ActiveObjectsException aoe){
                        //empty results will be populated below using alternate DB query
                    }
                    //
                    if(members.length < 1){
                        members = ao.find(
                            SupportMember.class,
                            Query.select().alias(TeamToUser.class, "am").alias(SupportMember.class, "sm")
                                    .join(TeamToUser.class, "sm.ID = USERID")
                                    .where("TEAMID = ? and HIDE = ? and ASSIGN = ? ", role.getID(),Boolean.FALSE,Boolean.TRUE)
                                    .order("LASTDATE"));
                        logger.debug("There are " + members.length +" assignables with existing dates");
                    }
                    final SupportMember next = members.length > 0 ? members[0] : null;
                    if (null != next) {
                        final TeamToUser[] history = ao.find(TeamToUser.class,
                                Query.select().where("TEAMID = ? and USERID = ?", role.getID(), next.getID()));
                        
                        ao.executeInTransaction(new TransactionCallback<Void>() {
                            @Override
                            public Void doInTransaction() {
                                history[0].setLastAssigned(new Date());
                                history[0].save();
                                return null;
                            }
                        });                   
                    return next;
                } else {
                    return null;// no team defined
                }
            }else{
                return null;
            }
    }

    @Override
    public SupportTeam updateProjectTeam(final SupportTeam role) {
        return ao.executeInTransaction(new TransactionCallback<SupportTeam>() {

            @Override
            public SupportTeam doInTransaction() {
                role.save();
                for (int i = 0; i < role.getAssignments().length; i++) {
                    TeamToUser assignment = role.getAssignments()[i];
                    assignment.save();
                }
                return role;
            }
        });

    }

    @Override
    public SupportTeam updateProjectTeam(final Integer teamId, final List<String> activeUsers) {
        SupportTeam team = getProjectTeam(teamId);
        for (int i = 0; i < team.getAssignments().length; i++) {
            TeamToUser assignment = ao.get(TeamToUser.class,team.getAssignments()[i].getID());
            logger.warn("Activating " + assignment.getUser() + "? "
                    + activeUsers.contains(assignment.getUser().getPrincipleName()));
           assignment.setAssignable(activeUsers.contains(assignment.getUser()
                    .getPrincipleName()));

            assignment.save();
            logger.warn("Saved assignment as:" + team.getAssignments()[i].isAssignable());
        }
        return team;

    }

    @Override
    public void updateUsersLinkedToTeam(final Set<ApplicationUser> latestUsers, final SupportTeam role) {
        // ao.executeInTransaction(new TransactionCallback<Object>(){
        // @Override
        // public Object doInTransaction() {
        // list of all currently assigned poeple. As we validate roles from the
        // new list, they are
        // removed from this this. Leftovers are ones who have left JIRA or
        // moved out of the group.
        TeamToUser[] currentAssignments = role.getAssignments();

        // there may be existing assignments to consider, but it may be brand
        // new.
        List<TeamToUser> leftOvers = Lists.newArrayList();
        if (null != currentAssignments) {
            leftOvers = Lists.newArrayList(currentAssignments);
        }

        for (ApplicationUser user : latestUsers) {
            SupportMember teamMember = createOrUpdateUser(user.getKey(), user.getName(), user.getDisplayName());

            TeamToUser teamToUser = findOrCreateAssignment(teamMember, role);
            if (teamToUser.isHidden()) {
                // previously existent but hidden, show it
                teamToUser.setHidden(false);
                teamToUser.save();
            }
            leftOvers.remove(teamToUser);
        }

        for (TeamToUser defunctAssignment : leftOvers) {
            defunctAssignment.setHidden(true);
            defunctAssignment.save();
        }
        // return null;
        // }
        //
        // });

    }

    private TeamToUser findOrCreateAssignment(SupportMember user, SupportTeam role) {
        TeamToUser[] results = ao.find(TeamToUser.class, "USERID = ? AND TEAMID = ?", user.getID(), role.getID());
        if (results.length > 1) {
            throw new IllegalStateException(
                    "Application cannot have more than 1 Assingment mapping between a project Role and User.");
        }
        if (results.length > 0) {
            return results[0];
        } else {
            TeamToUser mapping = ao.create(TeamToUser.class, new DBParam("USERID", user.getID()), new DBParam("TEAMID",
                    role.getID()));
            return mapping;
        }
    }

    private SupportMember createOrUpdateUser(String key, String name, String displayName) {
        SupportMember[] results = ao.find(SupportMember.class, "KEY = ?", key);
        if (results.length > 1) {
            throw new IllegalStateException("Application cannot have more than 1 user with same user key. Conflict:" + key);
        }
        if (results.length > 0) {
            SupportMember user = results[0];
            if( ! user.getDisplayName().equals(displayName) ){
                user.setDisplayName(displayName);
                user.save();
            }
            if( ! user.getPrincipleName().equals(name) ){
                user.setPrincipleName(name);
                user.save();
            }
            return user;
        } else {
            SupportMember user = ao.create(SupportMember.class, new DBParam("KEY", key), new DBParam("NAME", name),
                    new DBParam("DISPLAY", displayName));
            return user;
        }
    }

    private SupportTeam findSupportTeamByProjectIdAndName(Long projectId, String name) {
        SupportTeam[] results = ao.find(SupportTeam.class,
                Query.select().where("PROJECTID = ? and NAME = ?", projectId, name));
        if (results.length > 1) {
            throw new IllegalStateException("Application cannot have more than 1 team per project with marching names");
        }
        return results.length > 0 ? results[0] : null;
    }

    @Override
    public SupportTeam[] findAllTeamsWith(Long projectId, ProjectComponent component) {
        SupportTeam[] results = ao.find(SupportTeam.class,
                Query.select().where("PROJECTID = ? AND CMPNTS like ?", projectId, "%COMP-" + component.getId() + "%"));

        return results;
    }

}
