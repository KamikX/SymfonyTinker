package sk.kamikx.symfonytinker.settings

data class AppSettingsState(
    @JvmField var showExecutionStarted: Boolean = true,
    @JvmField var showExecutionEnded: Boolean = true,
    @JvmField var dynamicGutterRunIcon: Boolean = true,
    @JvmField var runningCounter: Int = 0
)

