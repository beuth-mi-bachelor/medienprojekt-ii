(function() {
    "use strict";

    Room.rooms = {};

    /**
     * constructor for instantiation
     * @param name {String} a unique name for the room
     * @constructor
     */
    function Room(name) {
        if (!Room.rooms) {
            Room.rooms = {};
        }
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

    Room.prototype = {
        switchRoom: function(client, room, callback) {
            console.log(room, callback);
            Room.leaveAllRooms(client, function() {
                room.joinRoom(client, callback);
            });
        },
        leaveRoom: function(client, callback) {
            client.leave(this.name, function() {
                if (callback) {
                    callback();
                }
            });
        },
        joinRoom: function(client, callback) {
            client.join(this.name, function() {
                if (callback) {
                    callback();
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

    exports.Room = Room;

}());