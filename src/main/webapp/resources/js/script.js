var subscribeSocketJS;
var subscribeStomp;
var peer;
var checkingConnection;
var timeUpdate;

//If Logged in
getUsername(function(success){
	login();
});

function loadStomp(){
	subscribeSocketJS = new SockJS('http://localhost:8080/BuyMyTime/greeting');	
	subscribeStomp = Stomp.over(subscribeSocketJS);
	subscribeStomp.connect('guest', 'guest', function(frame){
     	subscribeStomp.subscribe('/user/queue/call', handleCall);
     	subscribeStomp.subscribe('/user/queue/callAnswer', handleCallAnswer);
     	subscribeStomp.subscribe('/user/queue/endCall', handleCallEnd);
     	subscribeStomp.subscribe('/user/queue/chat', handleChatMessage);
     	subscribeStomp.subscribe('/user/queue/paid', handlePaidMessage);
     	subscribeStomp.subscribe('/user/queue/paidAnswer', handlePaidAnswer);
     	subscribeStomp.subscribe('/user/queue/communique', handleCommunique);
    });
}

function login(){
	loadStomp();
}

function handleCommunique(communiqueMessage){
	console.log("handleCOmmunique");
	var message = JSON.parse(communiqueMessage.body);
	communiqueDialogHtml(message.communique);
	if(message.action != null)
		makeAction(message.action);
}

function makeAction(action){
	if (action == "endPaid"){
		endPaidCall();
	}
}

function sendEndPaidMessage(callWith){
	var endCallMessage = {'callWith': callWith};
    var payload = JSON.stringify(endCallMessage);
    subscribeStomp.send("/BuyMyTime/endPaid", {}, payload); 
}

function endPaidCall(){
	$("#clock").remove();
	$("#endPaid").remove();
	clearInterval(timeUpdate);
}

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

function handleCallEnd(endCallMessage){
	console.log("handleCallEnd script.js");
	var endCallMessageBody = JSON.parse(endCallMessage.body);
	endCallDialogHtml(endCallMessageBody);
	endCall(endCallMessageBody.callWith);
}

function cancelCall(callTo){
	console.log("cancelCall script.js");
	$("#callingToDialog").dialog("close");
	var cancelMessage = {'name': callTo};
    var payload = JSON.stringify(cancelMessage);
    subscribeStomp.send("/BuyMyTime/cancelCall", {}, payload); 
}

function handleCallAnswer(callAnswer){
	console.log("handleCallAnswer script.js");
	var message = JSON.parse(callAnswer.body);
	if(message.accept)
		peerCall(message.callingTo);
	else{
		rejectCallHtml(message.callingTo);
	}
}

function handleChatMessage(chatMessage){
	console.log("handleChatMessage script.js");
	var message = JSON.parse(chatMessage.body);
	$("#messagesList").append('<div style="color:green;">' + message.messageContent + '</div>');
	if(chatMessage.type == "file"){
		$("#chatMessagesList")
			.append('<div style="color:green;"><span id="messageFile" class="messageFile">' 
					+ message.messageContent + '</span></div>');
		$(document).ready(function(){
			$("#messageFile").click(function(){
				getFileName(function(fileName) {
					downloadURI("resources/files/" + fileName, message.messageContent);
				}, message.id);
			});
		});
	} else {
		$("#messagesList").append('<div style="color:green;">' + message.messageContent + '</div>');
		$("#chatMessagesList")
		.append('<div style="color:green;">' + message.messageContent + '</div>');
	}
}

function downloadURI(uri, name) {
	  var link = document.createElement("a");
	  link.download = name;
	  link.href = uri;
	  document.body.appendChild(link);
	  link.click();
	  document.body.removeChild(link);
	  delete link;
	}

function handlePaidMessage(paidMessage){
	var message = JSON.parse(paidMessage.body);
	payingCallMessageDialogHtml(message);
}

function handlePaidAnswer(message){
	var messageBody = JSON.parse(message.body);
	if(messageBody.accept){
		acceptPaidAnswerDialogHtml(messageBody);
	    startPaidHtml();
	    startSendingTimeUpdate();
	} else {
		declinePaidAnswerDialogHtml(messageBody);
	}
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
      dataConnection = peer.connect(call.peer, {});
      callStatus = true;
      startCheckingConnection(call.peer);
      step3(call);
    });
    peer.on('error', function(err){
    	if(err.type == "peer-unavailable"){
    		handlePeerUnavailableError(callingFrom);
		}else{
		    alert(err.message);
		    step2();
		    console.log(err.type);
		}
    });
    
	getUsername(function(callingTo) {
		var answerMessage = {'callingFrom': callingFrom, 'callingTo': callingTo, 'accept': true};
	    var payload = JSON.stringify(answerMessage);
	    setTimeout(function(){
	    	subscribeStomp.send("/BuyMyTime/callAnswer", {}, payload);  
	    }, 1000);
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
          startCheckingConnection(callingTo);
      }, 1000);
    });
    peer.on('call', function(call){
      call.answer(window.localStream);
      step3(call);
    });
    peer.on('error', function(err){
    	if(err.type == "peer-unavailable"){
    		handlePeerUnavailableError(callingTo);
		}else{
		    alert(err.message);
		    step2();
		    console.log(err.type);
		}
    });
    
}

function handlePeerUnavailableError(username){
	endCall(username);
	var endCallMessage = {
			'callWith': username,
			'communique': "peer unavailable error"
	};
	endCallDialogHtml(endCallMessage);
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
	    	subscribeStomp.send("/BuyMyTime/callAnswer", {}, payload);  
	    }, 2000);
	});
}

function startPaid(toId, fromId, price, maxTime){
	console.log("startPaid script.js");
	var answerMessage = {'toId': toId, 'fromId': fromId, 'price': price, 'maxTime': maxTime};
    var payload = JSON.stringify(answerMessage);
    subscribeStomp.send("/BuyMyTime/paidCall", {}, payload);  
}

function sendPaidAnswer(toId, fromId, price, maxTime, accept){
	var answerMessage = {'paying': toId, 'receiver': fromId, 'price': price, 'maxTime': maxTime, 'accept': accept};
    var payload = JSON.stringify(answerMessage);
    subscribeStomp.send("/BuyMyTime/paidCallAnswer", {}, payload);
}

function editProfile(userProfile){
	editProfileHtml(userProfile);
}

function sendChatMessage(sendTo, messageContent){
	console.log(sendTo);
	var sendFrom = document.getElementById("userNick").innerText;
	$("#messagesList").append('<div>' + messageContent + '</div>').scrollTop("-1000");
	$("#chatMessagesList").append('<div>' + messageContent + '</div>').scrollTop("-1000");
	var chatMessage = {'sendFrom': sendFrom, 'sendTo': sendTo ,'messageContent': messageContent};
    var payload = JSON.stringify(chatMessage);
    subscribeStomp.send("/BuyMyTime/message", {}, payload);	
}

function sendMessage(sendTo){
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
  	  var messageContent = $('#messageContent').val();
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
    	$("#chatSendButton").click();
    	$("textarea#chatMessageContent").val("");
    	$("textarea#chatMessageContent").focus();
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

function startCheckingConnection(username) {
	checkingConnection = setInterval(function(){
		peer.connect(username, {});
	}, 2000);
}

function startSendingTimeUpdate() {
	console.log("TU WESZLO");
	var timeUpdateMessage = {'username': "nobody"};
	var payload = JSON.stringify(timeUpdateMessage);
	timeUpdate = setInterval(function(){
		subscribeStomp.send("/BuyMyTime/timeUpdate", {}, payload);
		console.log("TRALALAL");
	}, 15000);
}



function showChat(username){
	console.log(username);
	$("#chatContents").show();
	$("#contents").hide();
}

function showContents(){
	$("#chatContents").hide();
	$("#contents").show();
}

function endCall(callWith) {
	clearInterval(timeUpdate);
	clearInterval(checkingConnection);
	removeCallOverlapHtml(callWith);
	getEndCall(callWith);
	showContents();
	if(peer != null)
		peer.destroy();
	if(window.localStream != undefined){
		window.localStream.getVideoTracks()[0].stop();
	}
}

function sendEndCallMessage(callWith){
	var endCallMessage = {'callWith': callWith};
    var payload = JSON.stringify(endCallMessage);
    subscribeStomp.send("/BuyMyTime/endCall", {}, payload); 
}

function addCard(){
	addNewCardHtml();
}
