function homeHtml(categories) {
	var h = [''];
	categories.forEach(function(category){
		h.push('<div class="tile">' + category.name + '<\/div>');
	});
	document.getElementById('contents').innerHTML = h.join('');
}

function cardsHtml(cards) {
	var h = [''];
	cards.forEach(function(card){
		h.push('<button onClick="getUserProfile(\'' + card.userNick + '\')">' + card.userNick + '<\/button>');
		h.push('<a><h2>' + card.title + '<\/h2><\/a>');
		h.push('<a>' + card.description + '<\/a>');
	});
	document.getElementById('contents').innerHTML = h.join('');
}

function aboutHtml(about) {
	var h = [''];
	h.push('<div>' + about + '<\/div>');
	document.getElementById('contents').innerHTML = h.join('');
}

function profileHtml(user) {
	var h = [''];
	h.push('<div><span id="username">' + user.username + '</span><\/div>');
	if(user.status){
		h.push('<div>Online<\/div>');
		h.push('<button id="call" class="pure-button pure-button-success">Call<\/button>');
	}else{
		h.push('<div>Offline<\/div>');
	}
	document.getElementById('contents').innerHTML = h.join('');
	$("#call").click(function(){
		call(user.username);
    });
}

function callingDialogHtml(callTo) {
	var h = [''];
	h.push('<div id="callingToDialog" title="Outcoming call" style="display:none;">');
	h.push('<p>Calling to ' + callTo + ' ...<\/p>');
	document.getElementById('dialog').innerHTML = h.join('');
	$("#callingToDialog").dialog();
}

function callDialogHtml(userCalling){
	var h = [''];
	h.push('<div id="callingDialog" title="Incoming call" style="display:none;">');
	h.push('<p>Calling from ' + userCalling + '<\/p>');
	h.push('<div><button id="answerCall" class="pure-button pure-button-success">Answer<\/button>');
	h.push('<button id="rejectCall" class="pure-button pure-button-error">Reject<\/button><\/dov>');
	h.push('<\/div>');
	document.getElementById('dialog').innerHTML = h.join('');
	$("#callingDialog").dialog();
	$("#answerCall").click(function(){
		window.location.href = '/BuyMyTime/cam/' + userCalling;
    });
	$("#rejectCall").click(function(){
		rejectCall(userCalling);
		$("#callingDialog").dialog('close');
    });
}

function rejectCallHtml(username) {
	var h = [''];
	h.push('<div id="rejectedCallDialog" title="Rejected call" style="display:none;">');
	h.push('<p>User ' + username + ' rejected your call.<\/p>');
	$("#callingToDialog").dialog('close');
	document.getElementById('dialog').innerHTML = h.join('');
	$("#rejectedCallDialog").dialog();
}
