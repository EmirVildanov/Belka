package server.userInteractor

enum class UserState {
    NOT_STARTED,

    MAIN_MENU,
    FILLING_ACCOUNT_INFO,

    FILLING_NAME,
    FILLING_SURNAME,
    FILLING_ABOUT,

    STARTED;

    val allowedExecutions: List<Execution>
        get() = when (this) {
            NOT_STARTED -> listOf(StartExecution)
            MAIN_MENU -> listOf(FillAccountInfoExecution, HelpExecution, FindExecution)
            FILLING_ACCOUNT_INFO -> listOf(
                FillNameCommandExecution,
                FillSurnameCommandExecution,
                FillAboutCommandExecution,
                BackExecution(MAIN_MENU)
            )
            FILLING_NAME -> listOf(FillNameTextExecution, BackExecution(FILLING_ACCOUNT_INFO))
            FILLING_SURNAME -> listOf(FillSurnameNameTextExecution, BackExecution(FILLING_ACCOUNT_INFO))
            FILLING_ABOUT -> listOf(FillAboutTextExecution, BackExecution(FILLING_ACCOUNT_INFO))
            STARTED -> listOf(BackExecution(MAIN_MENU))
            else -> listOf()
        }
}
