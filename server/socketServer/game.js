var util = require("util"),
    io = require("socket.io"),
    Player = require("./Player").Player;

var socket,
    players;

var PORT = 1337;

function init() {
    players = [];
    socket = io.listen(PORT);
    setEventHandlers();
}

var setEventHandlers = function() {
    socket.sockets.on("connection", onSocketConnection);
};

function onSocketConnection(client) {
    util.log("New player has connected: " + client.id);
    client.on("disconnect", onClientDisconnect);
    client.on("new player", onNewPlayer);
}

function onClientDisconnect() {
    util.log("Player has disconnected: " + this.id);
    var removePlayer = playerById(this.id);
    if (!removePlayer) {
        util.log("Player not found: " + this.id);
        return;
    }
    players.splice(players.indexOf(removePlayer), 1);
    this.broadcast.emit("remove player", {id: this.id});
}

function onNewPlayer(data) {
    console.log(data);
    var newPlayer = new Player(data.x, data.y),
        i,
        existingPlayer;
    newPlayer.id = this.id;
    this.broadcast.emit("new player", {id: newPlayer.id, x: newPlayer.getX(), y: newPlayer.getY()});
    for (i = 0; i < players.length; i++) {
        existingPlayer = players[i];
        this.emit("new player", {id: existingPlayer.id, x: existingPlayer.getX(), y: existingPlayer.getY()});
    }
    players.push(newPlayer);
}

function playerById(id) {
    var i;
    for (i = 0; i < players.length; i++) {
        if (players[i].id == id)
            return players[i];
    }
    return null;
}

init();