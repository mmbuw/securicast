let wbtCharacteristic = null;

var bluetoothDevice;
var wbtData;

function onWebBluetoothStart(btData) {
	bluetoothDevice = null;
	wbtData = null;
	console.log("Requesting Bluetooth Devices for two-factor authentication")
	navigator.bluetooth.requestDevice({
		filters: [{
			services: [0x1337]
		}]
		// acceptAllDevices: true
		})
	.then(device => {
		console.log("Trying to connect with " + device.name);
		bluetoothDevice = device;
		wbtData = btData;
		//console.log('wbtData: ' + wbtData);
		device.addEventListener("gattserverdisconnected", onDisconnected);
		connect();
	})
	.catch(error => {
		console.log(error);
		document.getElementById("wbtInfo").textContent="An error occured, please try again.";
		document.getElementById("wbtInfo").style.fontWeight = "bold";
	});
}

function connect() {
	logTime("connecting");
	document.getElementById("wbtInfo").textContent="Please wait until the second factor is verified.";
	document.getElementById("wbtInfo").style.fontWeight = "bold";
	$("#wbtInfo").textillate();
	
	return bluetoothDevice.gatt.connect()
	.then(server => {
		logTime("getting service");
		console.log("Getting WBT-Authentication Service");
		return server.getPrimaryService(0x1337)
	})
	.then(service => {
		logTime("getting characteristic");
		console.log("Getting Characteristic 0x2001");
		service.getCharacteristic(0x2001)
		.then(_ => {
			console.log("Start Testing Notifications");
			_.startNotifications();
			console.log("EventListener");
			_.addEventListener("characteristicvaluechanged", handleCVChanged);
			console.log("Started");
			wbtCharacteristic = _;
			console.log("Let's go!");
			writeToCharacteristic(wbtData);
		});
	})
	.catch(error => {
		console.log(error);
		document.getElementById("wbtInfo").textContent="An error occured, please try again.";
		document.getElementById("wbtInfo").style.fontWeight = "bold";
	});
}

function handleCVChanged(event) {
	logTime("handling response");
	var value = event.target.value;
	var arr = [];
	var str = null;
	for (i = 0; i < value.byteLength; i++) {
		arr.push(value.getInt8(i));
	}
	console.log("arr: " + arr);
	str = arr.join();
	document.getElementById("otpData").value = str;
	document.hiddenOtpData.submit();
}

function onDisconnected(data) {
	logTime("disconnected");
	console.log("disconnected");
	console.log("Trying to reconnect");
	connect();
}


function writeToCharacteristic(data) {
	logTime("writing characteristic");
	let data2 = unicodeStringToTypedArray(data);
	return wbtCharacteristic.writeValue(data2)
		.catch(err => console.log("Error when writing value to characteristic!", err));
}

// https://coolaj86.com/articles/unicode-string-to-a-utf-8-typed-array-buffer-in-javascript/
function unicodeStringToTypedArray(s) {
    var escstr = encodeURIComponent(s);
    var binstr = escstr.replace(/%([0-9A-F]{2})/g, function(match, p1) {
        return String.fromCharCode('0x' + p1);
    });
    var ua = new Uint8Array(binstr.length);
    Array.prototype.forEach.call(binstr, function (ch, i) {
        ua[i] = ch.charCodeAt(0);
    });
    return ua;
}