package sk.kamikx.symfonytinker.services

import com.intellij.openapi.Disposable
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
class DynamicGutterService(private val project: Project) : Disposable {

    private val connection: MessageBusConnection = project.messageBus.connect(this)

    init {

        // Register listener for file open/close events
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, object : FileEditorManagerListener {

            override fun fileOpened(source: FileEditorManager, file: VirtualFile) {
                addGutterMarker(file, source.selectedTextEditor)
            }
        })

        // Process currently opened files
        FileEditorManager.getInstance(project).allEditors.forEach { editor ->
            if (editor is TextEditor) {
                addGutterMarker(editor.file, editor.editor)
            }
        }
    }

    private fun addGutterMarker(file: VirtualFile?, editor: Editor?) {
        if (file == null || editor == null) return
        if (file.fileType is SymfonyTinkerConsoleType) {
            ApplicationManager.getApplication().invokeLater({
                val gutterMarker = RunGutterMarker(editor)
                editor.putUserData(RunGutterMarker.KEY, gutterMarker)
            }, project.disposed)
        }
    }

    private fun destroyGutterMarker(file: VirtualFile?, editor: Editor?, withInvokeLater: Boolean = false) {
        if (file == null || editor == null) return
        if (file.fileType is SymfonyTinkerConsoleType) {
            if (withInvokeLater) {
                ApplicationManager.getApplication().invokeLater({
                    RunGutterMarker.removeMarker(editor)
                }, project.disposed)
            } else {
                RunGutterMarker.removeMarker(editor)
            }
        }
    }

    override fun dispose() {
        // cleanup if needed (e.g., remove markers from all open editors)
        // Remove markers from all open editors
        FileEditorManager.getInstance(project).allEditors.forEach { editor ->
            if (editor is TextEditor) {
                destroyGutterMarker(editor.file, editor.editor)
            }
        }
    }
}
