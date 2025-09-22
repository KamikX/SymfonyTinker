package sk.kamikx.symfonytinker.settings

import com.google.gson.JsonObject
import com.intellij.openapi.components.*
import com.intellij.openapi.components.Service

@Service(Service.Level.APP)
@State(name = "SymfonyTinkerAppSettingsState", storages = [(Storage("symfony-tinker-app-settings.xml"))])
class AppSettings() : SerializablePersistentStateComponent<AppSettingsState>(AppSettingsState()) {

    var showExecutionStarted
        get() = state.showExecutionStarted
        set(value) {
            updateState {
                it.copy(showExecutionStarted = value)
            }
        }

    var showExecutionEnded
        get() = state.showExecutionEnded
        set(value) {
            updateState {
                it.copy(showExecutionEnded = value)
            }
        }

    var dynamicGutterRunIcon
        get() = state.dynamicGutterRunIcon
        set(value) {
            updateState {
                it.copy(dynamicGutterRunIcon = value)
            }
        }

    var runningCounter
        get() = state.runningCounter
        set(value) {
            updateState {
                it.copy(runningCounter = value)
            }
        }


    var varDumperEnabled
        get() = state.varDumperEnabled
        set(value) {
            updateState {
                it.copy(varDumperEnabled = value)
            }
        }

    companion object {
        @JvmStatic
        fun getInstance(): AppSettings = service()
    }

    fun asJsonString(): String {
        val projectSettingsObject = JsonObject()
        projectSettingsObject.addProperty("varDumperEnabled", this.varDumperEnabled)
        return projectSettingsObject.toString()
    }

}