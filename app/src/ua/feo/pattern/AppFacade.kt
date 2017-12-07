package ua.feo.pattern

import javafx.scene.input.MouseEvent
import ua.feo.app.data.ProgramData
import ua.feo.app.inf.*

object AppFacade {

    private val programData : ProgramData = ProgramData.get()
    private var stageRouter : StageRouter = programData.stageRouter

    private lateinit var loginWindow : LoginWindow
    private lateinit var mainWindow : MainWindow
    private lateinit var taskWindow : TaskWindow
    private lateinit var resultWindow : ResultWindow

    fun gotoLoginWindow() = with (stageRouter.goTo(StageRouter.LOGIN_WINDOW) as LoginWindow) { loginWindow = this; this }
    fun gotoMainWindow() = with (stageRouter.goTo(StageRouter.MAIN_WINDOW) as MainWindow) { mainWindow = this; this }
    fun gotoTaskWindow() = with (stageRouter.goTo(StageRouter.TASK_WINDOW) as TaskWindow) { taskWindow = this; this }
    fun gotoResultindow() = with (stageRouter.goTo(StageRouter.RESULT_WINDOW) as ResultWindow) { resultWindow = this; this }

    fun loginButtonClick() = loginWindow.loginButtonClick()
    fun registrationButtonClick() = loginWindow.registrationButtonClick()

    fun exitToAuthButtonClick() = mainWindow.exitButtonClick()
    fun taskListItemClick(event : MouseEvent) = mainWindow.taskListItemClick(event)

    fun exitButtonClick() = taskWindow.exitButtonClick()
    fun endButtonClick() = taskWindow.endButtonClick()
    fun reloadButtonClick() = taskWindow.reloadButtonClick()
    fun runButtonClick() = taskWindow.runButtonClick()

    fun mainButtonClick() = resultWindow.mainButtonClick()
    fun repeatButtonClick() = resultWindow.repeatButtonClick()
    fun nextButtonClick() = resultWindow.nextButtonClick()

}