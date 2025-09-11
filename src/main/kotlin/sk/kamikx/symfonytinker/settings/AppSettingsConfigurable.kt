package sk.kamikx.symfonytinker.settings

import com.intellij.openapi.options.BoundSearchableConfigurable
import com.intellij.openapi.ui.DialogPanel
import sk.kamikx.symfonytinker.SymfonyTinkerBundle
import com.intellij.ui.dsl.builder.*

class AppSettingsConfigurable() : BoundSearchableConfigurable(
    displayName = SymfonyTinkerBundle.message("st.app.settings.displayName"),
    helpTopic = SymfonyTinkerBundle.message("st.app.settings.helpTopic")
) {

    override fun createPanel(): DialogPanel {

        val settings = AppSettings.getInstance()

        return panel {

            group(SymfonyTinkerBundle.message("st.app.settings.outputSettings")) {

                row {
                    checkBox(SymfonyTinkerBundle.message("st.app.settings.showExecutionStart"))
                        .bindSelected(settings::showExecutionStarted)
                }
                row {
                    checkBox(SymfonyTinkerBundle.message("st.app.settings.showExecutionEnd"))
                        .bindSelected(settings::showExecutionEnded)
                }
                row {
                    checkBox(SymfonyTinkerBundle.message("st.app.settings.dynamicGutterRunIcon"))
                        .bindSelected(settings::dynamicGutterRunIcon)
                }
            }
        }
    }
}