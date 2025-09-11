package sk.kamikx.symfonytinker.settings

import com.google.gson.JsonObject
import com.intellij.openapi.components.*
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
@State(name = "SymfonyTinkerProjectSettings", storages = [(Storage("symfony-tinker-project-settings.xml"))])
class ProjectSettings(internal val project: Project) : SerializablePersistentStateComponent<ProjectSettingsState>(ProjectSettingsState()) {

    var customSymfonyPath
        get() = state.customSymfonyPath
        set(value) {
            updateState {
                it.copy(customSymfonyPath = value)
            }
        }

    var customVendorPath
        get() = state.customVendorPath
        set(value) {
            updateState {
                it.copy(customVendorPath = value)
            }
        }

    var terminateApp
        get() = state.terminateApp
        set(value) {
            updateState {
                it.copy(terminateApp = value)
            }
        }

    var filesDirectoryName
        get() = state.filesDirectoryName
        set(value) {
            updateState {
                it.copy(filesDirectoryName = value)
            }
        }

    var defaultConsoleCode
        get() = state.defaultConsoleCode
        set(value) {
            updateState {
                it.copy(defaultConsoleCode = value)
            }
        }


    companion object {
        @JvmStatic
        fun getInstance(project: Project): ProjectSettings = project.service()
    }

    fun asJsonString(): String {
        val projectSettingsObject = JsonObject()
        projectSettingsObject.addProperty("symfonyRoot", this.customSymfonyPath)
        projectSettingsObject.addProperty("vendorRoot", this.customVendorPath)
        projectSettingsObject.addProperty("filesDirectoryName", this.filesDirectoryName)
        projectSettingsObject.addProperty("terminateApp", this.terminateApp)
        return projectSettingsObject.toString()

    }
}

