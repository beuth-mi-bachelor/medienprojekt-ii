(function() {
    "use strict";

    var io = require("socket.io"),
        Player = require("./models/Player").Player,
        Room = require("./models/Room").Room;

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
        client.on("switch_room", function(data) {
            onSwitchRoom(this, data);
        });
    }

    function onDisconnect(client) {
        var currentPlayer = Player.getPlayer(client.id);
        Room.leaveAllRooms(client, currentPlayer, function() {
            currentPlayer.removePlayer();
            client.emit('success_disconnection', {
                message: "removed player",
                player: currentPlayer
            });
        });
    }

    function onNewPlayer(client, data) {
        var defaultRoom = new Room(client.rooms[0]);
        var newPlayer = new Player(client.id, data.name);

        newPlayer.switchRoom(client, "lobby", function() {
            client.emit('success_new_player', {
                message: "added player",
                player: newPlayer
            });
        });
    }

    function onSwitchRoom(client, room) {
        var currentPlayer = Player.getPlayer(client.id);
        currentPlayer.switchRoom(client, room.name, function(roomData) {
            socket.sockets.in(room.name).emit('update_room', roomData);
        });
    }

    init();

}());