package sk.kamikx.symfonytinker.services

import com.intellij.execution.process.KillableProcessHandler
import com.intellij.execution.process.OSProcessHandler
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessTerminatedListener
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task.Backgroundable
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.remote.BaseRemoteProcessHandler
import com.jetbrains.php.composer.ComposerUtils
import com.jetbrains.php.config.PhpProjectConfigurationFacade
import com.jetbrains.php.config.commandLine.PhpCommandSettings
import com.jetbrains.php.config.commandLine.PhpCommandSettingsBuilder
import com.jetbrains.php.config.interpreters.PhpInterpreter
import com.jetbrains.php.run.script.PhpScriptRunConfiguration
import com.jetbrains.php.run.script.PhpScriptRuntimeConfigurationProducer
import sk.kamikx.symfonytinker.SymfonyTinkerBundle
import sk.kamikx.symfonytinker.settings.ProjectSettings
import sk.kamikx.symfonytinker.utils.NotificationUtils
import java.io.File
import java.nio.charset.StandardCharsets
import kotlin.io.readText
import sk.kamikx.symfonytinker.listeners.PhpProcessListener
import sk.kamikx.symfonytinker.settings.AppSettings
import sk.kamikx.symfonytinker.utils.getSymfonyTinkerToolWindow

@Service(Service.Level.PROJECT)
class PhpService(private val project: Project) {

    private val progressManager = ProgressManager.getInstance()
    private val toolWindowManager = ToolWindowManager.getInstance(project)
    private val projectSettings = ProjectSettings.getInstance(project)
    private val appSettings = AppSettings.getInstance()
    private val phpProcessListener = PhpProcessListener(project)

    private val phpScriptRunConfiguration: PhpScriptRunConfiguration = PhpScriptRunConfiguration(
        project,
        PhpScriptRuntimeConfigurationProducer().configurationFactory,
        "Symfony Console"
    )
    private val phpInterpreter: PhpInterpreter? = PhpProjectConfigurationFacade.getInstance(project).interpreter

    private val symfonyRootPath: String
        get() {
            if (projectSettings.customSymfonyPath.isNotBlank() && projectSettings.customSymfonyPath.isNotEmpty()) {
                thisLogger().debug("Set up custom symfony webroot path: ${projectSettings.customSymfonyPath}")
                return projectSettings.customSymfonyPath
            } else { // Default path
                return ComposerUtils.findFileInProject(project, ComposerUtils.CONFIG_DEFAULT_FILENAME)
                    .parent
                    .path
            }
        }

    private val vendorPath: String
        get() {
            // Set a custom vendor path if is set in project settings
            if (projectSettings.customVendorPath.isNotBlank() && projectSettings.customVendorPath.isNotEmpty()) {
                thisLogger().debug("Set up custom vendor path: ${projectSettings.customVendorPath}")
                return projectSettings.customVendorPath
            } else {
                return "$symfonyRootPath/vendor"
            }
        }


    private val phpPsyShConsoleRunCode: String
        get() {
            val fileStream = javaClass.classLoader.getResourceAsStream("scripts/PsyShConsoleRun.php")
            return fileStream?.reader(Charsets.UTF_8)?.readText() ?: ""
        }

    private val phpCustomShellCode: String
        get() {
            val fileStream = javaClass.classLoader.getResourceAsStream("scripts/CustomShell.php")
            return fileStream?.reader(Charsets.UTF_8)?.readText() ?: ""
        }


    fun runScript(file: VirtualFile) {

        val document = FileDocumentManager.getInstance().getDocument(file)
        if (document === null) {
            thisLogger().debug("PHP document is null")
            return
        }
        val phpCode = document.text


        // If the code is empty or has only 1 line, do nothing
        if (phpCode.isEmpty() || document.lineCount == 1 || phpCode.trim() == "<?php" ) return

        thisLogger().debug("Run script...")
        thisLogger().debug("Symfony root path: $symfonyRootPath")
        thisLogger().debug("Vendor root path: $vendorPath")

        // Check PHP interpreter
        if (phpInterpreter == null) {
            NotificationUtils.noPhpInterpreter(project)
        }

        // Check the symfony webroot index.php file
        val symfonyWebRootIndexPathFile = File("$symfonyRootPath/public/index.php")
        if (!symfonyWebRootIndexPathFile.exists() || !symfonyWebRootIndexPathFile.isFile) {
            NotificationUtils.symfonyWebRootIndexNotExist(project, symfonyWebRootIndexPathFile.path)
            return
        }

        // Check the symfony in a project vendor root path
        val symfonyVendorPathFile = File("$vendorPath/symfony")
        if (!symfonyVendorPathFile.exists() || !symfonyVendorPathFile.isDirectory) {
            NotificationUtils.symfonyVendorDirectoryNotFound(project, vendorPath)
            return
        }

        // Check if PHS scripts are present
        if (phpPsyShConsoleRunCode.isEmpty() or phpCustomShellCode.isEmpty()) {
            thisLogger().error("Php scripts problem: PsySH script state: ${phpPsyShConsoleRunCode.isEmpty()}. Custom Shell script state: ${phpCustomShellCode.isEmpty()}")
            return
        }

        thisLogger().debug("Symfony root path final: $symfonyRootPath")
        thisLogger().debug("Vendor root path final: $vendorPath")


        var processHandler: ProcessHandler
        try {

            // Create php command settings
            val phpCommandSettings = cratePhpCommandSettings()
            phpCommandSettings.addArguments(
                listOf(
                    "-r",
                    phpPsyShConsoleRunCode,
                    phpCode,
                    projectSettings.asJsonString(),
                    appSettings.asJsonString(),
                    phpCustomShellCode
                )
            )

            // Create a process handler with ANSI output (e.g: \u001b[31m = red text)
            processHandler = getAnsiUnfilteredProcessHandler(
                phpScriptRunConfiguration.createProcessHandler(
                    project,
                    phpCommandSettings
                )
            )

            // Attach termination listener to the process handler
            ProcessTerminatedListener.attach(processHandler, project, "")

        } catch (ex: Exception) {
            thisLogger().error("Php interpreter error: ${ex.message}")
            NotificationUtils.phpInterpreterError(project, ex.message)
            return
        }

        // Attach php process listener
        processHandler.addProcessListener(phpProcessListener)

        // Show the output tool window
        toolWindowManager.getToolWindow("SymfonyTinkerWindow")?.run {
            this.activate(null)
        }

        // Reset output
        project.getSymfonyTinkerToolWindow()?.resetOutput()

        // Run the process in the background
        progressManager.run(createBackgroundTask(processHandler))

    }


    /**
     * Create a background task to run the process.
     */
    private fun createBackgroundTask(processHandler: ProcessHandler): Backgroundable {
        return object : Backgroundable(project, SymfonyTinkerBundle.message("st.console.running")) {
            override fun run(progressIndicator: ProgressIndicator) {
                processHandler.startNotify()
                processHandler.processInput?.writer()?.write("\u0004")
                while (!processHandler.isProcessTerminated) {
                    Thread.sleep(250)
                    try {
                        progressIndicator.checkCanceled()
                    } catch (ex: ProcessCanceledException) {
                        processHandler.destroyProcess()
                        throw ex
                    }
                }
            }
        }
    }

    /**
     * Create settings for PHP command.
     */
    private fun cratePhpCommandSettings(): PhpCommandSettings {
        val phpCommandSettings = PhpCommandSettingsBuilder(project, phpInterpreter!!).loadAndStartDebug().build()
        phpCommandSettings.apply {
            this.setWorkingDir(symfonyRootPath)
            this.importCommandLineSettings(
                phpScriptRunConfiguration.settings.commandLineSettings,
                symfonyRootPath
            )
        }

        return phpCommandSettings
    }

    /**
     * Get unfiltered process handler.
     * This is needed to prevent the IDE from filtering out the output.
     */
    private fun getAnsiUnfilteredProcessHandler(processHandler: ProcessHandler): ProcessHandler {
        if (processHandler is OSProcessHandler) {
            //preservation of ANSI colors in the output
            //correct UTF-8 decoding
            //possibility to "kill" the process (not just detach it)
            return KillableProcessHandler(processHandler.process, processHandler.commandLine, StandardCharsets.UTF_8)
        }

        if (processHandler is BaseRemoteProcessHandler<*>) {
            return BaseRemoteProcessHandler(processHandler.process, processHandler.commandLine, StandardCharsets.UTF_8)
        }

        // fallback
        return processHandler
    }
}
