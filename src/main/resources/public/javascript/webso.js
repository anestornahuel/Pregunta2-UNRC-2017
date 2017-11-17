var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/duelo");
webSocket.onmessage = function (msg) { 
    console.log(msg);
    message(msg); 
};

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

// Mensaje para empezar a jugar en modo duelo
function sendJugar(opponent) {
    if (id("score").value != "") {
        var msg = {
            type : "Jugar",
            score : id("score").value,
            opponentname : opponent
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
                "<button onclick=\"sendAbandonar('" + user + "')\">Abandonar</button>" +
                "</li>");
            });
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