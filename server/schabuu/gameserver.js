"use strict";

var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);

var PORT = 1337;

app.get('/', function (req, res) {
    res.sendFile(__dirname + '/fastSocketTest/index.html');
});

io.on('connection', function (client) {

    client.on('message', function (msg, err) {
        console.log(msg);
    });

});

http.listen(PORT, function () {
    console.log('listening on http://localhost:' + PORT);
});