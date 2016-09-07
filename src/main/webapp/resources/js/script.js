var subscribeSocketJS;
var subscribeStomp;
if(subscribeSocketJS === undefined)
	subscribeSocketJS = new SockJS('http://localhost:8080/BuyMyTime/greeting');	
if(subscribeStomp === undefined){
	subscribeStomp = Stomp.over(subscribeSocketJS);
	subscribeStomp.connect('guest', 'guest', function(frame){
    	console.log("Connected to subsriber.");
     	subscribeStomp.subscribe('/user/queue/call', handleCall);
     	subscribeStomp.subscribe('/user/queue/acceptCall', handleAcceptCall);
     	subscribeStomp.subscribe('/user/queue/chat', handleChatMessage);
     	subscribeStomp.subscribe('/user/queue/paid', handlePaidMessage);
     	subscribeStomp.subscribe('/user/queue/paidAnswer', handlePaidAnswer);
    });
}

function handleCall(call){
	var message = JSON.parse(call.body);
	console.log('Subsribing message: ', message);
	if(confirm("Calling from " + message.name + "! Would you like to answer?")){
		console.log("Nawiazuje polaczenie!");
		window.location.href = '/BuyMyTime/cam/' + message.name;
	}else{
		console.log("Nie nawiazuje!");
	};
}

function handleAcceptCall(acceptCall){
	var message = JSON.parse(acceptCall.body);
	console.log('Accepted connection with id: ', message.id);
	window.location.href = '/BuyMyTime/video/' + message.id;
}

function handleChatMessage(message){
	var message = JSON.parse(message.body);
	$("#messagesList").append('<li style="color:green;">' + message.messageContent + '</li>');
}

function handlePaidMessage(message){
	var message = JSON.parse(message.body);
	$("#price").text(message.price);
	$("#maxTime").text(message.maxTime);
	$("#dialog").dialog();
}

function handlePaidAnswer(message){
	var message = JSON.parse(message.body);
	if(message.accept){
		console.log("Akceptacja");
	    startClock();
	}else
		console.log("Nie akceptacja");
}

function call(){
	var nick = document.getElementById("username").innerText;
    var callMessage = {'name': nick};
    var payload = JSON.stringify(callMessage);
    subscribeStomp.send("/BuyMyTime/call", {}, payload);      
}

function answerTheCall(username, id){
	var answerMessage = {'username': username, 'id': id};
    var payload = JSON.stringify(answerMessage);
    setTimeout(function(){
    	subscribeStomp.send("/BuyMyTime/answerCall", {}, payload);  
    }, 2000);
}

function startPaid(toId, fromId, price, maxTime){
	var answerMessage = {'toId': toId, 'fromId': fromId, 'price': price, 'maxTime': maxTime};
    var payload = JSON.stringify(answerMessage);
    setTimeout(function(){
    	subscribeStomp.send("/BuyMyTime/paidCall", {}, payload);  
    }, 1000);
}

function sendPaidAnswer(toId, fromId, price, maxTime, accept){
	var answerMessage = {'toId': toId, 'fromId': fromId, 'price': price, 'maxTime': maxTime, 'accept': accept};
    var payload = JSON.stringify(answerMessage);
    setTimeout(function(){
    	subscribeStomp.send("/BuyMyTime/paidCallAnswer", {}, payload);  
    }, 500);
}

function getLogin() {
	$("#loginDialog").dialog();
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
        	console.log(data);
        }
    });
}

function getLogout() {
	$.ajax({
        type : "GET",
        url : "logout",
        success: function(data){
        	window.location.replace("");
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

$(function(){
	$('#send').click(function(){
  	  var messageContent = $('#messageContent').val()
  	  var sendFrom = document.getElementById("yourid").innerText;
  	  var sendTo = document.getElementById("id").innerText;
  	  $("#messagesList").append('<li>' + messageContent + '</li>');
  	  var chatMessage = {'sendFrom': sendFrom, 'sendTo': sendTo ,'messageContent': messageContent};
      var payload = JSON.stringify(chatMessage);
      subscribeStomp.send("/BuyMyTime/message", {}, payload);
    });
    
    $("#registerButton").click(function(){
    	$("#loginDialog").dialog('close');
    	$("#registerDialog").dialog();
    });
    
});

$(document).keypress(function(e) {
    if(e.which == 13) {
    	e.preventDefault();
    	$("#send").click();
    	$("textarea#messageContent").val("");
    	$("textarea#messageContent").focus();
    }
});
