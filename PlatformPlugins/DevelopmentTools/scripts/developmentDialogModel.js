// @page=developmentDialogModel.js

$(document).ready(function(){
	var devDialog = $("#developmentToolsDialog");
	var token = getUserToken();
	$(devDialog.find("#temporaryId")).text(token);
});