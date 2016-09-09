function homeHtml(categories) {
	var h = [''];
	categories.forEach(function(category){
		h.push('<div class="tile">' + category.name + '<\/div>');
	});
	document.getElementById('contents').innerHTML = h.join('');
	$("#chatContents").hide();
	$("#contents").show();
}

function contactsHtml(contacts) {
	var h = [''];
	contacts.forEach(function(contact){
		h.push('<div>' + contact.contactUsername + '<\/div>');
	});
	document.getElementById('contents').innerHTML = h.join('');
	$("#chatContents").hide();
	$("#contents").show();
}

function cardsHtml(cards) {
	var h = [''];
	cards.forEach(function(card){
		h.push('<div><a><h2>' + card.title + '<\/h2><\/a>');
		h.push('<button onClick="getUserProfile(\'' + card.userNick + '\')">' + card.userNick + '<\/button>');
		h.push('<a>' + card.description + '<\/a><\/div>');
	});
	document.getElementById('contents').innerHTML = h.join('');
	$("#chatContents").hide();
	$("#contents").show();
}

function aboutHtml(about) {
	var h = [''];
	h.push('<div>' + about + '<\/div>');
	document.getElementById('contents').innerHTML = h.join('');
	$("#chatContents").hide();
	$("#contents").show();
}

function profileHtml(user) {
	var h = [''];
	h.push('<div><span id="username">' + user.username + '</span><\/div>');
	if(user.status){
		h.push('<div>Online<\/div>');
		if(user.username != document.getElementById('userNick').innerHTML){
			h.push('<button id="call" class="pure-button pure-button-success">Call<\/button>');
			h.push('<button id="addContact" class="pure-button pure-button-warning">Add to Contacts<\/button>');
		}
	}else{
		h.push('<div>Offline<\/div>');
	}
	document.getElementById('contents').innerHTML = h.join('');
	if(user.username != document.getElementById('userNick').innerHTML){
		$("#call").click(function(){
			call(user.username);
	    });
		$("#addContact").click(function(){
			addContact(user.username);
	    });
	}
	$("#chatContents").hide();
	$("#contents").show();
}

function callingDialogHtml(callTo) {
	console.log("callingDialogHtml views.js");
	var h = [''];
	h.push('<p>Calling to ' + callTo + ' ...<\/p>');
	h.push('<button id="cancelCall" onClick="cancelCallClick()" class="pure-button pure-button-warning">Cancel<\/button>');
	document.getElementById('dialog').innerHTML = h.join('');
	$(document).ready(function() {
		$("#dialog").dialog({title: "Outcoming call", closeOnEscape: true, close: function(){ cancelCall(callTo); }});
		$("#dialog").dialog("open");
	});
}

function successContactAddHtml(contactUsername){
	console.log("successContactAddHtml views.js");
	var h = [''];
	h.push('<p>User ' + contactUsername + ' added to Contacts.<\/p>');
	document.getElementById('dialog').innerHTML = h.join('');
	$(document).ready(function() {
		$("#dialog").dialog({title: "Adding successful", closeOnEscape: true });
		$("#dialog").dialog("open");
	});
}

function callDialogHtml(userCalling){
	var h = [''];
	h.push('<p>Calling from ' + userCalling + '<\/p>');
	h.push('<div><button id="answerCall" class="pure-button pure-button-success">Answer<\/button>');
	h.push('<button id="rejectCall" onClick="rejectCallClick()"' 
			+ 'class="pure-button pure-button-error">Reject<\/button><\/div>');
	document.getElementById('dialog').innerHTML = h.join('');
	$(document).ready(function() {
		$("#dialog").dialog({title: "Incoming call", closeOnEscape: true, close: function(){ rejectCall(userCalling); }}).dialog("open");
		$("#answerCall").click(function(){
			$("#dialog").dialog({close: function(){ }}).dialog("close");
			chatContentHtml(userCalling);
			answerCall(userCalling);
	    });
	});
}

function chatContentHtml(username) {
	var h = [''];
	h.push('<div class="left-panel"><video id="their-video" autoplay><\/video><\/div>');
	h.push('<div class="right-panel">');
	h.push('<video id="my-video" autoplay="true" muted="true"><\/video>');
	h.push('<h2>Video Chat with <span id="chatWith">' + username + '</span><\/h2>');
	h.push('<div class="clock" style="display:none;"><\/div><div class="chatContent">');
	h.push('<ul id="messagesList"><\/ul><\/div>');
	h.push('<textarea rows="2" cols="30" id="messageContent"> <\/textarea>');
	h.push('<p><button class="pure-button" id="send" onClick="sendMessage()">Send<\/button><\/p>');
	h.push('<div id="step3">');
	h.push('<p><button class="pure-button pure-button-error" id="end-call">End call<\/button><\/p>');
	h.push('<p><button class="pure-button pure-button-success" id="start">Start<\/button><\/p>');
	h.push('<\/div><\/div>');
	document.getElementById('chatContents').innerHTML = h.join('');
	$("#menu").append('<button id="callOverlap" class="ui-button ui-widget ui-corner-all"' + 
			' style="float:right;" onClick="showChat()">' + username + '</button>');
	$("#chatContents").show();
	$("#contents").hide();
}

function rejectCallHtml(username) {
	var h = [''];
	h.push('<p>User ' + username + ' rejected your call.<\/p>');
	$("#dialog").dialog("close");
	document.getElementById('dialog').innerHTML = h.join('');
	$(document).ready(function() {
		$("#dialog").dialog({title: "Rejected call", closeOnEscape: true}).dialog("open");
	});
}
