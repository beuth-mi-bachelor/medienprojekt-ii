"use strict";

var Player = require("./Player").Player,
    dataSet = require('./../data/data.json');

if (!Game.games) {
    Game.games = {};
}

Game.roles = ["guesser", "audio", "video"];
Game.team = [0, 1];

/**
 * instance of a game in a room
 * @param server {EventEmitter} pub sub system to server
 * @param room {Room} instance of room launching the game
 * @param rounds {int} number of rounds to play
 * @param time {int} time for each round
 * @constructor
 */
function Game(server, room, rounds, time) {
    this.players = [];
    var index = 0;
    this.server = server;
    this.rounds = rounds || 3;
    this.currentRound = 1;
    this.time = time || 30;
    this.currentTime = time || 30;
    this.timeOutBetweenRounds = 8;
    this.room = room;
    this.score = {
        0: 0,
        1: 0
    };
    this.points = [];
    this.streamNames = {
        audio: room.name + "-audio",
        video: room.name + "-video"
    };
    this.currentWord = Game.getDataset();
    var self = this;
    for (var player in room.players) {
        if (room.players.hasOwnProperty(player)) {
            var currentPlayer = Player.getPlayer(room.players[player].id);
            setRole(currentPlayer, index, self);
            currentPlayer.team =  Game.team[(index) % Game.team.length];
            this.players.push(currentPlayer);
            index++;
        }
    }
    Game.games[this.room.name] = this;
    this.interval = null;
}

function setRole(currentPlayer, index, self) {
    currentPlayer.role = Game.roles[(index + (self.currentRound-1)) % Game.roles.length];
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
 * get selected game
 * @returns {Game} containing the game instance
 */
Game.getGame = function (name) {
    return Game.games[name];
};

/**
 * starts game with this.rounds rounds
 */
Game.prototype.startGame = function() {
    this.startRound();
    this.currentWord = Game.getDataset();
    this.server.emit("emitToRoom", this.room.name, 'game_start', {
        time: this.currentTime,
        word: this.currentWord,
        score: this.score,
        round: this.currentRound
    });
};

/**
 * starts a round with this.time time
 */
Game.prototype.startRound = function() {
    var self = this;
    if (this.currentRound != 1) {
        this.currentWord = Game.getDataset();
    }
    this.server.emit("emitToRoom", this.room.name, 'game_round_start', {
        time: this.currentTime,
        word: this.currentWord,
        score: this.score,
        round: this.currentRound
    });
    this.interval = setInterval(function() {
        self.currentTime -= 1;
        self.server.emit("emitToRoom", self.room.name, 'game_update', {
            time: self.currentTime
        });
        if (self.currentTime <= 0) {
            self.endRound(true);
        }
    }, 1000);
};

/**
 * ends a round
 * @param winner {Number} if null it is draw, else teamID won
 */
Game.prototype.endRound = function(winner) {
    this.interval = clearInterval(this.interval);
    this.interval = null;
    this.currentTime = this.time;
    var self = this;

    for (var i = 0; i < this.players.length; i++) {
        setRole(self.players[i], i, self);
    }

    if (!winner) {
        this.points.push({0: 5, 1: 5});
        this.score[0] += 5;
        this.score[1] += 5;
    } else {
        if (parseInt(winner, 10) === 0) {
            this.points.push({0: 10, 1: 0});
            this.score[0] += 10;
        }  else {
            this.points.push({0: 0, 1: 10});
            this.score[1] += 10;
        }
    }

    this.server.emit("emitToRoom", this.room.name, 'game_round_end', {
        points: this.points,
        score: this.score,
        players: this.players,
        teamWon: winner,
        timeout: this.timeOutBetweenRounds
    });

    if (this.rounds - this.currentRound <= 0) {
        this.endGame();
    } else {
        this.currentRound += 1;
        setTimeout(this.startRound(), this.timeOutBetweenRounds);
    }

};

/**
 * get all games as an array
 * @returns {[Game]} an array containing all games
 */
Game.getAllGamesAsArray = function () {
    return Object.keys(Game.games).map(function (key) {
        return Game.games[key];
    });
};

/**
 * ends a game
 */
Game.prototype.endGame = function() {
    this.server.emit("emitToRoom", this.room.name, 'game_end', {
        points: this.points,
        score: this.score
    });
    delete Game.games[this.room.name];
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