/**
 * Created by Sprotte on 18.05.15.
 */
function Room(name, password) {
    this.name = name;
    this.people = [];
    this.status = "available";
    this.password = password;
};

Room.prototype.addPerson = function(personID) {
    if (this.status === "available") {
        this.people.push(personID);
    }
};

module.exports = Room;
