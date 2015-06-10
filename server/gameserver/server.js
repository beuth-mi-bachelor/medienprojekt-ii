(function() {
    "use strict";

    var io = require("socket.io"),
        express = require("express"),
        expressHbs = require('express3-handlebars'),
        Player = require("./models/Player").Player,
        Room = require("./models/Room").Room,
        Game = require("./models/Game").Game;

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
        app.listen(7777);
        setEventHandlers();
        testGame();
    }

    function testGame() {
        var p1 = new Player("1", "testPlayer 1");
        var p2 = new Player("2", "testPlayer 2");
        var p3 = new Player("3", "testPlayer 3");
        var p4 = new Player("4", "testPlayer 4");

        var testRoom = new Room("testRoom", 4, "");

        var addPlayer = function(player) {
            testRoom.players[player.id] = {
                id: player.id,
                name: player.name,
                isActive: player.isActive
            };
        };

        addPlayer(p1);
        addPlayer(p2);
        addPlayer(p3);
        addPlayer(p4);

        var rounds = 3;
        var time = 5;

        var game = new Game(testRoom, rounds, time);

        console.log(game.toString());
        game.startGame();
        setTimeout(function() {
            console.log("starting next round");
            game.startRound();
        }, time * 1000 + 500);
        setTimeout(function() {
            game.startRound();
            console.log("starting next round");
        }, ((time * 1000) * 2) + 1000);
    }

    var setEventHandlers = function() {
        socket.sockets.on("connect", onConnect);
    };

    function onConnect(client) {
        client.on("disconnect", function(data) {
            onDisconnect(this, data);
        });
        client.on("new_player", function(data) {
            onNewPlayer(this, data);
        });
        client.on("switch_room", function(data) {
            onSwitchRoom(this, data);
        });
        client.on("room_list", function() {
            onGetRooms(this);
        });
        client.on("random_room", function() {
            onGetRandomRoom(this);
        });
        client.on("get_player", function(data) {
            onGetPlayer(this, data);
        });
        client.on("player_inactive", function() {
            onPlayerInactive(this);
        });
        client.on("player_active", function() {
            onPlayerActive(this);
        });
        client.on("player_ready", function() {
            onPlayerReady(this);
        });
        client.on("check_room", function(data) {
            onCheckRoom(this, data);
        });
    }

    function onDisconnect(client) {
        var currentPlayer = Player.getPlayer(client.id);
        Room.leaveAllRooms(client, currentPlayer, function() {
            currentPlayer.removePlayer();
        });
    }

    function onNewPlayer(client, data) {
        var defaultRoom = new Room(client.rooms[0]);
        var newPlayer = new Player(client.id, data.name);

        newPlayer.switchRoom(socket, client, "lobby", function() {
            client.emit('new_player_callback', {
                playerId: newPlayer.id,
                room: defaultRoom.name
            });
        });
    }

    function onCheckRoom(client, data) {
        var room = Room.getRoom(data.name);
        var canJoin = false;
        if (room) {
            canJoin = !room.isFull();
        } else {
            canJoin = true;
        }
        client.emit('check_room_callback', {
            name: data.name,
            canJoin: canJoin
        });
    }

    function onPlayerInactive(client) {
        var player = Player.getPlayer(client.id);
        player.setActivitiy(false);
        if (player.room) {
            var currentRoom = Room.getRoom(player.room.name);
            currentRoom.players[player.id].isActive = player.isActive;
        }
    }

    function onPlayerActive(client) {
        var player = Player.getPlayer(client.id);
        player.setActivitiy(true);
        if (player.room) {
            var currentRoom = Room.getRoom(player.room.name);
            currentRoom.players[player.id].isActive = player.isActive;

            if (currentRoom.isFull()) {
                if (currentRoom.checkActivity()) {
                    socket.sockets.in(currentRoom.name).emit('game_ready');
                }
            }

        }
    }

    function onPlayerReady(client) {
        var player = Player.getPlayer(client.id);
        if (player.room) {
            var currentRoom = Room.getRoom(player.room.name);
            var rdy = currentRoom.playerIsReady();
            if (rdy) {
                socket.sockets.in(currentRoom.name).emit('game_start');
            }
        }
    }

    function onGetPlayer(client, data) {
        var player = Player.getPlayer(data.id);
        client.emit('get_player_callback', player);
    }

    function onGetRandomRoom(client) {
        client.emit("random_room_callback", Room.getRandomRoomName());
    }

    function onSwitchRoom(client, room) {
        var currentPlayer = Player.getPlayer(client.id);
        var oldRoom = currentPlayer.room;
        currentPlayer.switchRoom(socket, client, room.name, function(roomData) {
            socket.sockets.in(room.name).emit('update_room', roomData);
            socket.sockets.in(oldRoom.name).emit('update_room', oldRoom);
            socket.emit("switch_room_callback", room);
        });
    }

    function onGetRooms(client) {
        client.emit("room_list_callback", Room.getAllRoomsAsObject());
    }

    init();

}());