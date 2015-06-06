(function() {
    "use strict";

    // imports
    var Player = require("./Player").Player,
        Room = require("./Room").Room;

    /**
     * instance of a game
     * @param socket {{}} socket
     * @param room {Room} the room which started this game
     * @param timePerRound {int} time in seconds, that each round length is
     * @constructor
     */
    function Game(socket, room, timePerRound) {
        this.socket = socket;
        this.currentTime = 0;
        this.length = timePerRound;
        this.room = room;
        this.players = room.getAllPlayersAsArray();
        this.gameUpdateInterval = null;
    }

    Game.prototype = {
        sendGameUpdates: function() {
            self.currentTime += 1;
            self.socket.sockets.in(self.room.name).emit('game_time_update', self.currentTime);
            if (self.currentTime >= self.length) {
                // END GAME
                // clear interval
                clearInterval(self.gameUpdateInterval);
                self.gameUpdateInterval = null;
                self.endGame();
            }
        },
        startGame: function() {
            this.gameUpdateInterval = setInterval(this.sendGameUpdates(), 1000);
        },
        endGame: function(solution) {
            if (!solution) {
                // no one won
            } else {
                // handle solution
            }
        }
    };

    /**
     * node export
     * @type {Game}
     */
    exports.Game = Game;

}());