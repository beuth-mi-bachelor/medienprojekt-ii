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
     * @constructor
     */
    function Room(name, maxPlayers) {
        this.name = name;
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
     * leaves all rooms - normally a player can only be in one room
     * @param client {{id: String}} reference to the socket
     * @param player {Player} the player joining the room
     * @param callback {Function} callback fn
     */
    Room.leaveAllRooms = function(client, player, callback) {
        for (var i = 0; i < client.rooms.length; i++) {
            var currentRoom = Room.getRoom(client.rooms[i]);
            console.log(currentRoom);
            if (i === client.rooms.length-1) {
                currentRoom.leaveRoom(client, player, callback, null);
            } else {
                currentRoom.leaveRoom(client, player, callback, null);
            }
        }
    };

    /**
     * switches a player between rooms
     * @param client {{id: String}} reference to the socket
     * @param player {Player} the player joining the room
     * @param room {Room} the room to join
     * @param callback1 callback fn
     * @param callback2 callback fn
     */
    Room.switchRoom = function(client, player, room, callback1, callback2) {
        Room.leaveAllRooms(client, player, function() {
            room.joinRoom(client, player, callback1, callback2);
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
         * @param client {{id: String}} reference to the socket
         * @param player {Player} the player joining the room
         * @param callback1 callback fn
         * @param callback2 callback fn
         */
        joinRoom: function(client, player, callback1, callback2) {
            var self = this;
            client.join(this.name, function() {
                self.players[player.id] = true;
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
        /**
         * checks if the room is full with players
         * @returns {boolean} checks if maxPlayers is exceeded
         */
        roomIsFull: function() {
            if (this.name === "lobby") {
                return false;
            }
            return (this.getNumberOfPlayersInRoom() < this.maxPlayers);
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