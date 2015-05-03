var os=require('os');
var net=require('net');

var networkInterfaces=os.networkInterfaces();

var port = 1337;
var count = 1;

function callback_server_connection(socket){

    var remoteAddress = socket.remoteAddress;
    var remotePort = socket.remotePort;
    socket.setNoDelay(true);
    console.log("connected: ", remoteAddress, " : ", remotePort);

    var msg = 'Hello ' + remoteAddress + ' : ' +  remotePort + '\r\n'
        + "You are #" + count + '\r\n';
    count++;

    socket.end(msg);

    socket.on('data', function (data) {
        console.log(data.toString());
    });

    socket.on('end', function () {
        console.log("ended: ", remoteAddress, " : ", remotePort);
    });
}

for (var interf in networkInterfaces) {

    networkInterfaces[interf].forEach(function(details){
        if ((details.family=='IPv4') && !details.internal) {
            console.log("server is waiting under: http://" + details.address + ":" + port);
        }
    });
}

var netServer = net.createServer(callback_server_connection);
netServer.listen(port);