(function () {
    "use strict";

    // imports
    var Room = require("./Room").Room;

    /**
     * global player list
     */
    if (!Player.players) {
        Player.players = {};
    }

    /**
     * constructor for instantiation
     * @param server {EventEmitter} pub sub system to server
     * @param clientID {String} a unique identifier for a player
     * @param name {String} represents the player
     * @constructor
     */
    function Player(server, clientID, name) {
        this.id = clientID;
        this.name = name;
        this.isActive = true;
        this.room = null;
        this.server = server;
        this.role = null;
        this.team = null;
        Player.players[clientID] = this;
    }

    /**
     * get all players as an object
     * @returns {{Player}} a JSON-Object containing all players
     */
    Player.getAllPlayersAsObject = function () {
        return Player.players;
    };

    /**
     * get all players as an array
     * @returns {[Player]} an array containing all players
     */
    Player.getAllPlayersAsArray = function () {
        return Object.keys(Player.players).map(function (key) {
            return Player.players[key];
        });
    };

    /**
     * shows how many players are registered
     * @returns {Number} a number between zero and infinite
     */
    Player.getNumberOfPlayers = function () {
        return Object.keys(Player.players).length;
    };

    /**
     * get a specific player by id
     * @param id {Number} unique id of player
     * @returns {Player} an instance of the player or null if not found
     */
    Player.getPlayer = function (id) {
        if (!Player.players.hasOwnProperty(id)) {
            return null;
        }
        return Player.players[id];
    };

    /**
     * removes the current player
     */
    Player.prototype.removePlayer = function () {
        if (Player.players.hasOwnProperty(this.id)) {
            delete Player.players[this.id];
        }
    };

    /**
     * switches the current room
     * @param socket {{}} socket
     * @param client {{id: String}} reference to the socket
     * @param roomName {String} name of the room
     * @param callback {Function} callback fn
     */
    Player.prototype.switchRoom = function (socket, client, roomName, callback) {
        var self = this;
        var room = Room.getRoom(roomName);

        if (!room) {
            room = new Room(this.server, roomName);
        }
        if (room.isFull()) {
            console.log("room was full");
            client.emit("room_full", room);
            return;
        }
        Room.switchRoom(socket, client, this, room, function () {
            self.room = room;
        }, callback);
    };

    /**
     * if player minized the app
     * @param newValue {boolean} false, if player is not in game, else true
     */
    Player.prototype.setActivitiy = function (newValue) {
        this.isActive = newValue;
    };

    /**
     * change name of a player
     * @param name {String} new name of the player
     */
    Player.prototype.changeName = function (name) {
        this.name = name;
    };

    /**
     * displays a readable string of a player instance
     * @returns {{String}} representation of this player
     */
    Player.prototype.toString = function () {
        return JSON.stringify(this);
    };

    /**
     * node export
     * @type {Player}
     */
    exports.Player = Player;

}());