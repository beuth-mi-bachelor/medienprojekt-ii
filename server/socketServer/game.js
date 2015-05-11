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
        client.on("join_room", onJoinRoom);
        client.on("leave_room", onLeaveRoom);
    }

    function onClientDisconnect() {
        var removePlayer = playerById(this.id);
        if (!removePlayer) {
            return;
        }
        players.splice(players.indexOf(removePlayer), 1);
        this.broadcast.emit("remove player", {id: this.id});
    }

    function onNewPlayer(data) {
        var newPlayer = new Player(data.x, data.y),
            i,
            existingPlayer;
        newPlayer.id = this.id;
        this.broadcast.emit("new player", {
            id: newPlayer.id
        });
        for (i = 0; i < players.length; i++) {
            existingPlayer = players[i];
            this.emit("new player", {
                id: existingPlayer.id
            });
        }
        players.push(newPlayer);
    }

    function onJoinRoom(roomName) {
        socket.join(roomName, function() {
            socket.emit("room", {
                room: roomName
            });
        });
    }

    function onLeaveRoom(roomName) {
        socket.leave(roomName, function() {
            socket.emit("room", {
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