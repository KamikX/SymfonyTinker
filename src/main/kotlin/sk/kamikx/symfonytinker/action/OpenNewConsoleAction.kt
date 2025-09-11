package sk.kamikx.symfonytinker.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import sk.kamikx.symfonytinker.SymfonyTinkerBundle
import sk.kamikx.symfonytinker.repository.ConsoleFileRepository

class OpenNewConsoleAction : AnAction() {

    companion object {
        const val ID = "sk.kamikx.symfonytinker.action.OpenNewConsoleAction"
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.isVisible = true
        e.presentation.isEnabled = false
        e.presentation.text = SymfonyTinkerBundle.message("st.menu.action.openNewConsole")
        if (e.project === null) return
        e.presentation.isEnabled = true
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val consoleFileRepository = ConsoleFileRepository(project)
        val tinkerConsole = consoleFileRepository.create()
        FileEditorManager.getInstance(project).openFile(tinkerConsole, true)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.BGT
    }
}
