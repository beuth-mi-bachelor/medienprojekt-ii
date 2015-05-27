var io = require('socket.io-client'),
    assert = require("assert"),
    expect = require('expect.js');

var myReporter = {
    jasmineStarted: function(suiteInfo) {
        console.log('Running suite with ' + suiteInfo.totalSpecsDefined + " tests:");
    },
    suiteStarted: function(result) {
        console.log('  ' + result.description);
    },
    specStarted: function(result) {
        console.log('    ' + result.description);
    }
};

jasmine.getEnv().addReporter(myReporter);

describe('Suite for testing game logic', function () {
    "use strict";
    var socket,
        PORT = 1337,
        SERVER = "localhost";

    describe('a player is joining the room', function () {

        it('connect to server', function (done) {
            connect(done);
        });

        it('new player is created', function (done) {

            var player = {
                name: "testplayer"
            };

            socket.emit("new_player", player);

            socket.on('success_new_player', function (data) {
                assert.equal(data.player.name, player.name, "player is not equal");
                done();
            });

        });

        it('joining new room', function (done) {
            var room = {
                name: "testroom"
            };

            socket.emit("join_room", room);
            socket.on('success_join_room', function (data) {
                assert.equal(data.room.name, room.name, "room is not equal");
                done();
            });

        });

        it('disconnect from server', function (done) {
            disconnect(done);
        });

    });

    function connect(done) {
        socket = io.connect('http://' + SERVER + ':' + PORT, {
            'reconnection delay': 0,
            'reopen delay': 0,
            'force new connection': true
        });
        socket.on('connect', function () {
            console.log('connected...');
        });
        socket.on('disconnect', function () {
            console.log('disconnected...');
        });
        done();
    }

    function disconnect(done) {
        if (socket.connected) {
            console.log('disconnecting...');
            socket.emit("disconnection");
            socket.on('success_disconnection', function (data) {
                console.log("removed player:\n" + JSON.stringify(data.player) + ",\nplayers online: " + JSON.stringify(data.players));
                socket.disconnect();
                done();
            });
        } else {
            console.log('no connection to break...');
            done();
        }
    }

});

