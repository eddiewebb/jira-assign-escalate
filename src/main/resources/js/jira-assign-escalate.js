




/*
 * this is a function called at the bottom of this own file, 
 * but also by the create dialog as new teams are added
 */

var updateEventBindings = function(){
	//console.log("DEBUG: updating event bdinings");
	AJS.$('.selectAll').click(function(e) {  //on click 
		console.log("selectAll clicked");
	 	var teamId = AJS.$(e.target).attr("data-id");
	        if(this.checked) { // check select status

	    		console.log("selecting all");
	            AJS.$('.activatedUsers-'+teamId).each(function() { //loop through each checkbox
	                this.checked = true;  //select all checkboxes with class "checkbox1"               
	            });
	        }else{
	    		console.log("UNselectAll clicked");
	            AJS.$('.activatedUsers-'+teamId).each(function() { //loop through each checkbox
	                this.checked = false; //deselect all checkboxes with class "checkbox1"                       
	            });         
	        }
	    });
	AJS.$(".astupdate").click(function(e){

		e.preventDefault();
		//JS used to submit changes on button click
		console.log(e);
	 	//var teamId = AJS.$(e.srcElement).attr("data-id");
		var btnElement = e.target || e.srcElement;
	 	var teamId = AJS.$(btnElement).attr("data-id");
	 	console.log("Team ID is " + teamId);
		var configEndpoint = AJS.contextPath() + "/rest/support-teams/1.0/team/" + teamId;
	 jQuery.ajax({
	     url: configEndpoint,
	     data: AJS.$(AJS.$(e.target).parents("form")).serialize()
	     ,
	     type: 'PUT',
	     dataType: 'json',
	     async: false,
	     success: function(data) {
	         AJS.messages.success({
	            title: "Saved!",
	            body: "Available assignees have been updated."
	         }); 
	     } ,
	     error: function(data) {
	        AJS.messages.warning({
	            title: "Oh no!",
	            body: "Recieved error code: " + data.status + ", " + data.statusText
	         }); 
	     } 
	  });
	 
	});
	 
	 
	 
	 /*
	  * Edit team
	  */


	 AJS.$(".resyncButton").click(function(e){

	 	e.preventDefault();
	 	//JS used to submit changes on button click
	 	console.log(e);
	 	var teamId = AJS.$(e.target).attr("data-id");
	 	var configEndpoint = AJS.contextPath() + "/rest/support-teams/1.0/team/" + teamId + "/reindex";
	  jQuery.ajax({
	      url: configEndpoint,
	      data: "1"
	      ,
	      type: 'POST',
	      dataType: 'json',
	      async: false,
	      success: function(data) {
	          AJS.messages.success({
	             title: "Saved!",
	             body: "The users belonging to the selected role have been re-synced."
	          }); 
	          var selectedAjsParams = {
	                  teamUsers: data.users
	              }   
	          var updatedUsers = JIRA.Templates.AssignEscalate.supportTeamRows(selectedAjsParams);  
	          AJS.$("#tableBody-" + teamId).html(updatedUsers);
	      } ,
	      error: function(data) {
	         AJS.messages.warning({
	             title: "Oh no!",
	             body: "Recieved error code: " + data.status + ", " + data.statusText
	          }); 
	      } 
	   });
	  
	 });
	 
	 
	 

	 AJS.$(".deleteButton").click(function(e){

	 	e.preventDefault();
	 	//JS used to submit changes on button click
	 	console.log(e);
	 	var teamId = AJS.$(e.target).attr("data-id");
	 	var configEndpoint = AJS.contextPath() + "/rest/support-teams/1.0/team/" + teamId ;
		  jQuery.ajax({
		      url: configEndpoint,
		      data: "1"
		      ,
		      type: 'DELETE',
		      dataType: 'text',
		      async: false,
		      success: function(data) {
		          AJS.messages.success({
		             title: "Gone!",
		             body: "That team is no more!"
		          }); 
		          AJS.$("#tabLink-" + teamId).remove();
		          AJS.$("#tabs-team-" + teamId).remove();
		          
		      } ,
		      error: function(data) {
		         AJS.messages.warning({
		             title: "Oh no!",
		             body: "Recieved error code: " + data.status + ", " + data.statusText
		          }); 
		      } 
		   });
	  
	 });
}

AJS.$( document ).ready(function() {

	if(window.location.href.indexOf("com.edwardawebb.jira-assign-escalate:support-panel") > -1){
		updateEventBindings();
		bindTeamDialog();
	}

});



// AEFJ-6 - when already viewing any other priject tab, only ajax is used to load additonal tabs.
// We need to re-bind our events.
// only needed for JIRA < 6.4, as new project dashboard uses full page reloads.
AJS.$(document).ajaxSuccess(function (e,jqXHR,ajaxOptions){
	   var requestUrl = ajaxOptions.url;
	   if(requestUrl.indexOf("selectedTab=com.edwardawebb.jira-assign-escalate:support-panel") > -1){
		  updateEventBindings();
		  bindTeamDialog();
		  AJS.tabs.setup();
		  
	   }
});

