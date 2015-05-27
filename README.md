Assign & Escalate Plugin for Atlassian JIRA
-------------------------------------------

For the latest docs and more details please head over to https://eddiewebb.atlassian.net/wiki/display/AEFJ/Assign+and+Escalate+for+JIRA+Home




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


## LIcense
    Copyright (C) 2015  Edward A Webb

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>

