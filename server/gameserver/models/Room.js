(function() {
    "use strict";

    /**
     * global room list
     */
    if (!Room.rooms) {
        Room.rooms = {};
    }

    /**
     * constructor for instantiation
     * @param name {String} a unique name for the room
     * @param maxPlayers {Number} number of players for this room
     * @param password {String} for accessing the room
     * @constructor
     */
    function Room(name, maxPlayers, password) {
        this.name = name;
        this.password = password || "";
        this.players = {};
        this.maxPlayers = maxPlayers || 4;
        Room.rooms[name] = this;
    }

    /**
     * get all rooms as an object
     * @returns {{Room}} a JSON-Object containing all rooms
     */
    Room.getAllRoomsAsObject = function() {
        return Room.rooms;
    };

    /**
     * get all rooms as an array
     * @returns {[Room]} an array containing all rooms
     */
    Room.getAllRoomsAsArray = function() {
        return Object.keys(Room.rooms).map(function (key) {
            return Room.rooms[key];
        });
    };

    /**
     * shows how many rooms are registered
     * @returns {Number} a number between zero and infinite
     */
    Room.getNumberOfRooms = function() {
        return Object.keys(Room.rooms).length;
    };

    /**
     * get a specific room by name
     * @param name {String} unique name of room
     * @returns {Room} an instance of the room or null if not found
     */
    Room.getRoom = function(name) {
        if (!Room.rooms.hasOwnProperty(name)) {
            return null;
        }
        return Room.rooms[name];
    };

    /**
     * get a random room name, which is not full
     * @returns {String} an room not filled yet or a generated name
     */
    Room.getRandomRoomName = function() {
        var allRooms = Room.getAllNonFilledRooms();

        // sort by number of players in room
        allRooms.sort(function(a, b){
            return Object.keys(a.players).length > Object.keys(b.players).length;
        });

        // if no rooms found: create one
        if (allRooms.length === 0) {
            return "room-" + new Date().getTime();
        }
        return allRooms[0].name;
    };

    /**
     * get all rooms that are not filled yet
     * @returns {[Room]} a list of rooms
     */
    Room.getAllNonFilledRooms = function() {
        var allRooms = JSON.parse( JSON.stringify( Room.getAllRoomsAsObject() ) ),
            allEmptyRooms = [];
        delete allRooms["lobby"];
        for (var room in allRooms) {
            if (allRooms.hasOwnProperty(room)) {
                var currentRoom = Room.getRoom(allRooms[room].name);
                if (!currentRoom.isFull()) {
                    allEmptyRooms.push(currentRoom);
                }
            }
        }
        return allEmptyRooms;
    };

    /**
     * leaves all rooms - normally a player can only be in one room
     * @param client {{id: String}} reference to the socket
     * @param player {Player} the player joining the room
     * @param callback {Function} callback fn
     */
    Room.leaveAllRooms = function(client, player, callback) {
        if (client.rooms.length === 0 && player.room) {
            player.room.leaveRoom(client, player, callback, null);
        } else {
            for (var i = 0; i < client.rooms.length; i++) {
                var currentRoom = Room.getRoom(client.rooms[i]);
                if (i === client.rooms.length-1) {
                    if (currentRoom !== null) {
                        currentRoom.leaveRoom(client, player, callback, null);
                    }
                } else {
                    if (currentRoom !== null) {
                        currentRoom.leaveRoom(client, player, null, null);
                    }
                }
            }
        }
    };

    /**
     * switches a player between rooms
     * @param socket {{}} socket
     * @param client {{id: String}} reference to the socket
     * @param player {Player} the player joining the room
     * @param room {Room} the room to join
     * @param callback1 callback fn
     * @param callback2 callback fn
     */
    Room.switchRoom = function(socket, client, player, room, callback1, callback2) {
        Room.leaveAllRooms(client, player, function() {
            room.joinRoom(socket, client, player, callback1, callback2);
        });
    };

    Room.prototype = {
        /**
         * a client leaves a room
         * @param client {{id: String}} reference to the socket
         * @param player {Player} the player joining the room
         * @param callback1 callback fn
         * @param callback2 callback fn
         */
        leaveRoom: function(client, player, callback1, callback2) {
            var self = this;
            client.leave(this.name, function() {
                delete self.players[player.id];
                if (self.isEmpty()) {
                    self.remove();
                }
                if (callback1) {
                    callback1(self);
                }
                if (callback2) {
                    callback2(self);
                }
            });
        },
        /**
         * a client joins a room
         * @param socket {{}} socket
         * @param client {{id: String}} reference to the socket
         * @param player {Player} the player joining the room
         * @param callback1 callback fn
         * @param callback2 callback fn
         */
        joinRoom: function(socket, client, player, callback1, callback2) {
            var self = this;
            client.join(this.name, function() {
                self.players[player.id] = {
                    name: player.name,
                    isActive: player.isActive
                };
                if (self.isFull()) {
                    if (self.checkActivity()) {
                        socket.sockets.in(self.name).emit('game_ready');
                    }
                }
                if (callback1) {
                    callback1(self);
                }
                if (callback2) {
                    callback2(self);
                }
            });
        },
        /**
         * get the number of players currently in this room
         * @returns {Number} of players between 0 and maxPlayers for this room
         */
        getNumberOfPlayersInRoom: function() {
            return Object.keys(this.players).length;
        },
        checkActivity: function() {
            for (var player in this.players) {
                if (this.players.hasOwnProperty(player) && !this.players[player].isActive) {
                    return false;
                }
            }
            return true;
        },
        /**
         * checks if the room is full with players
         * @returns {boolean} checks if maxPlayers is exceeded
         */
        isFull: function() {
            if (this.name === "lobby") {
                return false;
            }
            return !(this.getNumberOfPlayersInRoom() < this.maxPlayers);
        },
        /**
         * checks if the room is empty
         * @returns {boolean} checks if currentPlayers is zero
         */
        isEmpty: function() {
            if (this.name === "lobby") {
                return false;
            }
            return !this.getNumberOfPlayersInRoom();
        },
        /**
         * removes given room
         */
        remove: function() {
            if (this.name === "lobby") {
                return;
            }
            Room.rooms[this.name] = {};
            delete Room.rooms[this.name];
        },
        /**
         * displays a readable string of a room instance
         * @returns {{String}} representation of this room
         */
        toString: function() {
            return JSON.stringify(this);
        }
    };

    /**
     * node export
     * @type {Room}
     */
    exports.Room = Room;

}());