package sk.kamikx.symfonytinker.toolWindow

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import sk.kamikx.symfonytinker.SymfonyTinkerBundle


class SymfonyTinkerToolWindowFactory : ToolWindowFactory {

    init {
        thisLogger().warn("Don't forget to remove all non-needed sample code files with their corresponding registration entries in `plugin.xml`.")
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val symfonyTinkerToolWindow = SymfonyTinkerToolWindow(toolWindow)
        val content = ContentFactory.getInstance().createContent(symfonyTinkerToolWindow.getContent(), null, false)

        // Set title
        toolWindow.stripeTitle = SymfonyTinkerBundle.message("st.toolWindow.stripeTitle")

        toolWindow.contentManager.addContent(content)
    }

    override fun shouldBeAvailable(project: Project) = true
}
