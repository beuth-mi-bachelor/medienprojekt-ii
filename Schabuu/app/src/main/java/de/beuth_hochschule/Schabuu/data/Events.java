package de.beuth_hochschule.Schabuu.data;

public class Events {

    /**
     * Game {
     *      "game": {
     *           "players": Array{Players},
     *            "rounds": Integer,
     *            "currentRound: Integer,
     *            "time": Integer,
     *            "currentTime": Integer,
     *            "timeOutBetweenRounds": Integer,
     *            "room": {Room},
     *            "points": [
     *               // round 1
     *               Object {
     *                    "team_red": Integer,
     *                    "team_blue": Integer
     *               },
     *               // round 2
     *               Object {
     *                    "team_red": Integer,
     *                  "team_blue": Integer
     *               }
     *           ],
     *            "streamNames" = {
     *               "audio": String
     *               "video": String
     *            },
     *            "currentWord": {
     *                word(String): [list, of, words]
     *            }
     *      }
     * }
     */


    /**
     * Room {
     *     name: String,
     *     password: String
     *     players: {
     *          Player.id: {Player}
     *     },
     *     maxPlayers: Integer
     *     gameReady: Integer
     * }
     */

    /**
     * Player {
     *     id: String,
     *     name: String,
     *     room: Room,
     *     role: String,
     *     team: Integer
     * }
     */

    /**
     * only an emitter
     */
    public static final String NEW_PLAYER = "new_player";

    /**
     * JSONObject {
     *      "playerId": String,
     *      "room": String
     * }
     */
    public static final String NEW_PLAYER_CALLBACK = "new_player_callback";

    /**
     * only an emitter
     */
    public static final String RANDOM_ROOM = "random_room";

    /**
     * String roomName
     */
    public static final String RANDOM_ROOM_CALLBACK = "random_room_callback";

    /**
     * only an emitter
     */
    public static final String SWITCH_ROOM = "switch_room";

    /**
     * JSONObject{Room} the room switched to
     */
    public static final String SWITCH_ROOM_CALLBACK = "switch_room_callback";

    /**
     * only an emitter
     */
    public static final String ROOM_LIST = "room_list";

    /**
     * JSONObject {
     *      roomName(String): {Room}
     * }
     */
    public static final String ROOM_LIST_CALLBACK = "room_list_callback";

    /**
     * JSONObject {
     *      "game": {Game}
     * }
     */
    public static final String GAME_READY = "game_ready";

    /**
     * JSONObject {
     *      "time": Integer
     *      "word": {
     *          word(String): [list, of, words]
     *      }
     *      "round": Integer
     * }
     */
    public static final String GAME_START = "game_start";

    /**
     * JSONObject {
     *      "time": Integer
     * }
     */
    public static final String GAME_UPDATE = "game_update";

    /**
     * JSONObject {
     *      "time": Integer
     *      "word": {
     *          word(String): [list, of, words]
     *      }
     *      "round": Integer
     * }
     */
    public static final String GAME_ROUND_START = "game_round_start";

    /**
     * JSONObject {
     *      "points": [
     *          // round 1
     *          Object {
     *              "team_red": Integer,
     *              "team_blue": Integer
     *          },
     *          // round 2
     *          Object {
     *              "team_red": Integer,
     *              "team_blue": Integer
     *          }
     *      ]
     * }
     */
    public static final String GAME_ROUND_END = "game_round_end";

    /**
     * JSONObject {
     *      "points": [
     *          // round 1
     *          Object {
     *              "team_red": Integer,
     *              "team_blue": Integer
     *          },
     *          // round 2
     *          Object {
     *              "team_red": Integer,
     *              "team_blue": Integer
     *          }
     *      ],
     *      "players": Array{Players}
     * }
     */
    public static final String GAME_END = "game_end";

    /**
     * only an emitter
     */
    public static final String PLAYER_ACTIVE = "player_active";

    /**
     * only an emitter
     */
    public static final String PLAYER_INACTIVE = "player_inactive";

    /**
     * only an emitter
     */
    public static final String PLAYER_READY = "player_ready";

    /**
     * JSONObject{Room} complete data of room
     */
    public static final String ROOM_UPDATE = "update_room";

    /**
     * only an emitter
     */
    public static final String ROOM_CHECK = "check_room";

    /**
     * JSONObject {
     *     "name": String
     *     "canJoin": Boolean
     * }
     */
    public static final String ROOM_CHECK_CALLBACK = "check_room_callback";
}
