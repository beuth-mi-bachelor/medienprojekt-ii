(function() {
    "use strict";

    function Player(id, name) {
        if (!Player.players) {
            Player.players = {};
        }
        this.id = id;
        this.name = name;
        Player.players[id] = this;
    }

    Player.getAllPlayers = function() {
        return Player.players;
    };

    Player.getPlayer = function(id) {
        return Player.players[id];
    };

    Player.prototype.removePlayer = function() {
        delete Player.players[this.id];
    };

    Player.prototype.toString = function() {
        return JSON.stringify(this);
    };

    exports.Player = Player;

}());