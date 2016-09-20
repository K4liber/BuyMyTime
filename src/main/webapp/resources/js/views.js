function loadOverlapsHtml(usernames){
	usernames.forEach(function(username){
	$("#menu").append('<button class="pure-button-black" id="callOverlap' + username + 
			'" style="float:right;">' + username + '</button>');
	});
	$(document).ready(function(){
		usernames.forEach(function(username){
			$("#callOverlap" + username).click(function(){
				showChat(username);
			});
		});
	});
}

function communiqueDialogHtml(communique){
	var h = [''];
	h.push('<p>' + communique + '<\/p>');
	document.getElementById('dialog').innerHTML = h.join('');
	$(document).ready(function() {
		$("#dialog").dialog({title: "Communique", closeOnEscape: true });
		$("#dialog").dialog("open");
	});
}

function removeCallOverlapHtml(username){
	console.log("removing");
	$("#callOverlap" + username).remove();
}

function homeHtml(categories) {
	var h = [''];
	categories.forEach(function(category){
		h.push('<div class="tile"><button id="category' + category.id + '" class="tileButton">');
		h.push('<div>' + category.name + '<\/div>');
		h.push('<div>' + category.description + '<\/div>');
		h.push('</button></div>');
	});
	document.getElementById('contents').innerHTML = h.join('');
	$("#chatContents").hide();
	$("#contents").show();
	$(document).ready(function() {
		categories.forEach(function(category){
			$("#category" + category.id).click(function(){
				getCardsByCategory(category.name);
		    });
		});
	});
}

function editProfileHtml(userProfile) {
	var h = [''];
	h.push('<form action="" id="editProfile" method="post" enctype="multipart/form-data" >');
	h.push('<div>Username: <span id="username">' + userProfile.username + '</span></div>');
	h.push('<div>Email: <span id="email">' + userProfile.email + '</span></div>');
	h.push('<div>Profile photo: <input type="file" name="file" required id="upload"></div>');
	h.push('<div><img class="profileImage" src="/BuyMyTime/resources/img/' + userProfile.imageName + '"/></div>');
	h.push('<input type="submit" value="Load photo" />');
	document.getElementById('contents').innerHTML = h.join('');
	$("#chatContents").hide();
	$("#contents").show();
	$(document).ready(function() {
		$("#editProfile").submit(function(e){
		    e.preventDefault();
			var formdata = new FormData(this);
			postProfileImage(formdata);
		});
	});
}

function addNewCardHtml(){
	var h = [''];
	h.push('<form action="" id="addCard" method="post">');
	h.push('<label>Title</label>:');
	h.push('<input  type="text" id="cardTitle">');
	h.push('<label>Tags</label>:');
	h.push('<input  type="text" id="tags">');
	h.push('<label>Category</label>:');
	h.push('<input  type="text" id="categoryName">');
	h.push('<label>Description</label>:');
	h.push('<textarea cols="50" rows="5" id="description"></textarea>');
	h.push('<input type="submit" value="Add card" />');
	h.push('</form>');
	document.getElementById('contents').innerHTML = h.join('');
	$("#chatContents").hide();
	$("#contents").show();
	$(document).ready(function() {
		$("#addCard").submit(function(e){
			e.preventDefault();
			var cardData = {};
			cardData["title"] = $("#cardTitle").val();
			cardData["categoryName"] = $("#categoryName").val();
			cardData["description"] = $("#description").val();
			var cardModelData = JSON.stringify( {card: cardData, tags:  $("#tags").val()} );
			postCardModel(cardModelData);
		});
	});
}

function messagesHtml(contacts) {
	var h = [''];
	h.push('<div class="contactsPanel">');
	h.push('<div class="contactsLeftPanel">');
	contacts.forEach(function(contactInfo){
		h.push('<div class="contactPanel">');
		if(contactInfo.status){
			if(!contactInfo.open)
				h.push('<span class="contactSpan" style="color:white;">'+ contactInfo.username + '<\/span> Online');
			else
				h.push('<span class="contactSpan" >'+ contactInfo.username + '<\/span> Online');
		}
		else{
			if(!contactInfo.open)
				h.push('<span class="contactSpan" style="color:green;">'+ contactInfo.username + '<\/span>');
			else
				h.push('<span class="contactSpan">'+ contactInfo.username + '<\/span>');
		}
		h.push('<\/div>');
	});
	h.push('<\/div>');
	h.push('<div class="contactsRightPanel" id="contactsRightPanel"><\/div>');
	h.push('<\/div>');
	document.getElementById('contents').innerHTML = h.join('');
	$("#chatContents").hide();
	$("#contents").show();
	$(document).ready(function() {
		$(".contactSpan").click(function(){
			getContact(this.innerHTML);
			this.style.color = "black"; 
	    });
	});
}

function messagesOverlapHtml(){
	getMessagesCount(function(count){
		var h =[''];
		if (count != 0){
			h.push(count);
			document.getElementById('newMessages').innerHtml = h.join('');
		} else {
			$("#newMessages").remove();
		}
	});
}

function contactHtml(userContact) {
	var h = [''];
	h.push('<div><h2><span id="contactUsername">' + userContact.profile.username +'<\/span><\/h2><\/div>');
	h.push('<div class="messagesContent">');
	h.push('<div id="messagesList' + userContact.profile.username  + '">');
	userContact.chatMessages.forEach(function(chatMessage){
		if (chatMessage.sendTo == userContact.profile.username){
			if (chatMessage.type == "file"){
				h.push('<div style="color:blue;" class="chatMessage"><span id="messageFile' + chatMessage.id 
						+ '" class="messageFile">' + chatMessage.messageContent + '</span></div>');
			} else 
				h.push('<div style="color:green;">' + chatMessage.messageContent + '<\/div>');
		} else {
			if (chatMessage.type == "file"){
				h.push('<div style="color:blue;" class="chatMessage"><span id="messageFile' + chatMessage.id 
						+ '" class="messageFile">' + chatMessage.messageContent + '</span></div>');
			} else
				h.push('<div>' + chatMessage.messageContent + '<\/div>');
		}
	});
	h.push('<\/div>');
	h.push('<\/div>');
	h.push('<textarea rows="2" cols="30" id="messageContent"><\/textarea>');
	h.push('<p><button class="pure-button" id="contactSendButton">Send<\/button><\/p>');
	h.push('<form id="sendFileContact" method="post" enctype="multipart/form-data" >');
	h.push('<input name="username" type="text" value="' + userContact.profile.username + '" style="display:none;" readonly>');
	h.push('<input type="file" name="file" required id="upload">');
	h.push('<input type="submit" value="Send file" /><\/p>');
	h.push('</form>');
	document.getElementById('contactsRightPanel').innerHTML = h.join('');
	$(document).ready(function() {
		userContact.chatMessages.forEach(function(chatMessage){
			$("#messageFile" + chatMessage.id).click(function(){
				getFileName(function(fileName) {
					downloadURI("resources/files/" + fileName, chatMessage.messageContent);
				}, chatMessage.id);
			});
		});
		$("#contactSendButton").click(function(){
			sendMessage(userContact.profile.username);
			$("textarea#messageContent").val("");
	    	$("textarea#messageContent").focus();
	    });
		$("#sendFileContact").submit(function(e){
		    e.preventDefault();
			var formdata = new FormData(this);
			postFileMessage(formdata, function(fileName){
			    $("#messagesList" + userContact.profile.username)
				.append('<div class="chatMessage"><span onClick="downloadFile(\'' 
						+ fileName + '\', \'' + $('#upload').val() + '\')" id="' 
						+ $('#upload').val() + '" class="messageFile">' 
						+ $('#upload').val() + '</span></div>');
			});
		});
	});
}

function cardsHtml(cards) {
	var h = [''];
	h.push('<div class="categoryLeftPanel">');
	cards.forEach(function(card){
		h.push('<div id="card' + card.id + '" class="card"><a><h2>' + card.title + '<\/h2><\/a>');
		h.push('<img class="profileImage" src="/BuyMyTime/resources/img/' + card.authorImageName + '"/>');
		h.push('<button onClick="getUserProfile(\'' + card.userNick + '\')">' + card.userNick + '<\/button>');
		h.push('<a>' + card.description + '<\/a><\/div>');
	});
	h.push('<\/div>');
	h.push('<div id="categoryRightPanel" class="categoryRightPanel">');
	h.push('<\/div>');
	document.getElementById('contents').innerHTML = h.join('');
	$("#chatContents").hide();
	$("#contents").show();
	$(document).ready(function() {
		cards.forEach(function(card){
			$("#card" + card.id).click(function(){
				categoryProfile(card.userNick);
		    });
		});
	});
}

function categoryProfileHtml(userProfile){
	var h = [''];
	h.push('<div><span id="username">' + userProfile.username + '</span><\/div>');
	h.push('<div><img class="profileImage" src="/BuyMyTime/resources/img/' + userProfile.imageName + '"/><\/div>');
	if(userProfile.status){
		h.push('<div>Online<\/div>');
	}else{
		h.push('<div>Offline<\/div>');
	}
	if(userProfile.username != document.getElementById('userNick').innerHTML){
		h.push('<button id="call" class="pure-button pure-button-success">Call<\/button>');
		h.push('<button id="sendMessageCards" class="pure-button pure-button-warning">Send message<\/button>');
	}else{
		h.push('<button id="edit" class="pure-button">Edit profile<\/button>');
		h.push('<button id="addCard" class="pure-button">Add card<\/button>');
	}
	document.getElementById('categoryRightPanel').innerHTML = h.join('');
	$(document).ready(function() {
		if(userProfile.username != document.getElementById('userNick').innerHTML){
			$("#call").click(function(){
				call(userProfile.username);
		    });
			$("#sendMessageCards").click(function(){
				getMessages();
				getContact(userProfile.username);
		    });
		}else{
			$("#edit").click(function(){
				editProfile(userProfile);
		    });
			$("#addCard").click(function(){
				addCard();
		    });
		}
	});
}

function aboutHtml(about) {
	var h = [''];
	h.push('<div>' + about + '<\/div>');
	document.getElementById('contents').innerHTML = h.join('');
	$("#chatContents").hide();
	$("#contents").show();
}

function profileHtml(userProfile) {
	var h = [''];
	h.push('<div><span id="username">' + userProfile.username + '</span><\/div>');
	h.push('<div><img class="profileImage" src="/BuyMyTime/resources/img/' + userProfile.imageName + '"/><\/div>');
	if(userProfile.status){
		h.push('<div>Online<\/div>');
	}else{
		h.push('<div>Offline<\/div>');
	}
	if(userProfile.username != document.getElementById('userNick').innerHTML){
		h.push('<button id="call" class="pure-button pure-button-success">Call<\/button>');
		h.push('<button id="sendMessageProfile" class="pure-button pure-button-warning">Send message<\/button>');
	}else{
		h.push('<button id="edit" class="pure-button">Edit profile<\/button>');
		h.push('<button id="addCard" class="pure-button">Add card<\/button>');
	}
	document.getElementById('contents').innerHTML = h.join('');
	$(document).ready(function() {
		if(userProfile.username != document.getElementById('userNick').innerHTML){
			$("#call").click(function(){
				call(userProfile.username);
		    });
			$("#sendMessageProfile").click(function(){
				getMessages();
				getContact(userProfile.username);
		    });
		}else{
			$("#edit").click(function(){
				editProfile(userProfile);
		    });
			$("#addCard").click(function(){
				addCard();
		    });
		}
	});
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

function successContactDialogAddHtml(contactUsername){
	console.log("successContactAddHtml views.js");
	var h = [''];
	h.push('<p>User ' + contactUsername + ' added to Contacts.<\/p>');
	document.getElementById('dialog').innerHTML = h.join('');
	$(document).ready(function() {
		$("#dialog").dialog({title: "Adding successful", closeOnEscape: true });
		$("#dialog").dialog("open");
	});
}

function existContactDialogHtml(contactUsername){
	var h = [''];
	h.push('<p>User ' + contactUsername + ' is your contact actually.<\/p>');
	document.getElementById('dialog').innerHTML = h.join('');
	$(document).ready(function() {
		$("#dialog").dialog({title: "Contact already exist", closeOnEscape: true });
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
	h.push('<div class="left-panel"><h2>Chat with <span id="chatWith">' + username + '</span><\/h2>');
	h.push('<video id="their-video" autoplay><\/video><\/div>');
	h.push('<div class="right-panel">');
	h.push('<video id="my-video" autoplay="true" muted="true"><\/video>');
	h.push('<div id="clock" style="display:none;"><\/div>');
	h.push('<button class="pure-button pure-button-success" id="endPaid" style="display:none;">End paid chat<\/button>');
	h.push('<div class="chatContent">');
	h.push('<div id="chatMessagesList' + username + '" class="chatMessagesList" ><\/div><\/div>');
	h.push('<textarea rows="2" cols="30" id="chatMessageContent"><\/textarea>');
	h.push('<p><button class="pure-button" id="chatSendButton">Send<\/button>');
	h.push('<form id="sendFile" method="post" enctype="multipart/form-data" >');
	h.push('<input name="username" type="text" value="' + username + '" style="display:none;" readonly>');
	h.push('<input type="file" name="file" required id="upload">');
	h.push('<input type="submit" value="Send file" /><\/p>');
	h.push('</form>');
	h.push('<div id="step3">');
	h.push('<p><button class="pure-button pure-button-error" id="endCall">End call<\/button><\/p>');
	h.push('<p><button class="pure-button pure-button-success" id="startPaying">Paying conversation<\/button><\/p>');
	h.push('<\/div>');
	h.push('<\/div>');
	document.getElementById('chatContents').innerHTML = h.join('');
	$("#menu").append('<button id="callOverlap' + username + '" class="ui-button ui-widget ui-corner-all"' + 
			' style="float:right;" onClick="showChat()">' + username + '</button>');
	$("#chatContents").show();
	$("#contents").hide();
	$(document).ready(function() {
		$("#endCall").click(function() {
			confirmEndCallDialogHtml(document.getElementById('chatWith').innerHTML);
		});
		$("#chatSendButton").click(function() {
			sendChatMessage(document.getElementById('chatWith').innerHTML,
					$('#chatMessageContent').val());
		});
		$("#startPaying").click(function() {
			payingCallDialogHtml(document.getElementById('chatWith').innerHTML);
		});
		$("#endPaid").click(function() {
			endPaidCall();
			sendEndPaidMessage(document.getElementById('chatWith').innerHTML);
		});
		$("#sendFile").submit(function(e){
		    e.preventDefault();
			var formdata = new FormData(this);
			postFileMessage(formdata, function(fileName){
			    $("#chatMessagesList" + username)
				.append('<div class="chatMessage"><span onClick="downloadFile(\'' 
						+ fileName + '\', \'' + $('#upload').val() + '\')" id="' 
						+ $('#upload').val() + '" class="messageFile">' 
						+ $('#upload').val() + '</span></div>');
			    $("#messagesList" + username)
				.append('<div class="chatMessage"><span onClick="downloadFile(\'' 
						+ fileName + '\', \'' + $('#upload').val() + '\')" id="' 
						+ $('#upload').val() + '" class="messageFile">' 
						+ $('#upload').val() + '</span></div>');
			});
		});
	});
}

function confirmEndCallDialogHtml(callWith) {
	var h = [''];
	h.push('<p>Are you sure to end conversation with ' + callWith + '?<\/p>');
	h.push('<div><button id="confirmEndCall" class="pure-button pure-button-success">End Call<\/button>');
	h.push('<button id="cancelEnding"' 
			+ 'class="pure-button pure-button-error">Cancel<\/button><\/div>');
	document.getElementById('dialog').innerHTML = h.join('');
	$(document).ready(function() {
		$("#dialog").dialog({title: "Confirm the end of conversation", closeOnEscape: true}).dialog("open");
		$("#confirmEndCall").click(function(){
			$("#dialog").dialog("close");
			sendEndCallMessage(callWith);
			endCall(callWith);
	    });
		$("#cancelEnding").click(function(){
			$("#dialog").dialog("close");
	    });
	});
}

function payingCallDialogHtml(callWith) {
	var h = [''];
	h.push('<p>Start a chat at the ' + callWith + ' expense.<\/p>');
	h.push('<label>Your price: <\/label>');
	h.push('<input id="price" type="number" min="1"><\/input>$/h');
	h.push('<label>Max time: <\/label>');
	h.push('<input id="maxTime" type="number" min="1"><\/input>min');
	h.push('<div><button id="startPayingChat" class="pure-button pure-button-success">Send<\/button>');
	h.push('<button id="cancelPayingChat" class="pure-button pure-button-error">Cancel<\/button><\/div>');
	document.getElementById('dialog').innerHTML = h.join('');
	$(document).ready(function() {
		$("#dialog").dialog({title: "Lets earn some money" , closeOnEscape: true}).dialog("open");
		$("#startPayingChat").click( function (){
			if ( document.getElementById('price').value != null && document.getElementById('maxTime').value != null ){
				startPaid(callWith, document.getElementById('userNick').innerHTML,
						document.getElementById('price').value, document.getElementById('maxTime').value);
				$("#dialog").dialog("close");
			} else {
				$("#dialog").append('<div>Your values cannot be empty.</div>');
			}
	    });
		$("#cancelPayingChat").click(function(){
			$("#dialog").dialog("close");
	    });
	});
}

function payingCallMessageDialogHtml(message){
	var h = [''];
	h.push('<p>Request for a chat at your expense with user ' + message.fromId + '.<\/p>');
	h.push('<label>Price: ' + message.price + '<\/label>');
	h.push('<label>Max time: ' + message.maxTime + '<\/label>');
	h.push('<div><button id="agreePayingChat" class="pure-button pure-button-success">Agree<\/button>');
	h.push('<button id="disagreePayingChat" class="pure-button pure-button-error">Disagree<\/button><\/div>');
	document.getElementById('dialog').innerHTML = h.join('');
	$(document).ready(function() {
		$("#dialog").dialog({title: "Chat" , closeOnEscape: true, close: function(){ 
			sendPaidAnswer(message.toId, message.fromId, message.price, message.maxTime, false); }}).dialog("open");
		$("#disagreePayingChat").click( function (){
			$("#dialog").dialog("close");
	    });
		$("#agreePayingChat").click(function(){
			sendPaidAnswer(message.toId, message.fromId, message.price, message.maxTime, true);
			$("#dialog").dialog({close: function(){ }}).dialog("close");
	    });
	});
	
}

function endCallDialogHtml(endCallMessage) {
	var h = [''];
	h.push('<p>Conversation with ' + endCallMessage.callWith + ' is ended.<\/p>');
	if(endCallMessage.communique != null)
		h.push('<p>' + endCallMessage.communique + '<\/p>');
	document.getElementById('dialog').innerHTML = h.join('');
	$(document).ready(function() {
		$("#dialog").dialog({title: "End of conversation", closeOnEscape: true}).dialog("open");
	});
}

function acceptPaidAnswerDialogHtml(message){
	var h = [''];
	h.push('<p>Receiver:  ' + message.receiver + '<\/p>');
	h.push('<p>Paying:  ' + message.paying + '<\/p>');
	h.push('<p>Price:  ' + message.price + '<\/p>');
	h.push('<p>Max time:  ' + message.maxTime + '<\/p>');
	h.push('<div><button id="ok" class="pure-button pure-button-success">OK<\/button><\/div>');
	document.getElementById('dialog').innerHTML = h.join('');
	$(document).ready(function() {
		$("#dialog").dialog({title: "Terms of chat" , closeOnEscape: true }).dialog("open");
		$("#ok").click( function (){
			$("#dialog").dialog("close");
	    });
	});
}

function declinePaidAnswerDialogHtml(message){
	var h = [''];
	h.push('<p>User ' + message.receiver + ' decline the terms of chat.<\/p>');
	h.push('<div><button id="ok" class="pure-button pure-button-success">OK<\/button><\/div>');
	document.getElementById('dialog').innerHTML = h.join('');
	$(document).ready(function() {
		$("#dialog").dialog({title: "Rejection" , closeOnEscape: true }).dialog("open");
		$("#ok").click( function (){
			$("#dialog").dialog("close");
	    });
	});
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

function startPaidHtml() {
  	
	var startTime = new Date();
	$("#clock").innerHTML = "";
	$("#clock").show();
	$("#endPaid").show();
      
    setInterval(function(){
    	
		var currentTime = new Date().getTime() - startTime.getTime();
		var timer = new Date();
		timer.setTime(currentTime);
		var hours = timer.getHours() - 1;
		var minutes = timer.getMinutes();
		var seconds = timer.getSeconds();
		
		// Add leading zeros
		minutes = (minutes < 10 ? "0" : "") + minutes;
		seconds = (seconds < 10 ? "0" : "") + seconds;
		hours = (hours < 10 ? "0" : "") + hours;
		
		// Compose the string for display
		var currentTimeString = hours + ":" + minutes + ":" + seconds;
		$("#clock").html(currentTimeString);
		
    },1000);
    
}
