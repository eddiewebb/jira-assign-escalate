var selectedAjsParams = {
            projectId: AJS.$("#projectId").val()
        }   
var template = JIRA.Templates.AssignEscalate.newSupportTeam(selectedAjsParams);  















// Note this is a small dialog, so it fits in the Sandbox panel
// Standard sizes are 400, 600 and 840 pixels wide
var dialog = new AJS.Dialog({
    width: 400, 
    height: 300, 
    id: "new-support-team", 
    closeOnOutsideClick: true
});

// PAGE 0 (first page)
// adds header for first page
dialog.addHeader("New Support Team");

// add panel 1
dialog.addPanel("Create Your Team", template, "panel-body");
// You can remove padding with:
// dialog.get("panel:0").setPadding(0);

// add panel 2 (this will create a menu on the left side for selecting panels within page 0)
dialog.addPanel("Huh? Help.", "<p>Teams are used by the Assign Support and Escalate Level Two workflow functions.</p><p>You may create as many teams as you wish.</p>", "panel-body");

dialog.addButton("Create", function (dialog) {
    dialog.create();
});
dialog.addLink("Cancel", function (dialog) {
    dialog.hide();
}, "#");

// Add events to dialog trigger elements
AJS.$("#create-support-team").enable();
AJS.$("#create-support-team").click(function() {
    // PREPARE FOR DISPLAY
    // start first page, first panel
    dialog.gotoPanel(0);
    dialog.show();
});
    
