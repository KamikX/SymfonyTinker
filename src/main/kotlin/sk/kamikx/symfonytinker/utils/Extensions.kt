package sk.kamikx.symfonytinker.utils

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.vfs.VirtualFile
import sk.kamikx.symfonytinker.types.SymfonyTinkerConsoleType
import com.intellij.openapi.project.Project
import sk.kamikx.symfonytinker.services.ToolWindowService
import sk.kamikx.symfonytinker.toolWindow.SymfonyTinkerToolWindow
import java.awt.Color

fun AnActionEvent.getStFile(): VirtualFile? {
    val project = this.project!!

    // Try to get a selected file from the editor
    val file = FileEditorManager.getInstance(project).selectedEditor?.file
    if (file != null ) {
        return file
    }


    // If not, get a file from the context
    for (file in FileEditorManager.getInstance(project).openFiles) {
        if (file?.fileType is SymfonyTinkerConsoleType) {
            return file
        }
    }

    return null
}


fun Project.getSymfonyTinkerToolWindow(): SymfonyTinkerToolWindow? {
    val toolWindow = this.getService(ToolWindowService::class.java).toolWindow
    return toolWindow
}

fun Project.setSymfonyTinkerToolWindow(toolWindow: SymfonyTinkerToolWindow) {
    this.getService(ToolWindowService::class.java).toolWindow = toolWindow
}


fun Color.toHex(): String {
    return "#" + Integer.toHexString(this.rgb).substring(2)
}