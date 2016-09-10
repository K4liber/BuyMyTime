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

var peer;

function handleCall(call){
	console.log("handleCall script.js");
	var message = JSON.parse(call.body);
	callDialogHtml(message.name);
	subscribeStomp.subscribe('/user/queue/cancelCall', handleCancleCall);
}

function handleCancleCall(call){
	console.log("handleCancleCall script.js");
	$(document).ready(function() {
		$("#dialog").dialog({close: function(){ }}).dialog("close");
	});
}

function cancelCall(callTo){
	console.log("cancelCall script.js");
	$("#callingToDialog").dialog("close");
	var cancelMessage = {'name': callTo};
    var payload = JSON.stringify(cancelMessage);
    subscribeStomp.send("/BuyMyTime/cancelCall", {}, payload); 
}

function handleAcceptCall(acceptCall){
	console.log("handleAcceptCall script.js");
	var message = JSON.parse(acceptCall.body);
	if(message.accept)
		peerCall(message.callingTo);
	else{
		rejectCallHtml(message.callingTo);
	}
}

function handleChatMessage(message){
	console.log("handleChatMessage script.js");
	var message = JSON.parse(message.body);
	$("#messagesList").append('<div style="color:green;">' + message.messageContent + '</div>').scrollTop("0");
	$("#chatMessagesList").append('<div style="color:green;">' + message.messageContent + '</div>').scrollTop("0");
}

function handlePaidMessage(message){
	console.log("handlePaidMessage script.js");
	var message = JSON.parse(message.body);
	$("#price").text(message.price);
	$("#maxTime").text(message.maxTime);
	$("#dialog").dialog();
}

function handlePaidAnswer(message){
	console.log("handlePaidAnswer script.js");
	var message = JSON.parse(message.body);
	if(message.accept){
		console.log("Akceptacja");
	    startClock();
	}else
		console.log("Nie akceptacja");
}

function call(username){
	console.log("call script.js");
    var callMessage = {'name': username};
    var payload = JSON.stringify(callMessage);
    subscribeStomp.send("/BuyMyTime/call", {}, payload);  
    callingDialogHtml(username);
}

function answerCall(callingFrom){
	if(peer != undefined){
		peer.destroy();
		$("#callOverlap").remove();
	}
	navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia;
    navigator.getUserMedia({audio: true, video: true},
        function(stream){
            window.localStream = stream;
        },
        function(error) {
            console.log("navigator.getUserMedia error: ", error);
        }
    );
	console.log("answerCall script.js");
	var yourId = document.getElementById("userNick").innerText;
	peer = new Peer(yourId, {host: '192.168.1.19', port: 9000, path: '/BuyMyTime'});
	peer.on('open', function(){
      $('#my-id').text(peer.id);
      step1();
    });
    peer.on('call', function(call){
      call.answer(window.localStream);
      step3(call);
    });
    peer.on('error', function(err){
      alert(err.message);
      step2();
    });
	getUsername(function(callingTo) {
		var answerMessage = {'callingFrom': callingFrom, 'callingTo': callingTo, 'accept': true};
	    var payload = JSON.stringify(answerMessage);
	    setTimeout(function(){
	    	subscribeStomp.send("/BuyMyTime/answerCall", {}, payload);  
	    }, 2000);
	});
}

function peerCall(callingTo) {
	if(peer != undefined){
		peer.destroy();
		$("#callOverlap").remove();
	}
	navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia;
    navigator.getUserMedia({audio: true, video: true},
        function(stream){
            window.localStream = stream;
        },
        function(error) {
            console.log("navigator.getUserMedia error: ", error);
        }
    );
	$("#dialog").dialog({close: function(){ }}).dialog("close");
	chatContentHtml(callingTo);
	console.log("peerCall script.js");
	var yourId = document.getElementById("userNick").innerText;
	peer = new Peer(yourId, {host: '192.168.1.19', port: 9000, path: '/BuyMyTime'});
    peer.on('open', function(){
      $('#my-id').text(peer.id);
      setTimeout(function(){
          console.log(window.localStream);
          step3(peer.call(callingTo, window.localStream));
          step1();
      }, 2000);
    });
    peer.on('call', function(call){
      call.answer(window.localStream);
      step3(call);
    });
    peer.on('error', function(err){
      alert(err.message);
      step2();
    });
}

function getUsername(onSuccess) {
	console.log("getUsername script.js");
	$.ajax({
        type : "GET",
        url : "username",
        success: function(data){
        	onSuccess(data);
        }
    });
}

function rejectCallClick() {
	$("#dialog").dialog("close"); 
}

function cancelCallClick() {
	$("#dialog").dialog("close");
}

function rejectCall(rejected){
	console.log("rejectCall script.js");
	getUsername(function(username) {
		var answerMessage = {'callingFrom': rejected, 'callingTo': username, 'accept': false};
	    var payload = JSON.stringify(answerMessage);
	    setTimeout(function(){
	    	subscribeStomp.send("/BuyMyTime/answerCall", {}, payload);  
	    }, 2000);
	});
}

function startPaid(toId, fromId, price, maxTime){
	console.log("startPaid script.js");
	var answerMessage = {'toId': toId, 'fromId': fromId, 'price': price, 'maxTime': maxTime};
    var payload = JSON.stringify(answerMessage);
    setTimeout(function(){
    	subscribeStomp.send("/BuyMyTime/paidCall", {}, payload);  
    }, 1000);
}

function sendPaidAnswer(toId, fromId, price, maxTime, accept){
	console.log("sendPaidAnswer script.js");
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
        	if(data == "success")
        		successContactAddHtml(contactUsername);
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

function sendChatMessage(){
	var sendTo = document.getElementById("chatWith").innerText;
	var messageContent = $('#messageContent').val();
	var sendFrom = document.getElementById("userNick").innerText;
	$("#messagesList").append('<div>' + messageContent + '</div>').scrollTop("0");
	$("#chatMessagesList").append('<div>' + messageContent + '</div>').scrollTop("0");
	var chatMessage = {'sendFrom': sendFrom, 'sendTo': sendTo ,'messageContent': messageContent};
    var payload = JSON.stringify(chatMessage);
    subscribeStomp.send("/BuyMyTime/message", {}, payload);	
}

function sendMessage(){
	var sendTo = document.getElementById("contactUsername").innerText;
	var messageContent = $('#messageContent').val();
	var sendFrom = document.getElementById("userNick").innerText;
	$("#messagesList").append('<div>' + messageContent + '</div>').scrollTop("0");
	$("#chatMessagesList").append('<div>' + messageContent + '</div>').scrollTop("0");
	var chatMessage = {'sendFrom': sendFrom, 'sendTo': sendTo ,'messageContent': messageContent};
    var payload = JSON.stringify(chatMessage);
    subscribeStomp.send("/BuyMyTime/message", {}, payload);	
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

function step1 () {
    // Get audio/video stream
    navigator.getUserMedia({audio: true, video: true}, function(stream){
      // Set your video displays
      $('#my-video').prop('src', URL.createObjectURL(stream));
      window.localStream = stream;
      step2();
    }, function(){ $('#step1-error').show(); });
}

function step2 () {
	$('#step1').hide();
	$('#step2').show();
}

function step3 (call) {
    // Hang up on an existing call if present
    if (window.existingCall) {
      window.existingCall.close();
    }

    // Wait for stream on the call, then set peer video display
    call.on('stream', function(stream){
      $('#their-video').prop('src', URL.createObjectURL(stream));
    });

    // UI stuff
    window.existingCall = call;
    $('#their-id').text(call.peer);
    call.on('close', step2);
    $('#step1, #step2').hide();
    $('#step3').show();
}

function startClock() {
  	
	var startTime = new Date();
    var startHours = startTime.getHours();
	var startMinutes = startTime.getMinutes();
	var startSeconds = startTime.getSeconds();
	$(".clock").show();
      
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
		$(".clock").html(currentTimeString);
		
    },1000);
    
}

function showChat(){
	$("#chatContents").show();
	$("#contents").hide();
}