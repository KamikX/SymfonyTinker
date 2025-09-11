package sk.kamikx.symfonytinker.listeners

import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import sk.kamikx.symfonytinker.settings.AppSettings
import sk.kamikx.symfonytinker.utils.getSymfonyTinkerToolWindow
import sk.kamikx.symfonytinker.SymfonyTinkerBundle
import sk.kamikx.symfonytinker.utils.SupportingUtils
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PhpProcessListener(private val project: Project) : ProcessListener {

    companion object {
        private const val OUTPUT_START_SEQUENCE = "%%START-OUTPUT%%"
        private const val OUTPUT_END_SEQUENCE = "%%END-OUTPUT%%"
    }

    private val processOutput = ArrayList<String>()
    private var capturing = false
    private var firstLine = true

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

                if (appSettings.showExecutionEnded) {
                    val endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                    project.getSymfonyTinkerToolWindow()
                        ?.addOutput(SymfonyTinkerBundle.message("st.console.executionFinishedAt",endTime))
                }
            }, ModalityState.nonModal()
        )
    }

    override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
        //thisLogger().debug("onTextAvailable: ${event.text}")
        if (firstLine) {
            firstLine = false
            return
        }

        var capText = event.text

        if (!capturing && capText.contains(OUTPUT_START_SEQUENCE)) {
            capText = capText.substring(
                capText.indexOf(OUTPUT_START_SEQUENCE) + OUTPUT_START_SEQUENCE.length
            )
            capturing = true
        }
        if (capturing && capText.contains(OUTPUT_END_SEQUENCE)) {
            capturing = false
        }

        if (capturing) {
            processOutput.add(capText)
            ApplicationManager.getApplication().invokeLater(
                {
                    project.getSymfonyTinkerToolWindow()?.addOutput(capText)
                }, ModalityState.nonModal()
            )
        }
    }
}
