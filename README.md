Assign & Escalate Plugin for Atlassian JIRA
-------------------------------------------



WHat it do
==========

This plugin allows each project in JIRA to define multiple support teams. Each support team may then be used in the provided Workflow Post-Functions to assign new tickets or escalate existing tickets to the appropriate team.


###Examples

#### Assign new tickets to L1
1. Create a Support Team "Level One" and check the appropriate users.
1. Edit the workflow to use the Assign-Support-Member on the initial "create" stage.

### Escalate tickets
1. Create a Support Team "Level Two" and check the appropriate users.
1. Edit the workflow to use the Escalate-Support-Member on a new Global transition "Escalate"



### Modules
1. Project Tab Panel
Provides means to create and edit teams.
1. Assign Support Member Workflow Post-Function
Will assign the next available member without regard for currently assigned user.
1. Escalate Support Member Workflow Post-Function
Will assign the next available support member making sure it is not the same as the current, and 'ding' the current assignee.
1. Team User Sync Job & Scheduler
These 2 modules work together to regularly update the member listings for teams based on the selected role. Users will be added or removed as the underlying User Directory evolves.



