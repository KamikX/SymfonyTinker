package sk.kamikx.symfonytinker.toolWindow

import com.intellij.openapi.components.service
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import sk.kamikx.symfonytinker.SymfonyTinkerBundle
import sk.kamikx.symfonytinker.services.MyProjectService
import javax.swing.JButton

class SymfonyTinkerToolWindow(toolWindow: ToolWindow) {

    private val service = toolWindow.project.service<MyProjectService>()

    fun getContent() = JBPanel<JBPanel<*>>().apply {
        val label = JBLabel(SymfonyTinkerBundle.message("randomLabel", "?"))

        add(label)
        add(JButton(SymfonyTinkerBundle.message("shuffle")).apply {
            addActionListener {
                label.text = SymfonyTinkerBundle.message("randomLabel", service.getRandomNumber())
            }
        })
    }
}