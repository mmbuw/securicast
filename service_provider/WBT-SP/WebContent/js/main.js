var started = false;
var lastAction = "none";

$("#username").bind("input", function() {
	logTime("user credentials");
});

$("#password").bind("input", function() {
	logTime("user credentials");
});

$("#totp").bind("input", function() {
	logTime("totp");
});

function logTime(action) {
	if (lastAction == "none" || lastAction != action) {
		lastAction = action;
		log(action);
	}
}

function log(action) {
	var currentdate = new Date();
	var log = action + ";" + currentdate.getHours() + 
	";" + currentdate.getMinutes() + ";" + currentdate.getSeconds() + 
	";" + currentdate.getMilliseconds();
	console.log(log);
	$.ajax({
		dataType: "text",
		url: "LoggingServlet",
		type: "POST",
		data: log
	})
}