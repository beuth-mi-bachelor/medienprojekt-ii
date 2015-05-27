(function() {
    "use strict";

    /**
     * constructor for instantiation
     * @param id {String} a unique identifier for a player
     * @param name {String} represents the player
     * @constructor
     */
    function Player(id, name) {
        if (!Player.players) {
            Player.players = {};
        }
        this.id = id;
        this.name = name;
        Player.players[id] = this;
    }

    /**
     * get all players as an object
     * @returns {{Player}} a JSON-Object containing all players
     */
    Player.getAllPlayersAsObject = function() {
        return Player.players;
    };

    /**
     * get all players as an array
     * @returns {[Player]} an array containing all players
     */
    Player.getAllPlayersAsArray = function() {
        return Object.keys(Player.players).map(function (key) {
            return Player.players[key];
        });
    };

    /**
     * shows how many players are registered
     * @returns {Number} a number between zero and infinite
     */
    Player.getNumberOfPlayers = function() {
        return Object.keys(Player.players).length;
    };

    /**
     * get a specific player by id
     * @param id {Number} unique id of player
     * @returns {Player} an instance of the player or null if not found
     */
    Player.getPlayer = function(id) {
        if (!Player.players.hasOwnProperty(id)) {
            return null;
        }
        return Player.players[id];
    };

    /**
     * removes the current player
     */
    Player.prototype.removePlayer = function() {
        if (Player.players.hasOwnProperty(this.id)) {
            delete Player.players[this.id];
        }
    };

    /**
     * displays a readable string of a player instance
     * @returns {{String}} representation of this player
     */
    Player.prototype.toString = function() {
        return JSON.stringify(this);
    };

    exports.Player = Player;

}());