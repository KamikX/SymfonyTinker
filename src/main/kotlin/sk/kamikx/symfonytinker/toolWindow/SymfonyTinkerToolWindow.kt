package sk.kamikx.symfonytinker.toolWindow

import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.ex.CheckboxAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.ui.SimpleToolWindowPanel
import com.intellij.openapi.util.Disposer.isDebugMode
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.components.JBScrollPane
import sk.kamikx.symfonytinker.SymfonyTinkerBundle
import sk.kamikx.symfonytinker.settings.AppSettings
import java.awt.event.ActionEvent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.swing.Timer
import sk.kamikx.symfonytinker.utils.SupportingUtils
import com.intellij.ui.jcef.JBCefApp
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.ui.jcef.JBCefBrowserBuilder
import java.awt.BorderLayout
import javax.swing.JPanel


class SymfonyTinkerToolWindow(private val toolWindow: ToolWindow) : SimpleToolWindowPanel(
    true,
    true
) {

    companion object {
        const val FONT_PT_ADJUSTER = 3
    }
    private val appSettings = AppSettings.getInstance()
    private val ourCefClient = JBCefApp.getInstance().createClient()
    // TODO:  isDebugMode brat s nastaveni aplikacie
    private val outputBrowser: JBCefBrowser = JBCefBrowserBuilder()
        .setClient(ourCefClient)
        .setEnableOpenDevToolsMenuItem(isDebugMode()).build()
    private var outputText: String = ""
    private var outputTime: String = ""
    private var timerRunning = false

    init {

        toolbar = createToolbar()
        apply {
            add(JBScrollPane(outputBrowser.component).apply {
                verticalScrollBar.unitIncrement = 1
                horizontalScrollBar.unitIncrement = 1
            })
        }

    }

    private fun createToolbar(): JPanel {
        val actions =  DefaultActionGroup()

        val toggleAction = object : CheckboxAction(SymfonyTinkerBundle.message("st.toolWindow.action.rawOutput")) {
            override fun isSelected(p0: AnActionEvent): Boolean {
                return !appSettings.varDumperEnabled
            }

            override fun setSelected(p0: AnActionEvent, p1: Boolean) {
                appSettings.varDumperEnabled = !p1
            }
            override fun getActionUpdateThread(): ActionUpdateThread {
                return ActionUpdateThread.EDT
            }
        }

        actions.add(toggleAction)
        val actionToolBar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actions, true)
        actionToolBar.targetComponent = this

        val panel = JPanel(BorderLayout())
        panel.add(actionToolBar.component, BorderLayout.EAST)

        return panel
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

//        if (!timerRunning) {
//            timerRunning = true
//            delayedUpdate(250, {
                updateView()
//                timerRunning = false
//            })
//        }
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
                        #output-wrapper {
                         background-color: #18171B;
                        }
                        #output {
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
                <body id="output-wrapper">
                     $timeString
                    <div id="output">
                           ${outputText.ifEmpty { "" }}
                    </div>
                    
                    ${SupportingUtils.getMessage()}
                    
                </body>
            </html>
            """
        )
    }

}