package server.userInteractor

enum class UserCommand(val commandName: String) {
    START("start"),
    HELP("help"),
    LEAVE_FEEDBACK("leave_feedback"),
    ACCOUNT("account"),
        NAME("name"),
        SURNAME("surname"),
        ABOUT("about"),
    FIND("find"),
    LOAD_PHOTO("load_photo"),
    BACK("back"),
    NO("no"),
    OK("ok");
}
