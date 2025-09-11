package sk.kamikx.symfonytinker.toolWindow

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.jcef.JBCefBrowser
import sk.kamikx.symfonytinker.SymfonyTinkerBundle
import sk.kamikx.symfonytinker.settings.AppSettings
import java.awt.event.ActionEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.swing.Timer
import sk.kamikx.symfonytinker.utils.SupportingUtils

class SymfonyTinkerToolWindow(private val toolWindow: ToolWindow) : SimpleToolWindowPanel(
    true,
    true
) {

    companion object {
        const val FONT_PT_ADJUSTER = 3
    }
    private val appSettings = AppSettings.getInstance()
    private val outputBrowser = JBCefBrowser()
    private var outputText: String = ""
    private var outputTime: String = ""
    private var timerRunning = false

    init {
        apply {
            add(JBScrollPane(outputBrowser.component).apply {
                verticalScrollBar.unitIncrement = 1
                horizontalScrollBar.unitIncrement = 1
            })
        }

    }

    private fun delayedUpdate(@Suppress("SameParameterValue") delayMs: Int, callback: () -> Unit) {
        val timer = Timer(delayMs) { e: ActionEvent? ->
            callback()
        }
        timer.isRepeats = false
        timer.start()
    }


    fun addOutput(output: String) {
        thisLogger().debug("Add output: $output")
        outputText += output

        if (!timerRunning) {
            timerRunning = true
            delayedUpdate(250, {
                updateView()
                timerRunning = false
            })
        }
    }


    fun clearOutput() {
        thisLogger().debug("Clear output")
        toolWindow.title = "Output"
        outputTime = ""
        outputText = ""
        toolWindow.show()
        this.outputBrowser.loadHTML("")
    }

    fun resetOutput() {
        thisLogger().debug("Reset output")
        toolWindow.title = "Output"
        outputTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        outputText = ""

        toolWindow.show()
        updateView()
    }

    private fun updateView() {

        thisLogger().debug("Update view")
        thisLogger().debug("Output text: $outputTime")

        val globalScheme = EditorColorsManager.getInstance().globalScheme
        val timeString =
            if (appSettings.showExecutionStarted)
                SymfonyTinkerBundle.message("st.console.executionStartedAt", outputTime)
            else
                ""

        this.outputBrowser.loadHTML(
            """
            <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        .output {
                          background-color: #18171B;
                        }
                        #end-execution,
                        #start-execution {
                            background-color: #18171B;
                            color: #56DB3A;
                            font-family: '${globalScheme.editorFontName}';
                            font-size: ${globalScheme.editorFontSize - FONT_PT_ADJUSTER}pt;
                        }
                        
                        #support-me {
                            margin-top: 10px;
                            background-color: #18171B;
                            color: #56DB3A;
                            font-family: '${globalScheme.editorFontName}';
                            font-size: ${globalScheme.editorFontSize - FONT_PT_ADJUSTER}pt;
                        }
                        
                        #support-me a {
                            color: #FF8400;
                        }
                    </style>
      
                </head>
                <body>
                     $timeString
                    <div class="output">
                           ${outputText.ifEmpty { "" }}
                    </div>
                    
                    ${SupportingUtils.getMessage()}
                    
                </body>
            </html>
            """
        )
    }

}