var selectedAjsParams = {
            projectId: AJS.$("#projectId").val(),
            roles: rolesData
        }   
var template = JIRA.Templates.AssignEscalate.newSupportTeam(selectedAjsParams);  













AJS.$("#create-support-team").click(function() {
    // PREPARE FOR DISPLAY
	// Note this is a small dialog, so it fits in the Sandbox panel
	// Standard sizes are 400, 600 and 840 pixels wide
	var dialog = new AJS.Dialog({
	    width: 600, 
	    height: 400, 
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

	//define submit action


	//JS used to submit changes on button click
	var configEndpoint = AJS.contextPath() + "/rest/support-teams/1.0/team" ;

	dialog.addButton("Create", function (dialog) {
		console.log(AJS.$('form#newTeam').serialize())
		 jQuery.ajax({
		     url: configEndpoint,
		     data: AJS.$('form#newTeam').serialize(),
		     type: 'POST',
		     dataType: 'json',
		     async: false,
		     success: function(data) {
		         AJS.messages.success({
		            title: "Saved!",
		            body: "New team created! ."
		         }); 

		         console.log(data);
		         jQuery.ajax({
		    	     url: configEndpoint + "/" + data.id + "/reindex",
		    	     data: "hi mr server",
		    	     type: 'POST',
		    	     dataType: 'json',
		    	     async: true,
		    	     success: function(data){
		    	    	 console.log(data);
		    	    	 AJS.$("#tabsMenu").append("<li class=\"menu-item active-tab\"><a href=\"#tabs-team-" + data.id + "\"><strong>" + data.name + "</strong></a></li>");

		    	    	 var newTab = JIRA.Templates.AssignEscalate.supportTeamTab({team:data});  
			   	          AJS.$("#tabs").append(newTab); 
			   	         
		    	     },
		    	     error: function(data) {
		    	        AJS.messages.warning({
		    	            title: "resync needed",
		    	            body: "But Team sync failed, please try manually after the page reloads." 
		    	         }); 
		    	     } 
		    	  });

		 	        dialog.remove();
		         //AJS.$('form#newTeam');
		     } ,
		     error: function(data) {
		        AJS.messages.warning({
		            title: "Oh no!",
		            body: "Recieved error code: " + data.status + ", " + data.statusText
		         }); 
		     } 
		  });
	});


	dialog.addLink("Cancel", function (dialog) {
	    dialog.hide();
	}, "#");
    dialog.gotoPanel(0);
    dialog.show();
});



// Add events to dialog trigger elements
AJS.$("#create-support-team").enable();

    
