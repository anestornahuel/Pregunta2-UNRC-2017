var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/duelo");
webSocket.onmessage = function (msg) { message(msg); };
webSocket.onopen = function() {
    if (window.location.pathname == "/espera") {
        id("entrarButton").click();
    }
}

// Mensaje para entrar a la lista de usuarios en modo duelo
function sendEntrar(username) {
    var msg = {
        type : "Entrar",
        sendername : username
    }
    var strng = JSON.stringify(msg)
    webSocket.send(strng); 
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}