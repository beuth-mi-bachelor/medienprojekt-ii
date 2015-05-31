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
    var client1,
        client2,
        PORT = 1337,
        SERVER = "localhost";

    describe('First tests with 2 Clients', function () {

        it('client1 is connecting to server', function (done) {
            connect1(done);
        });

        it('new player for client1 is created', function (done) {
            var player = {
                name: "testplayer1"
            };

            client1.emit("new_player", player);

            client1.on('success_new_player', function (data) {
                assert.equal(data.player.name, player.name, "player is not equal");
                assert.equal(data.player.room.name, "lobby", "player is not in lobby");
                done();
            });

        });

        it('client1 switching the room', function (done) {
            var room = {
                name: "testroom"
            };

            client1.emit("switch_room", room);

            client1.on('update_room', function (newRoom) {
                assert.equal(newRoom.name, room.name, "room is not equal");
                done();
            });

        });

        it('client1 is connecting to server', function (done) {
            connect2(done);
        });

        it('new player for client2 is created', function (done) {

            var player = {
                name: "testplayer2"
            };

            client2.emit("new_player", player);

            client2.on('success_new_player', function (data) {
                assert.equal(data.player.name, player.name, "player is not equal");
                assert.equal(data.player.room.name, "lobby", "player is not in lobby");
                done();
            });

        });

        it('client2 switching the room', function (done) {
            var room = {
                name: "testroom"
            };

            var allBroadcasted = {
                first: false,
                second: false
            };

            client2.emit("switch_room", room);

            client1.on('update_room', function (data) {
                console.log(data);
                // ONLY FOR ASYNC TESTING
                allBroadcasted.first = true;
            });

            client2.on('update_room', function (data) {
                console.log(data);
                // ONLY FOR ASYNC TESTING
                allBroadcasted.second = true;
            });

            var check = setInterval(function() {
                if (allBroadcasted.first && allBroadcasted.second) {
                    clearInterval(check);
                    done();
                }
            }, 10);

        });

        it('disconnect client1 from server', function (done) {
            disconnect1(done);
        });

        it('disconnect client2 from server', function (done) {
            disconnect2(done);
        });

    });

    function connect1(done) {
        client1 = io.connect('http://' + SERVER + ':' + PORT, {
            'reconnection delay': 0,
            'reopen delay': 0,
            'force new connection': true
        });
        client1.on('connect', function () {
            console.log('connected...');
        });
        client1.on('disconnect', function () {
            console.log('disconnected...');
        });
        done();
    }

    function connect2(done) {
        client2 = io.connect('http://' + SERVER + ':' + PORT, {
            'reconnection delay': 0,
            'reopen delay': 0,
            'force new connection': true
        });
        client2.on('connect', function () {
            console.log('connected...');
        });
        client2.on('disconnect', function () {
            console.log('disconnected...');
        });
        done();
    }

    function disconnect1(done) {
        if (client1.connected) {
            console.log('disconnecting...');
            client1.emit("disconnection");
            client1.on('success_disconnection', function (data) {
                console.log("removed player: " + JSON.stringify(data.player.name) + "\nplayers online: " + JSON.stringify(data.players));
                client1.disconnect();
                done();
            });
        } else {
            console.log('no connection to break...');
            done();
        }
    }

    function disconnect2(done) {
        if (client2.connected) {
            console.log('disconnecting...');
            client2.emit("disconnection");
            client2.on('success_disconnection', function (data) {
                console.log("removed player: " + JSON.stringify(data.player.name) + "\nplayers online: " + JSON.stringify(data.players));
                client2.disconnect();
                done();
            });
        } else {
            console.log('no connection to break...');
            done();
        }
    }

});

