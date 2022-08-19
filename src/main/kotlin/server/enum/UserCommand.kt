package server.enum

enum class UserCommand(val commandName: String) {
    START("start"),
    ACCOUNT("account"),
        NAME("name"),
        SURNAME("surname"),
    LOAD_PHOTO("load_photo"),
    OK("ok");
}
