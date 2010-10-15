

 function requestConfirmation(formId,messageKey,titleKey) {
	 jQuery.alerts.overlayOpacity= 0.4;
	 jQuery.alerts.overlayColor= '#333';
	 jConfirm(messageKey, titleKey,function(userInput) {
		  if(userInput) {
			  $("#" + formId).submit(); 
           }
        });
}
 
 function requestConfirmationForJQueryForm(form,messageKey,titleKey) {
	 jQuery.alerts.overlayOpacity= 0.4;
	 jQuery.alerts.overlayColor= '#333';
	 jConfirm(messageKey, titleKey,function(userInput) {
		  if(userInput) {
			 form.submit(); 
           }
        });
} 
 

function linkConfirmationHook(linkId, messageKey, titleKey) {
  var href = jQuery("#" + linkId ).attr('href');
  jQuery("#" + linkId).click(function() {
	  requestConfirmation(linkId + "form",messageKey,titleKey);
        });
  jQuery("#" + linkId).attr('href',"#");
  jQuery("<form id='" +  linkId + "form' action='" + href + "' method=\"post\"'></form>").insertBefore("#" + linkId);
}
