function homeHtml(categories) {
	var h = [''];
	categories.forEach(function(category){
		h.push('<div class="tile">' + category.name + '<\/div>');
	});
	document.getElementById('contents').innerHTML = h.join('');
}

function aboutHtml(about) {
	var h = [''];
	h.push('<div>' + about + '<\/div>');
	document.getElementById('contents').innerHTML = h.join('');
}