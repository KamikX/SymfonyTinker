package sk.kamikx.symfonytinker.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import sk.kamikx.symfonytinker.SymfonyTinkerBundle
import sk.kamikx.symfonytinker.services.PhpService
import sk.kamikx.symfonytinker.utils.getStFile

/** Always opens a new tinker console */
class RunConsoleAction : AnAction() {

    companion object {
        const val ID = "sk.kamikx.symfonytinker.action.RunConsoleAction"
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.isVisible = true
        e.presentation.isEnabled = false
        e.presentation.text = SymfonyTinkerBundle.message("st.menu.action.runConsole")
        e.project ?: return
        val file = e.getStFile()
        e.presentation.isEnabled = file != null
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getStFile()

        if (file != null) {
            val symfonyConsoleService = project.getService(PhpService::class.java)
            symfonyConsoleService.runScript(file)
        }
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}
