package server.userInteractor

enum class UserState {
    NOT_STARTED,

    MAIN_MENU,
    WRITING_APP_FEEDBACK,
    FILLING_ACCOUNT_INFO,

    FILLING_NAME,
    FILLING_SURNAME,
    FILLING_ABOUT,

    STARTED,

    CREATING_APPLICATION,
    CREATING_FROM_POINT,
    CREATING_TO_POINT,
    CREATING_TIME,
    CREATING_LEAVING_COMMENT,

    CHOOSING_APPLICATION,
    CHOOSING_FROM_POINT,
    CHOOSING_TO_POINT,
    CHOOSING_TIME,
    CHOOSING_LEAVING_COMMENT,

    EDITING,
    CONSIDERING_MATCH,

    ASKING_TO_RATE;

    /** Covered in a list of lists for KeyboardCreator automatically finds needed text. */
    val allowedExecutions: List<List<Execution>>
        get() = when (this) {
            NOT_STARTED -> listOf(listOf(StartExecution))
            MAIN_MENU -> listOf(
                listOf(FillAccountInfoExecution, LeaveFeedbackCommandExecution),
                listOf(HelpExecution, FindExecution)
            )
            WRITING_APP_FEEDBACK -> listOf(listOf(LeaveFeedbackTextExecution, BackExecution(MAIN_MENU)))
            FILLING_ACCOUNT_INFO -> listOf(
                listOf(
                    FillNameCommandExecution,
                    FillSurnameCommandExecution
                ),
                listOf(
                    FillAboutCommandExecution,
                    BackExecution(MAIN_MENU)
                )
            )
            FILLING_NAME -> listOf(listOf(FillNameTextExecution, BackExecution(FILLING_ACCOUNT_INFO)))
            FILLING_SURNAME -> listOf(listOf(FillSurnameNameTextExecution, BackExecution(FILLING_ACCOUNT_INFO)))
            FILLING_ABOUT -> listOf(listOf(FillAboutTextExecution, BackExecution(FILLING_ACCOUNT_INFO)))
            STARTED -> listOf(listOf(BackExecution(MAIN_MENU), CreateExecution))
            ASKING_TO_RATE -> listOf(listOf(RatingMatchTextExecution))
            else -> listOf()
        }
}
