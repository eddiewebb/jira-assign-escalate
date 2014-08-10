package com.edwardawebb.jira.assignescalate.ao;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.ManyToMany;
import net.java.ao.Mutator;
import net.java.ao.Preload;
import net.java.ao.schema.AutoIncrement;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.PrimaryKey;
import net.java.ao.schema.Table;


/**
 * This represents a single "role" for a project.
 *  A single project may have many support teams, each grouped into their "role".
 * 
 */
@Table("ASROLES")
@Preload
public interface ProjectRole extends Entity {
    @AutoIncrement
    @NotNull
    @PrimaryKey("ID")
    public int getID();
    
    
    @Accessor("PROJECTID")
    long getProjectId();
    @Mutator("PROJECTID")
    void setProjectId(long projectId);

    /*
     * This is the name for a support group based on component or otherwise
     * "DB Team","Web Team","Level 1", etc.
     */
    @Accessor("NAME")
    String getName();
    @Mutator("NAME")
    void setName(String name);
    
    /*
     * This is the role used to populate the team "Developers","Admins", etc/
     */
    @Accessor("ROLE")
    String getRole();
    @Mutator("ROLE")
    void setRole(String projectRole);

    @ManyToMany(value = ProjectRoleAssignmentMapping.class,reverse="getProjectRole",through="getSupportMember")
    SupportMember[] getAssignees();

    
}
