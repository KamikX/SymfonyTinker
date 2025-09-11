package sk.kamikx.symfonytinker.repository

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import sk.kamikx.symfonytinker.settings.ProjectSettings
import sk.kamikx.symfonytinker.SymfonyTinkerBundle
import sk.kamikx.symfonytinker.types.SymfonyTinkerConsoleType
import java.io.File

class ConsoleFileRepository(val project: Project) {

    fun getLastModified(): VirtualFile? {
        return getFiles().firstOrNull()
    }

    fun getLastModifiedOrCreate(initialContent: String? = null): VirtualFile {
        return getFiles().firstOrNull() ?: create(initialContent)
    }

    fun create(initialContent: String? = null): VirtualFile {
        val projectSettings = ProjectSettings.getInstance(project)
        val filesDirectory = File(project.basePath ?: "", projectSettings.filesDirectoryName)
        if (!filesDirectory.exists()) {
            filesDirectory.mkdirs()
            File(filesDirectory, ".gitignore").writeText("*")
        }

        var followNumber = 0
        while (true) {
            val suffix = if (followNumber == 0) "" else "_$followNumber"
            val newConsole = File(filesDirectory, "console${suffix}.st.php")
            if (!newConsole.exists()) {
                newConsole.createNewFile()
                newConsole.appendText("<?php")
                newConsole.appendText("\n")
                // Set the default console code from settings
                newConsole.appendText(projectSettings.defaultConsoleCode.trimIndent())
                newConsole.appendText("\n${initialContent ?: SymfonyTinkerBundle.message("st.console.defaultContent")}\n")
                return VfsUtil.findFileByIoFile(newConsole, true)!!
            }
            followNumber++
        }
    }

    private fun getFiles(): List<VirtualFile> {
        val files = FileTypeIndex.getFiles(SymfonyTinkerConsoleType, GlobalSearchScope.projectScope(project))
            .sortedByDescending { it.modificationStamp }
        return files
    }
}
