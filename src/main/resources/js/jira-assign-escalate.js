AJS.$('#selectAll').click(function(event) {  //on click 
        if(this.checked) { // check select status
            AJS.$('.selectedPeople').each(function() { //loop through each checkbox
                this.checked = true;  //select all checkboxes with class "checkbox1"               
            });
        }else{
            AJS.$('.selectedPeople').each(function() { //loop through each checkbox
                this.checked = false; //deselect all checkboxes with class "checkbox1"                       
            });         
        }
    });
