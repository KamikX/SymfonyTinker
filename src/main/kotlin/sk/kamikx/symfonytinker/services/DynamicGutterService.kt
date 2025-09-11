package sk.kamikx.symfonytinker.services

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.messages.MessageBusConnection
import sk.kamikx.symfonytinker.markers.RunGutterMarker
import sk.kamikx.symfonytinker.types.SymfonyTinkerConsoleType

@Service(Service.Level.PROJECT)
class DynamicGutterService(private val project: Project) {

    val connection: MessageBusConnection = project.messageBus.connect()

    fun start() {
        // Register listener for file open/close events
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, object : FileEditorManagerListener {

            override fun fileClosed(
                source: FileEditorManager,
                file: VirtualFile
            ) {
                destroyCarterMarker(file, source.selectedTextEditor)
            }

            override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
                addCarterMarker(file, source.selectedTextEditor)
            }

        })

        // Process current opened files
        val fileEditorManager = FileEditorManager.getInstance(project)
        fileEditorManager.allEditors.forEach { editor ->
            if (editor is TextEditor) {
                addCarterMarker(editor.file, editor.editor)
            }
        }
    }

    private fun addCarterMarker(file: VirtualFile?, editor: Editor?) {
        if (file == null || editor == null) return
        if (file.fileType is SymfonyTinkerConsoleType) {
            ApplicationManager.getApplication().invokeLater {
                val carterMarker = RunGutterMarker(editor)
                editor.putUserData(RunGutterMarker.KEY, carterMarker)
            }
        }
    }

    private fun destroyCarterMarker(file: VirtualFile?, editor: Editor?) {
        if (file == null || editor == null) return
        if (file.fileType is SymfonyTinkerConsoleType) {
            ApplicationManager.getApplication().invokeLater {
                RunGutterMarker.removeMarker(editor)
            }
        }
    }
}