package sk.kamikx.symfonytinker.listeners

import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.jetbrains.php.composer.ComposerUtils
import sk.kamikx.symfonytinker.settings.AppSettings
import sk.kamikx.symfonytinker.utils.getSymfonyTinkerToolWindow
import sk.kamikx.symfonytinker.SymfonyTinkerBundle
import sk.kamikx.symfonytinker.settings.ProjectSettings
import sk.kamikx.symfonytinker.utils.SupportingUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PhpProcessListener(private val project: Project) : ProcessListener {

    companion object {
        private const val OUTPUT_START_SEQUENCE = ">>START-OUTPUT<<"
        private const val OUTPUT_END_SEQUENCE = ">>END-OUTPUT<<"

        val SRC_REGEX = Regex("""src="/([^"]+)"""")
        val LINK_HREF_REGEX =  Regex("""<link\b[^>]*\bhref="/([^"]+)"""")
    }

    private var processOutput = ""
    private var capturing = false
    private val projectSettings = ProjectSettings.getInstance(project)

    private val symfonyRootPath: String
        get() {
            return if (projectSettings.customSymfonyPath.isNotBlank() && projectSettings.customSymfonyPath.isNotEmpty()) {
                projectSettings.customSymfonyPath
            } else { // Default path
                ComposerUtils.findFileInProject(project, ComposerUtils.CONFIG_DEFAULT_FILENAME)
                    .parent
                    .path
            }
        }


    override fun startNotified(event: ProcessEvent) {
        thisLogger().debug("startNotified")
    }

    override fun processTerminated(event: ProcessEvent) {
        thisLogger().debug("Process terminated")
        // Increment supporting counter
        SupportingUtils.increment()
        ApplicationManager.getApplication().invokeLater(
            {

                val appSettings = AppSettings.getInstance()

                // Raw output replaces file paths
                // JBCefBrowser need file:///ABSOLUTE_PATH/symfony_black_03.png
                if (!appSettings.varDumperEnabled) {

                    // Replace a src path with an absolute path
                    processOutput = SRC_REGEX.replace(processOutput) { matchResult ->
                        val path = matchResult.groupValues[1]
                        """src="${symfonyRootPath}/${path}""""
                    }

                    // Replace a link href path with an absolute path
                    processOutput = LINK_HREF_REGEX.replace(processOutput) { matchResult ->
                        val path = matchResult.groupValues[1]
                        matchResult.value.replace("""href="/$path""", """href="${symfonyRootPath}/$path"""")
                    }
                }

                project.getSymfonyTinkerToolWindow()
                    ?.addOutput(processOutput)

                if (appSettings.showExecutionEnded) {
                    val endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    project.getSymfonyTinkerToolWindow()
                        ?.addOutput(SymfonyTinkerBundle.message("st.console.executionFinishedAt",endTime))
                }

                // Reset capturing text
                processOutput = ""
            }, ModalityState.nonModal()
        )
    }

    override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
        //thisLogger().debug("onTextAvailable: ${event.text}")

        var capText = event.text

        // Start capturing
        if (!capturing && capText.contains(OUTPUT_START_SEQUENCE)) {
            capText = capText.substring(
                capText.indexOf(OUTPUT_START_SEQUENCE) + OUTPUT_START_SEQUENCE.length
            )
            capturing = true
        }

        // Stop capturing
        if (capturing && capText.contains(OUTPUT_END_SEQUENCE)) {
            capturing = false
        }

        if (capturing) {
            processOutput += capText
        }
    }
}
