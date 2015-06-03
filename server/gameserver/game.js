(function() {
    "use strict";

    var io = require("socket.io"),
        express = require("express"),
        expressHbs = require('express3-handlebars'),
        Player = require("./models/Player").Player,
        Room = require("./models/Room").Room;

    var socket,
        app = express();

    app.engine('hbs', expressHbs({extname:'hbs', defaultLayout:'room.hbs'}));
    app.set('view engine', 'hbs');
    app.locals.layout = false;

    var PORT = 1337;

    app.get('/debug', function (req, res) {
        var allRooms = Room.getAllRoomsAsArray();
        res.render('room', {
            rooms: allRooms
        });
    });

    function init() {
        socket = io.listen(PORT);
        // TODO: Disable logging when done
        app.listen(8888);
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
        client.on("get_rooms", function() {
            onGetRooms(this);
        });
        client.on("get_random_room", function() {
            onGetRandomRoom(this);
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

    function onGetRandomRoom(client) {
        client.emit("receive_random_room", Room.getRandomRoomName());
    }

    function onSwitchRoom(client, room) {
        var currentPlayer = Player.getPlayer(client.id);
        var oldRoom = currentPlayer.room;
        currentPlayer.switchRoom(client, room.name, function(roomData) {
            socket.sockets.in(room.name).emit('update_room', roomData);
            socket.sockets.in(oldRoom.name).emit('update_room', oldRoom);
        });
    }

    function onGetRooms(client) {
        client.emit("room_list", Room.getAllRoomsAsObject());
    }

    init();

}());