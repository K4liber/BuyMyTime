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
		h.push('<button onClick="getProfile(' + card.userNick + ')">' + card.userNick + '<\/button>');
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
	h.push('<div>' + user.username + '<\/div>');
	if(user.status){
		h.push('<div>Online<\/div>');
		h.push('<button onclick="call()" class="pure-button pure-button-success">Call<\/button>');
	}else{
		h.push('<div>Offline<\/div>');
	}
	document.getElementById('contents').innerHTML = h.join('');
}