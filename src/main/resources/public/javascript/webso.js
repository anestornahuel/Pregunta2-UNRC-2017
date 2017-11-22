var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/duelo");
webSocket.onmessage = function (msg) {
    message(msg); 
};

webSocket.onopen = function() {
    if (window.location.pathname == "/espera") {
        id("entrarButton").click();
    }
    if (window.location.pathname == "/jugando") {
        sendEsperando();
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

// Mensaje para entrar al conunto de usuarios esperando la respuesta del oponente
function sendEsperando() {
    var msg = {
        type : "Esperar",
    }
    var strng = JSON.stringify(msg)
    webSocket.send(strng); 
}

// Mensaje para desafiar en modo duelo a otro usuario
function sendDesafiar(opponent) {
    if (id("score").value !== "") {
        var msg = {
            type : "Desafiar",
            score : id("score").value,
            opponentname : opponent,
        }
        var strng = JSON.stringify(msg)
        webSocket.send(strng); 
    } else {
        id("score").value = "";
        id("estado").innerHTML = "Debes ingresar la cantidad de puntos a apostar";
    }
}

// Interpreta el mensaje
function message(msg) {
    var data = JSON.parse(msg.data);
    var type = data.type
    switch(type) {
        case "Actualizar":
            id("userlist").innerHTML = "";
            data.userlist.forEach(function (user) {
                insert("userlist", 
                "<li>" + 
                "<p>" + user + "</p>" +
                "<button onclick=\"sendDesafiar('" + user + "')\">Desafiar</button>" +
                "</li>");
            });
            id("duelist").innerHTML = "";
            data.duelist.forEach(function (user) {
                insert("duelist", 
                "<li>" + 
                "<p>" + user + "</p>" +
                "<form action=\"/jugando\" method=\"post\">" + 
                "<input id=\"submitDesafio\" type=\"submit\"" +
                "value=\"Jugar vs " + user + "\" name=\"duelo\"> </form> " +
                "</li>");
            });
            break; 
        case "Jugar":
            if (window.location.pathname == "/jugando") {
                id("continuarButton").click();
            }
            break;
        case "Error":
            id("estado").innerHTML = data.message;
            break;
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