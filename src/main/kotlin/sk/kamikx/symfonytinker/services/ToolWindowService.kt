package sk.kamikx.symfonytinker.services

import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import sk.kamikx.symfonytinker.toolWindow.SymfonyTinkerToolWindow

@Service(Service.Level.PROJECT)
class ToolWindowService(val project: Project) {
    var toolWindow: SymfonyTinkerToolWindow? = null
}