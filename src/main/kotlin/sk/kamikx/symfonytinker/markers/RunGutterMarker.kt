package sk.kamikx.symfonytinker.markers

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.*
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.util.Key
import sk.kamikx.symfonytinker.SymfonyTinkerBundle
import sk.kamikx.symfonytinker.SymfonyTinkerIcons
import sk.kamikx.symfonytinker.action.RunConsoleAction
import sk.kamikx.symfonytinker.settings.AppSettings

class RunGutterMarker(private val editor: Editor) {


    private val appSettings = AppSettings.getInstance()
    private var currentHighlighter: RangeHighlighter? = null
    private val caretListener = object : CaretListener {
        override fun caretPositionChanged(event: CaretEvent) {
            updateMarker()
        }
    }

    init {
        editor.caretModel.addCaretListener(caretListener)

        if (appSettings.dynamicGutterRunIcon) {
            updateMarker()
        } else {
            addMarker(1)
        }
    }

    private fun updateMarker() {

        if (!appSettings.dynamicGutterRunIcon) {
            addMarker(1)
        } else {
            addMarker()
        }
    }

    fun addMarker(customLineNumber: Int? = null) {

        thisLogger().debug("Add marker {$customLineNumber}")
        val document = editor.document
        val caretOffset = editor.caretModel.offset
        var lineNumber = document.getLineNumber(caretOffset)
        if (customLineNumber != null) {
            lineNumber = customLineNumber
        }

        val lineStart = document.getLineStartOffset(lineNumber)
        val lineEnd = document.getLineEndOffset(lineNumber)

        // Remove old
        currentHighlighter?.let {
            editor.markupModel.removeHighlighter(it)
        }

        // Add new
        val markupModel = editor.markupModel
        currentHighlighter = markupModel.addRangeHighlighter(
            lineStart,
            lineEnd,
            HighlighterLayer.CARET_ROW,
            null,                     // TextAttributes
            HighlighterTargetArea.LINES_IN_RANGE
        ).apply {
            gutterIconRenderer = object : GutterIconRenderer() {

                override fun getIcon() = SymfonyTinkerIcons.RunIcon
                override fun getClickAction(): AnAction = RunConsoleAction()
                override fun getTooltipText() = SymfonyTinkerBundle.message("st.console.run")
                override fun equals(other: Any?) = false
                override fun hashCode() = 0
                override fun isNavigateAction(): Boolean = true
            }
        }
    }

    fun dispose() {
        currentHighlighter?.let { editor.markupModel.removeHighlighter(it) }
        editor.caretModel.removeCaretListener(caretListener)
        currentHighlighter = null
    }

    companion object {

        val KEY = Key.create<RunGutterMarker>("RunCaretGutterMarker")

        /**
         * Utility function to remove the marker when the file is closed.
         */
        fun removeMarker(editor: Editor?) {
            editor ?: return
            editor.getUserData(KEY)?.dispose()
            editor.putUserData(KEY, null)
        }

    }
}
