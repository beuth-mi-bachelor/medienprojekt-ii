(function() {
    "use strict";

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

    Room.prototype = {
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