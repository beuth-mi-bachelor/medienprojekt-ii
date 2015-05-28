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
     * @constructor
     */
    function Room(name) {
        this.name = name;
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
     * @param callback {Function} callback fn
     */
    Room.leaveAllRooms = function(client, callback) {
        for (var i = 0; i < client.rooms.length; i++) {
            if (i === client.rooms.length-1) {
                client.leave(client.rooms[i], function() {
                    if (callback) {
                        callback();
                    }
                });
            } else {
                client.leave(client.rooms[i]);
            }

        }
    };

    /**
     * switches a player between rooms
     * @param client {{id: String}} reference to the socket
     * @param room {Room} the room to join
     * @param callback1 callback fn
     * @param callback2 callback fn
     */
    Room.switchRoom = function(client, room, callback1, callback2) {
        Room.leaveAllRooms(client, function() {
            room.joinRoom(client, callback1, callback2);
        });
    };

    Room.prototype = {
        /**
         * a client leaves a room
         * @param client {{id: String}} reference to the socket
         * @param callback1 callback fn
         * @param callback2 callback fn
         */
        leaveRoom: function(client, callback1, callback2) {
            client.leave(this.name, function() {
                if (callback1) {
                    callback1();
                }
                if (callback2) {
                    callback2();
                }
            });
        },
        /**
         * a client joins a room
         * @param client {{id: String}} reference to the socket
         * @param callback1 callback fn
         * @param callback2 callback fn
         */
        joinRoom: function(client, callback1, callback2) {
            client.join(this.name, function() {
                if (callback1) {
                    callback1();
                }
                if (callback2) {
                    callback2();
                }
            });
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