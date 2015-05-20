(function () {
    "use strict";

    var io = require("socket.io"),
        Player = require("./Player").Player;

    var socket;

    var Room = require('./room.js');

    var rooms = {};
    var players = {};

    var PORT = 1337;

    function init() {
        socket = io.listen(PORT);
        setEventHandlers();
    }

    var setEventHandlers = function () {
        socket.sockets.on("connection", onSocketConnection);
    };

    function onSocketConnection(client) {
        client.on("disconnect", function () {
            onClientDisconnect(this);
        });
        client.on("new_player", function (data) {
            onNewPlayer(this, data);
        });
        client.on("switch_room", function (data) {
            onSwitchRoom(this, data);
        });
        client.on("join_room", function (data) {
            onJoinRoom(this, data);
        });
        client.on("leave_room", function (data) {
            onLeaveRoom(this, data);
        });
        client.on("create_room", function (name, password) {
            onCreateRoom(this, name, password);
        });
    }

    function onClientDisconnect(client) {
        delete players[client.id];
        console.log("Client with ID: " + client.id + " and name: " + client.username + " disconnect from server");
    }

    function onNewPlayer(client, data) {
        client.username = data.name;
        client.room = "defaultRoom";
        var newPlayer = new Player(client.id, data.name, client.room);
        players[client.id] = newPlayer;
        console.log("New Player with ID: " + data.id + " and name: " + data.name);
    }

    function onSwitchRoom(client, roomName) {
        client.leave(client.room);
        client.join(roomName);
        console.log(client.username + " switched from room " + socket.room + " to " + roomName);
        client.room = roomName;
    }

    function onJoinRoom(client, roomName) {

        if (!(roomName in rooms)) {
            client.join(roomName, function () {
                client.emit("room", {
                    room: roomName,
                    clientIsInRooms: client.rooms
                });
            });
            client.room = roomName;
            console.log(client.username + " joint room: " + roomName);
        } else {
            client.join(roomName, function () {
                client.emit("room", {
                    room: roomName,
                    clientIsInRooms: client.rooms
                });
            });
            client.room = roomName;
            rooms[roomName].addPerson(client.id);
            players[client.id].room = roomName;
            console.log(client.username + " joint custom room: " + roomName);
        }
    }

    function onLeaveRoom(client, roomName) {
        if (!(roomName in rooms)) {
            client.leave(client.room, function () {
                client.emit("room", {
                    room: roomName,
                    clientIsInRooms: client.rooms
                });
            });
            client.room = "defaultRoom";
            console.log(client.username + " leaved room: " + roomName);
        } else {
            //TO DO if nobody left in the room destroy it
            client.leave(client.room, function () {
                client.emit("room", {
                    room: roomName,
                    clientIsInRooms: client.rooms
                });
            });
            client.room = "defaultRoom";
            console.log(client.username + " leaved room: " + roomName);
        }
    }

    function onCreateRoom(client, name, password) {
        var room = new Room(name, password);
        rooms[name] = room;
        //Emit Update Roomlist here <>
        onJoinRoom(client, name);
    }

    init();

}());