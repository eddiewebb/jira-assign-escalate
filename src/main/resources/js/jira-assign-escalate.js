AJS.$('#selectAll').click(function(event) {  //on click 
        if(this.checked) { // check select status
            AJS.$('.activatedUsers').each(function() { //loop through each checkbox
                this.checked = true;  //select all checkboxes with class "checkbox1"               
            });
        }else{
            AJS.$('.activatedUsers').each(function() { //loop through each checkbox
                this.checked = false; //deselect all checkboxes with class "checkbox1"                       
            });         
        }
    });






AJS.$(".astupdate").click(function(e){

	e.preventDefault();
	//JS used to submit changes on button click
	console.log(e);
	var teamId = AJS.$(e.srcElement).parent().children("#teamId")[0].value;
	var configEndpoint = AJS.contextPath() + "/rest/support-teams/1.0/team/" + teamId;
 jQuery.ajax({
     url: configEndpoint,
     data: AJS.$(e.srcElement).parent().serialize()
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