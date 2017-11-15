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

// Mensaje para desafiar en modo duelo a otro usuario
function sendDesafiar(opponent) {
    var msg = {
        type : "Desafiar",
        opponentname : opponent
    }
    var strng = JSON.stringify(msg)
    webSocket.send(strng); 
}

// Interpreta el mensaje
function message(msg) {
    var data = JSON.parse(msg.data);
    var type = data.type
    if (type == "Actualizar") {
        id("userlist").innerHTML = "";
        data.userlist.forEach(function (user) {
            insert("userlist", 
            "<li>" + 
            user + 
            "<button onclick=\"sendDesafiar('" + user + "')\">Desafiar</button>" +
            "</li>");
        });
        id("duelist").innerHTML = "";
        data.duelist.forEach(function (user) {
            insert("duelist", 
            "<li>" + 
            user +  
            "<button onclick=\"sendJugar('" + user + "')\">Jugar</button>" +
            "</li>");
        });
    } 
}

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}