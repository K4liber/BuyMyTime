function getEndCall(username){
	$.ajax({
        type : "GET",
        url : "endCall/" + username,
        success: function(data){
        	console.log(data);
        }
    });
}

function postProfileImage(formData){
	$.ajax({
        url: "edit",
        type: "POST",
        data: formData,
        mimeTypes:"multipart/form-data",
        contentType: false,
        cache: false,
        processData: false,
        success: function(){
            alert("file successfully submitted");
        },error: function(){
            alert("okey");
        }
	});
}

function postFileMessage(formData){
	$.ajax({
        url: "fileMessage",
        type: "POST",
        data: formData,
        mimeTypes:"multipart/form-data",
        contentType: false,
        cache: false,
        processData: false,
        success: function(){
            alert("file successfully submitted");
        },error: function(){
            alert("okey");
        }
	});
}

function postCardModel(cardModelData){
	$.ajax({
	    url: "example",
	    contentType: "application/json",
	    type: "POST",
	    data: cardModelData,
	    dataType: 'json',
	    processData: false,
	    success: function(){
	        alert("file successfully submitted");
	    },error: function(){
	        alert("okey");
	    }
	});
}

function getConnections(username){
	$.ajax({
        type : "GET",
        url : "connections/" + username,
        success: function(data){
        	console.log(data);
        }
    });
}

function getLogin() {
	$.ajax({
        type : "GET",
        url : "login",
        success: function(data){
        	homeHtml(data);
        }
    });
}

function getHome() {
	$.ajax({
        type : "GET",
        url : "categories",
        success: function(data){
        	homeHtml(data);
        }
    });
}

function getCards() {
	$.ajax({
        type : "GET",
        url : "cards",
        success: function(data){
        	cardsHtml(data);
        }
    });
}

function getCardsByCategory(categoryName) {
	$.ajax({
        type : "GET",
        url : "cards/" + categoryName,
        success: function(data){
        	cardsHtml(data);
        }
    });
}

function getProfile() {
	$.ajax({
        type : "GET",
        url : "username",
        success: function(data){
        	getUserProfile(data);
        }
    });
}

function getUserProfile(username){
	$.ajax({
        type : "GET",
        url : "profile/" + username,
        success: function(data){
        	profileHtml(data);
        }
    });
}

function getContacts(){
	$.ajax({
        type : "GET",
        url : "contacts",
        success: function(data){
        	contactsHtml(data);
        }
    });
}

function addContact(contactUsername){
	$.ajax({
        type : "GET",
        url : "addContact/" + contactUsername,
        success: function(data){
        	if(data == "success"){
        		successContactDialogAddHtml(contactUsername);
        	}else if(data == "exist"){
        		existContactDialogHtml(contactUsername);
        	}
        }
    });
}

function categoryProfile(username){
	$.ajax({
        type : "GET",
        url : "profile/" + username,
        success: function(data){
        	categoryProfileHtml(data);
        }
    });
}


function getAbout() {
	$.ajax({
        type : "GET",
        url : "about",
        data : {
        "username" : "bolek"
        },
        success: function(data){
        	aboutHtml(data);
        }
    });
}

function getContact(contactUsername){
	$.ajax({
        type : "GET",
        url : "contact/" + contactUsername,
        success: function(data){
        	contactHtml(data);
        }
    });
}

function getLogout() {
	$.ajax({
        type : "GET",
        url : "logout",
        success: function(data){
        }
    });
}

function getExit() {
	$.ajax({
		type: "GET",
		url: "exit"
	});
}

function getUsername(onSuccess) {
	$.ajax({
        type : "GET",
        url : "username",
        success: function(data){
        	if(data != "err")
        		onSuccess(data);
        }
    });
}

function getFileName(onSuccess, id){
	$.ajax({
        type : "GET",
        url : "fileName/" + id,
        success: function(data){
        	if(data != "err")
        		onSuccess(data);
        }
    });
}