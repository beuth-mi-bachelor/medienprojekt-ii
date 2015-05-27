(function() {
    "use strict";

    var io = require("socket.io"),
        Player = require("./models/Player").Player,
        Room = require("./models/Player").Room;

    var socket;

    var PORT = 1337;

    function init() {
        socket = io.listen(PORT);
        setEventHandlers();
    }

    var setEventHandlers = function() {
        socket.sockets.on("connection", onConnect);
    };

    function onConnect(client) {
        client.on("disconnection", function(data) {
            onDisconnect(this, data);
        });
        client.on("new_player", function(data) {
            onNewPlayer(this, data);
        });
        client.on("join_room", function(data) {
            onJoinRoom(this, data);
        });
        client.on("leave_room", function(data) {
            onLeaveRoom(this, data);
        });
    }

    function onDisconnect(client) {
        // leave all rooms
        for (var i = 0; i < client.rooms.length; i++) {
            client.leave(client.rooms[i]);
        }
        var currentPlayer = Player.getPlayer(client.id);
        currentPlayer.removePlayer();
        /**
         * Success callback
         */
        client.emit('success_disconnection', {
            message: "removed player",
            player: currentPlayer,
            players: Player.getAllPlayersAsObject()
        });
    }

    function onNewPlayer(client, data) {
        var newPlayer = new Player(client.id, data.name, client);
        /**
         * Success callback
         */
        client.emit('success_new_player', {
            message: "added player",
            player: newPlayer,
            players: Player.getAllPlayersAsObject()
        });
    }

    function onJoinRoom(client, roomName) {
        client.join(roomName, function() {
            client.emit("success_join_room", {
                room: roomName,
                clientIsInRooms: client.rooms
            });
        });
    }

    function onLeaveRoom(client, roomName) {
        client.leave(roomName, function() {
            client.emit("room", {
                room: roomName,
                clientIsInRooms: client.rooms
            });
        });
    }

    init();

}());