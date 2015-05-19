(function() {
    "use strict";

    function Player(id,name,roomName) {
        return {
            id: id,
            name : name,
            roomName : roomName
        };
    }

    exports.Player = Player;

}());