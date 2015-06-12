"use strict";

var io = require("socket.io"),
    express = require("express"),
    expressHbs = require('express3-handlebars'),
    Player = require("./models/Player").Player,
    Room = require("./models/Room").Room,
    Game = require("./models/Game").Game,
    EventEmitter = require('events').EventEmitter;

var socket,
    server = new EventEmitter(),
    app = express();

app.engine('hbs', expressHbs({extname:'hbs', defaultLayout:'debugging.hbs'}));
app.set('view engine', 'hbs');
app.locals.layout = false;

var args = process.argv.slice(2),
    PORT = parseInt(args[0], 10) || 80;

app.get('/debug', function (req, res) {
    var allRooms = Room.getAllRoomsAsArray(),
        allGames = Game.getAllGamesAsArray(),
        allPlayers = Player.getAllPlayersAsArray();

    res.render('debugging', {
        rooms: allRooms,
        games: allGames,
        players: allPlayers
    });
});

app.get(/^(.+)$/, function (req, res) {
    res.sendFile(__dirname + req.params[0]);
});

server.on('emitToRoom', function(roomName, event, data) {
    socket.sockets.in(roomName).emit(event, data);
});

server.on('startAGame', function(room, rounds, time) {
    if (room.isFull()) {
        if (room.checkActivity()) {
            var game = new Game(server, room, rounds, time);
            server.emit("emitToRoom", room.name, 'game_ready', {game: game});
        }
    }
});

function init() {
    socket = io.listen(PORT);
    // TODO: Disable logging when done
    app.listen(8080);
    setEventHandlers();
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
    client.on("player_change_name", function(data) {
        onChangeName(this, data);
    });
    client.on("solution_correct", function() {
        onSolution(this);
    });
}

function onDisconnect(client) {
    var currentPlayer = Player.getPlayer(client.id);
    Room.leaveAllRooms(client, currentPlayer, function() {
        currentPlayer.removePlayer();
    });
}

function onNewPlayer(client, data) {
    var defaultRoom = new Room(server, client.rooms[0]);
    var newPlayer = new Player(server, client.id, data.name);

    newPlayer.switchRoom(socket, client, "lobby", function() {
        client.emit('new_player_callback', {
            playerId: newPlayer.id,
            room: defaultRoom.name
        });
    });
}

function onChangeName(client, data) {
    var currentPlayer = Player.getPlayer(client.id);
    currentPlayer.changeName(data["playername"]);
    currentPlayer.room.players[client.id].name = data["playername"];
}

function onSolution(client) {
    var currentPlayer = Player.getPlayer(client.id);
    var roomName = currentPlayer.room.name;
    var game = Game.getGame(roomName);
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
        server.emit("startAGame", currentRoom, 3, 30);
    }
}

function onPlayerReady(client) {
    var player = Player.getPlayer(client.id);
    if (player.room) {
        var currentRoom = Room.getRoom(player.room.name);
        var rdy = currentRoom.playerIsReady();
        if (rdy) {
            Game.games[currentRoom.name].startGame();
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