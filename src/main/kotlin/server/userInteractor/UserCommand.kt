package server.userInteractor

enum class UserCommand(val commandName: String) {
    START("start"),
    HELP("help"),
    LEAVE_FEEDBACK("leave_feedback"),
    ACCOUNT("account"),
        NAME("name"),
        SURNAME("surname"),
        ABOUT("about"),
        LOAD_PHOTO("load_photo"),
    FIND("find"),
    CREATE("create"),

    REFUSE("refuse"),

    BACK("back"),
    NO("no"),
    OK("ok");
}
