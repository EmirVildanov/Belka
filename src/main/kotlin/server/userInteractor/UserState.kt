package server.userInteractor

enum class UserState {
    NOT_STARTED,

    MAIN_MENU,
    FILLING_ACCOUNT_INFO,

    FILLING_NAME,
    FILLING_SURNAME,

    STARTED,
    CHOOSING_DIRECTION;

    val allowedExecutions: List<Execution>
        get() = when (this) {
            NOT_STARTED -> listOf(StartExecution)
            MAIN_MENU -> listOf(FillAccountInfoExecution, HelpExecution, FindExecution)
            FILLING_ACCOUNT_INFO -> listOf(FillNameCommandExecution, FillSurnameCommandExecution, BackExecution(MAIN_MENU))
            FILLING_NAME -> listOf(FillNameExecution, BackExecution(FILLING_ACCOUNT_INFO))
            FILLING_SURNAME -> listOf(FillSurnameNameExecution, BackExecution(FILLING_ACCOUNT_INFO))
            else -> listOf()
        }
}
