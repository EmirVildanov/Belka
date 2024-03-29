package server.userInteractor

import db.MongoDbConnector
import model.AccountInfo
import server.MessageHandlerEnvironmentWrapper
import server.userInteractor.Execution.JustChangeStateCommandExecution
import server.userInteractor.Execution.NonStateChangeCommandExecution
import server.userInteractor.Execution.StateChangeCommandExecution
import server.userInteractor.Execution.TextExecution
import server.userInteractor.Execution.WithOnExecuteLogic
import server.userInteractor.Execution.WithPreExecuteLogic
import server.userInteractor.FillAboutTextExecution.ABOUT_MAX_LENGTH
import server.userInteractor.FillNameTextExecution.NAME_MAX_LENGTH
import server.userInteractor.FillSurnameNameTextExecution.SURNAME_MAX_LENGTH
import server.userInteractor.UserCommand.ABOUT
import server.userInteractor.UserCommand.ACCOUNT
import server.userInteractor.UserCommand.BACK
import server.userInteractor.UserCommand.CHOOSE_FROM
import server.userInteractor.UserCommand.CREATE
import server.userInteractor.UserCommand.FIND
import server.userInteractor.UserCommand.LEAVE_FEEDBACK
import server.userInteractor.UserCommand.NAME
import server.userInteractor.UserCommand.OK
import server.userInteractor.UserCommand.REFUSE
import server.userInteractor.UserCommand.START
import server.userInteractor.UserCommand.SURNAME
import server.userInteractor.UserState.ASKING_TO_RATE
import server.userInteractor.UserState.CREATING_APPLICATION
import server.userInteractor.UserState.CREATING_FROM_POINT
import server.userInteractor.UserState.FILLING_ABOUT
import server.userInteractor.UserState.FILLING_ACCOUNT_INFO
import server.userInteractor.UserState.FILLING_NAME
import server.userInteractor.UserState.FILLING_SURNAME
import server.userInteractor.UserState.MAIN_MENU
import server.userInteractor.UserState.STARTED
import server.userInteractor.UserState.WRITING_APP_FEEDBACK

object StartExecution : JustChangeStateCommandExecution(START, "Okay, let's start!", MAIN_MENU)
class BackExecution(toState: UserState) : JustChangeStateCommandExecution(BACK, "Back.", toState)
class OkExecution(toState: UserState) : JustChangeStateCommandExecution(OK, "Ok.", toState)
class NoExecution(toState: UserState) : JustChangeStateCommandExecution(OK, "No.", toState)

object HelpExecution : WithOnExecuteLogic, NonStateChangeCommandExecution(UserCommand.HELP) {
    private const val HELP_MESSAGE = "There is how this application works:\n" +
            "1.) You may create an application to share a ride with somebody\n" +
            "2.) You may check other's application and choose them\n" +
            "Please contact @<DevAcc> if you need any help."

    override suspend fun onExecute(env: MessageHandlerEnvironmentWrapper) {
        env.sendMessage(accountInfo.state, HELP_MESSAGE)
    }
}

object LeaveFeedbackCommandExecution :
    JustChangeStateCommandExecution(LEAVE_FEEDBACK, "Okay. Please, send me your feedback.", WRITING_APP_FEEDBACK)

object LeaveFeedbackTextExecution : WithPreExecuteLogic, TextExecution("Thank you for your feedback!", MAIN_MENU) {
    private const val MAX_FEEDBACK_LENGTH = 150;

    override suspend fun preExecuteCheck(env: MessageHandlerEnvironmentWrapper): PreExecuteResult {
        if (text.length >= MAX_FEEDBACK_LENGTH) {
            return PreExecuteResult.Error("Feedback must be shorter than $MAX_FEEDBACK_LENGTH symbols.")
        }
        return PreExecuteResult.Ok
    }

    override suspend fun handleTextInner(text: String) {
        MongoDbConnector.addAppFeedback(accountInfo.accountInfoId, text)
    }
}

object FillAccountInfoExecution :
    JustChangeStateCommandExecution(ACCOUNT, "Let's fill accountInfo.", FILLING_ACCOUNT_INFO)

object FillNameCommandExecution : JustChangeStateCommandExecution(
    NAME,
    "Please send me your name. It must not be greater than $NAME_MAX_LENGTH letters.",
    FILLING_NAME
)

object FillSurnameCommandExecution : JustChangeStateCommandExecution(
    SURNAME,
    "Please send me your surname. It must not be greater than $SURNAME_MAX_LENGTH letters.",
    FILLING_SURNAME
)

object FillAboutCommandExecution : JustChangeStateCommandExecution(
    ABOUT,
    "Please fill about section. It must not be greater than $ABOUT_MAX_LENGTH symbols.",
    FILLING_ABOUT
)

object FillNameTextExecution : WithPreExecuteLogic, TextExecution("Name is changed.", FILLING_ACCOUNT_INFO) {
    const val NAME_MAX_LENGTH = 13;

    override suspend fun handleTextInner(text: String) {
        MongoDbConnector.setAccountInfoName(accountInfo.accountInfoId, text)
    }

    override suspend fun preExecuteCheck(env: MessageHandlerEnvironmentWrapper): PreExecuteResult {
        if (text.length > NAME_MAX_LENGTH) {
            return PreExecuteResult.Error("User name must be not be greater than $NAME_MAX_LENGTH")
        }
        return PreExecuteResult.Ok
    }
}

object FillSurnameNameTextExecution : WithPreExecuteLogic,
    TextExecution("Surname is changed.", FILLING_ACCOUNT_INFO) {
    const val SURNAME_MAX_LENGTH = 13;

    override suspend fun handleTextInner(text: String) {
        MongoDbConnector.setAccountInfoSurname(accountInfo.accountInfoId, text)
    }

    override suspend fun preExecuteCheck(env: MessageHandlerEnvironmentWrapper): PreExecuteResult {
        if (text.length > SURNAME_MAX_LENGTH) {
            return PreExecuteResult.Error("User name must be not be greater than $SURNAME_MAX_LENGTH")
        }
        return PreExecuteResult.Ok
    }
}

object FillAboutTextExecution : WithPreExecuteLogic, TextExecution("About is changed.", FILLING_ACCOUNT_INFO) {
    const val ABOUT_MAX_LENGTH = 150;

    override suspend fun handleTextInner(text: String) {
        MongoDbConnector.setAccountInfoAbout(accountInfo.accountInfoId, text)
    }

    override suspend fun preExecuteCheck(env: MessageHandlerEnvironmentWrapper): PreExecuteResult {
        if (text.length > ABOUT_MAX_LENGTH) {
            return PreExecuteResult.Error("About must be not be greater than $ABOUT_MAX_LENGTH.")
        }
        return PreExecuteResult.Ok
    }
}

object FindExecution : WithPreExecuteLogic,
    JustChangeStateCommandExecution(FIND, "Let's find some ride!", STARTED) {
    override suspend fun preExecuteCheck(env: MessageHandlerEnvironmentWrapper): PreExecuteResult {
        if (accountInfo.name == null || accountInfo.about == null) {
            return PreExecuteResult.Error(
                "You must fill fields [name], [about] in order to start working with applications."
            )
        }
        return PreExecuteResult.Ok
    }
}

object CreateExecution : WithPreExecuteLogic,
    JustChangeStateCommandExecution(CREATE, "Let's create new application.", CREATING_APPLICATION) {
    override suspend fun preExecuteCheck(env: MessageHandlerEnvironmentWrapper): PreExecuteResult {
        if (accountInfo.createdApplicationIds.size < 3) {
            return PreExecuteResult.Ok
        }
        return PreExecuteResult.Error("Number of created applications exceed 3.")
    }
}


class ApplicationMatchExecution(userCommand: UserCommand) : JustChangeStateCommandExecution(
    userCommand,
    "Okay. Write some text or get a bad mark for non writing a feedback.",
    ASKING_TO_RATE
)

object RatingMatchTextExecution : WithPreExecuteLogic, TextExecution("Your rate is accepted. Thank You!", STARTED) {
    private const val RATING_MAX_LENGTH = 150

    override suspend fun preExecuteCheck(env: MessageHandlerEnvironmentWrapper): PreExecuteResult {
        if (text.length > RATING_MAX_LENGTH) {
            return PreExecuteResult.Error("Rating must be not be greater than $ABOUT_MAX_LENGTH.")
        }
        return PreExecuteResult.Ok
    }

    override suspend fun handleTextInner(text: String) {
        TODO("Not yet implemented")
    }
}

object RefuseToRateExecution : WithOnExecuteLogic,
    StateChangeCommandExecution(REFUSE, "Okay, you get unreliable badge.", STARTED) {
    override suspend fun doInnerInnerJob(env: MessageHandlerEnvironmentWrapper) {
        TODO("Not yet implemented")
    }
}

suspend fun matchApplicationLogic() {
    val accountInfoCreatedApplication = AccountInfo.createNewAccount(1L)
    val accountInfoAcceptedApplication = AccountInfo.createNewAccount(2L)
    // Unaccept all applications that has time conflicts with this.
    // Redirect

    // Some requests should block db?
    // For example when two users accept one application.

    /**
     * Case: user created application. Other user accepted it.
     * First user starts exploring account, but acception is deleted.
     * If he chooses:
     *  - To accept it -> redirect to the list of reviewing applications,
     *  delete this application and say that it was deleted.
     *  - To back or reject -> redirect to the list and just delete this application.
     *
     *  The same situation when trying to accept application that does not exist any more.
     */
}