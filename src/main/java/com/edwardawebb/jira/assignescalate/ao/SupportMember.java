package com.edwardawebb.jira.assignescalate.ao;

import java.util.Date;

import net.java.ao.Accessor;
import net.java.ao.Entity;
import net.java.ao.ManyToMany;
import net.java.ao.Mutator;
import net.java.ao.Preload;
import net.java.ao.schema.Default;
import net.java.ao.schema.Table;


/**
 * this interface is used by @AssigmentConfigurationActiveObjectsService  which exposes @AssignmentConfigurationResource objects to manipulate.
 * 
 * Classes should not extend or implement this interface.
 * 
 * @author n0158588
 *
 */
@Table("ASSTAT")
@Preload
public interface SupportMember extends Entity {

    
    @Accessor("NAME")
    public String getPrincipleName();
    @Mutator("NAME")
    public void setPrincipleName(String name);


    @ManyToMany(value = ProjectRoleAssignmentMapping.class,through="getProjectRole",reverse="getSupportMember")
    ProjectRole[] getProjectRoles();
}
