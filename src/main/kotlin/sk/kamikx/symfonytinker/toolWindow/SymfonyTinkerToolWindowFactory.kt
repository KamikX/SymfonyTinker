package sk.kamikx.symfonytinker.toolWindow

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import sk.kamikx.symfonytinker.SymfonyTinkerBundle
import sk.kamikx.symfonytinker.utils.setSymfonyTinkerToolWindow
import sk.kamikx.symfonytinker.action.*

class SymfonyTinkerToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val actionManager = ActionManager.getInstance()
        toolWindow.setTitleActions(
            listOf(
                actionManager.getAction(RunConsoleAction.ID),
                actionManager.getAction(ClearConsoleAction.ID),
                actionManager.getAction(OpenNewConsoleAction.ID),
                //TODO another actions
            )
        )

        // Create a new tool window instance
        val instance = SymfonyTinkerToolWindow(toolWindow)
        // Store instance in project service to access in other places.
        project.setSymfonyTinkerToolWindow(instance)

        val contentFactory = ApplicationManager.getApplication().getService(ContentFactory::class.java)
        val content: Content = contentFactory.createContent(instance, "", false)
        toolWindow.stripeTitle = SymfonyTinkerBundle.message("st.toolWindow.stripeTitle")
        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true

}
