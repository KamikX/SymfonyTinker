package sk.kamikx.symfonytinker.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import sk.kamikx.symfonytinker.SymfonyTinkerBundle
import sk.kamikx.symfonytinker.utils.getStFile
import sk.kamikx.symfonytinker.utils.getSymfonyTinkerToolWindow

class ClearConsoleAction : AnAction() {

    companion object {
        const val ID = "sk.kamikx.symfonytinker.action.ClearConsoleAction"
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.isVisible = true
        e.presentation.isEnabled = false
        e.presentation.text = SymfonyTinkerBundle.message("st.menu.action.clearConsole")
        e.project ?: return
        val file = e.getStFile()
        e.presentation.isEnabled = file != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getStFile()

        if (file != null) {
            val tinkerWindow = project.getSymfonyTinkerToolWindow() ?: return
            tinkerWindow.clearOutput()
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}
