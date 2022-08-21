package server.userInteractor

enum class UserCommand(val commandName: String) {
    START("start"),
    HELP("help"),
    ACCOUNT("account"),
        NAME("name"),
        SURNAME("surname"),
    FIND("find"),
    LOAD_PHOTO("load_photo"),
    BACK("back"),
    OK("ok");
}
