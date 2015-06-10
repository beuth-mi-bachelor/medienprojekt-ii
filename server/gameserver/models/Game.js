"use strict";

var Player = require("./Player").Player,
    dataSet = require('./../data/data.json');

/**
 * instance of a game in a room
 * @param room {Room} instance of room launching the game
 * @param rounds {int} number of rounds to play
 * @param time {int} time for each round
 * @constructor
 */
function Game(room, rounds, time) {
    this.players = [];
    for (var player in room.players) {
        if (room.players.hasOwnProperty(player)) {
            this.players.push(Player.getPlayer(room.players[player].id));
        }
    }
    this.rounds = rounds || 3;
    this.currentRound = 1;
    this.time = time || 30;
    this.currentTime = time || 30;
    this.points = {
        "teamred": 0,
        "teamblue": 0
    };
    this.streamNames = {
        audio: room.name + "-audio",
        video: room.name + "-video"
    };
    this.currentWord = Game.getDataset();
    this.interval = null;
}

/**
 * selects a random word from our database
 * @returns {{String:*[String]}} an data Object containing the content
 */
Game.getDataset = function () {
    var items = Object.keys(dataSet),
        rand = Math.floor(Math.random()*items.length),
        key = items[rand],
        dataToReturn = {};
    dataToReturn[key] = dataSet[key];
    return dataToReturn;
};

/**
 * starts game with this.rounds rounds
 */
Game.prototype.startGame = function() {
    this.startRound();
};

/**
 * starts a round with this.time time
 */
Game.prototype.startRound = function() {
    var self = this;
    this.currentWord = Game.getDataset();
    this.interval = setInterval(function() {
        self.currentTime -= 1;
        // TODO: send update to clients
        console.log("round " + self.currentRound + ", currentTime: " + self.currentTime);
        if (self.currentTime <= 0) {
            self.endRound(true);
        }
    }, 1000);
};

/**
 * ends a round
 * @param noWinner {boolean} if true it is draw, else someone won
 */
Game.prototype.endRound = function(noWinner) {
    this.interval = clearInterval(this.interval);
    this.interval = null;
    this.currentTime = this.time;
    if (this.rounds - this.currentRound <= 0) {
        this.endGame();
    } else {
        this.currentRound += 1;
    }
    // TODO: show scores
    if (noWinner) {
        // unentschieden
    } else {
        // some won
    }
};

/**
 * ends a game
 */
Game.prototype.endGame = function() {
    // TODO: Emit to all, show scores and so on
    console.log("game ended");
};

/**
 * representation of object
 * @returns {String} stringified version of a Game
 */
Game.prototype.toString = function() {
    return JSON.stringify(this);
};

/**
 * node export
 * @type {Game}
 */
exports.Game = Game;