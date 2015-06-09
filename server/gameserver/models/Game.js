"use strict";

/**
 * instance of a game in a room
 * @constructor
 */
function Game(room) {
    this.players = room.getAllPlayersAsArray();
    this.streamNames = {
        audio: room.name + "-audio",
        video: room.name + "-video"
    }
}

/**
 * node export
 * @type {Game}
 */
exports.Game = Game;