package sk.kamikx.symfonytinker.utils

import sk.kamikx.symfonytinker.SymfonyTinkerBundle
import sk.kamikx.symfonytinker.settings.AppSettings


object SupportingUtils {

    const val SUPPORT_THRESHOLD = 5

    fun increment() {
        AppSettings.getInstance().runningCounter += 1
    }

    fun reset() {
        AppSettings.getInstance().runningCounter = 0
    }

    fun getMessage(): String {
        if (AppSettings.getInstance().runningCounter >= SUPPORT_THRESHOLD) {
            reset()
            return SymfonyTinkerBundle.message("st.supporting.message")
        }
        return ""
    }

}