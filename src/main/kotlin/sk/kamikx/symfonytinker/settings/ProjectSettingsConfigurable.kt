package sk.kamikx.symfonytinker.settings

import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import sk.kamikx.symfonytinker.SymfonyTinkerBundle
import com.intellij.ui.dsl.builder.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import sk.kamikx.symfonytinker.components.ResizableTextArea


class ProjectSettingsConfigurable(internal val project: Project) : BoundSearchableConfigurable(
    displayName = SymfonyTinkerBundle.message("st.project.settings.displayName"),
    helpTopic = SymfonyTinkerBundle.message("st.project.settings.helpTopic")
) {

    override fun createPanel(): DialogPanel {

        val settings = ProjectSettings.getInstance(project)

        return panel {

            // Custom Symfony web root directory path
            row {
                label(SymfonyTinkerBundle.message("st.project.settings.customSymfonyWebRootDirectoryPath.label"))
            }
            row {
                val fileChooser = TextFieldWithBrowseButton()
                fileChooser.addBrowseFolderListener(
                    project, FileChooserDescriptorFactory.createSingleFolderDescriptor()
                )
                fileChooser.text = settings.customSymfonyPath
                cell(fileChooser)
                    .resizableColumn()
                    .align(Align.FILL)
                    .onApply {
                        settings.customSymfonyPath = fileChooser.text
                    }.onReset {
                        fileChooser.text = settings.customSymfonyPath
                    }.onIsModified {
                        fileChooser.text != settings.customSymfonyPath
                    }.comment(
                        SymfonyTinkerBundle.message("st.project.settings.customSymfonyWebRootDirectoryPath.comment")
                    )

            }

            // Custom Vendor directory path
            row {
                label(SymfonyTinkerBundle.message("st.project.settings.customVendorDirectoryPath.label"))
            }
            row {
                val fileChooser = TextFieldWithBrowseButton()
                fileChooser.addBrowseFolderListener(
                    project, FileChooserDescriptorFactory.createSingleFolderDescriptor()
                )
                fileChooser.text = settings.customVendorPath
                cell(fileChooser)
                    .resizableColumn()
                    .align(Align.FILL)
                    .onApply {
                        settings.customVendorPath = fileChooser.text
                    }.onReset {
                        fileChooser.text = settings.customVendorPath
                    }.onIsModified {
                        fileChooser.text != settings.customVendorPath
                    }.comment(
                        SymfonyTinkerBundle.message("st.project.settings.customVendorDirectoryPath.comment")
                    )

            }

            // Terminate app after execution
            row {
                checkBox(SymfonyTinkerBundle.message("st.project.settings.terminateApp"))
                    .bindSelected(settings::terminateApp)
            }

            // Working directory name
            row {
                label(SymfonyTinkerBundle.message("st.project.settings.filesDirectoryName.label"))
            }
            row {
                textField().bindText(settings::filesDirectoryName)
                    .comment(SymfonyTinkerBundle.message("st.project.settings.filesDirectoryName.comment"))
            }

            row {
                label(SymfonyTinkerBundle.message("st.project.settings.defaultConsoleCode.label"))
            }
            row {
                val resizableTextArea = ResizableTextArea(project)
                cell(resizableTextArea)
                    .align(Align.FILL)
                    .resizableColumn()
                    .onApply {
                        settings.defaultConsoleCode = resizableTextArea.codeArea.text
                    }.onReset {
                        resizableTextArea.codeArea.text = settings.defaultConsoleCode
                    }.onIsModified {
                        resizableTextArea.codeArea.text != settings.defaultConsoleCode
                    }.comment(SymfonyTinkerBundle.message("st.project.settings.defaultConsoleCode.comment"))
            }

        }
    }
}