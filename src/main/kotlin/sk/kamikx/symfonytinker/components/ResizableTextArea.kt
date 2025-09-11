package sk.kamikx.symfonytinker.components

import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JPanel
import com.intellij.ui.LanguageTextField
import com.jetbrains.php.lang.PhpLanguage
import com.intellij.openapi.project.Project


class ResizableTextArea(project: Project) : JPanel(BorderLayout()) {

    //val codeArea = JBTextArea(5, 40)
    // NOTE: project must be used here, otherwise error is raised:
    //Light files should have PSI only in one project, existing=com.jetbrains.php.lang.PhpFileViewProvider{vFile=LightVirtualFile: /Dummy.php, content=VirtualFileContent{size=0}, eventSystemEnabled=true} in ProjectDefault (Template) Project, requested in Project(name=psysh-dev-project, containerState=COMPONENT_CREATED, componentStore=/Users/kamil/Documents/Projects/PHP/Symfony-PsySH-IdeaPlugin/psysh-dev-project); psiFiles: class com.jetbrains.php.lang.psi.PhpFileImpl [Language: PHP], class com.intellij.psi.impl.source.html.HtmlFileImpl [Language: HTML]
    //java.lang.Throwable: Light files should have PSI only in one project, existing=com.jetbrains.php.lang.PhpFileViewProvider{vFile=LightVirtualFile: /Dummy.php
    val codeArea = LanguageTextField(PhpLanguage.INSTANCE, project, "", false)

    init {
        // codeArea.lineWrap = true
        // codeArea.wrapStyleWord = true
        val lineHeight = codeArea.editor?.lineHeight ?: 16
        codeArea.preferredSize = Dimension(300, lineHeight * 10)

        //val scrollPane = JBScrollPane(codeArea)
        val scrollPane = JBScrollPane(codeArea)
        add(scrollPane, BorderLayout.CENTER)

        val resizer = object : JComponent() {
            init {
                cursor = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR)
                preferredSize = Dimension(0, 8)

            }
        }

        val resizeListener = object : MouseAdapter() {
            private var startY = 0
            private var startHeight = 0

            override fun mousePressed(e: MouseEvent) {
                startY = e.yOnScreen
                startHeight = height
            }

            override fun mouseDragged(e: MouseEvent) {
                val dy = e.yOnScreen - startY
                preferredSize = Dimension(width, startHeight + dy) // only height
                revalidate()
            }
        }

        resizer.addMouseListener(resizeListener)
        resizer.addMouseMotionListener(resizeListener)

        add(resizer, BorderLayout.SOUTH)
    }
}
