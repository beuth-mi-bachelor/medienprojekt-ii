(function() {
    "use strict";

    var io = require("socket.io"),
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
        client.on("disconnect", onClientDisconnect);
        client.on("new_player", onNewPlayer);
        client.on("join_room", function(data) {
            onJoinRoom(this, data);
        });
        client.on("leave_room", function(data) {
            onLeaveRoom(this, data);
        });
    }

    function onClientDisconnect(data) {
        var removePlayer = playerById(data.id);
        if (!removePlayer) {
            return;
        }
        players.splice(players.indexOf(removePlayer), 1);
    }

    function onNewPlayer(data) {
        var newPlayer = new Player(data.id);
        console.log(players);
        players.push(newPlayer);
    }

    function onJoinRoom(client, roomName) {
        client.join(roomName, function() {
            client.emit("room", {
                room: roomName
            });
        });
    }

    function onLeaveRoom(client, roomName) {
        client.leave(roomName, function() {
            client.emit("room", {
                room: roomName
            });
        });
    }

    function playerById(id) {
        var i;
        for (i = 0; i < players.length; i++) {
            if (players[i].id === id) {
                return players[i];
            }
        }
        return null;
    }

    init();

}());