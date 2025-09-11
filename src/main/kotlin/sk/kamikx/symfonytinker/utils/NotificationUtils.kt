package sk.kamikx.symfonytinker.utils

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import sk.kamikx.symfonytinker.SymfonyTinkerBundle
import sk.kamikx.symfonytinker.action.notification.EditPhpInterpreterSettingsAction
import sk.kamikx.symfonytinker.action.notification.EditProjectSettingsAction

object NotificationUtils {

    fun info(project: Project?, title: String, content: String) {
        makeNotification(title, content, NotificationType.INFORMATION).notify(project)
    }

    fun warning(project: Project?, title: String, content: String) {
        makeNotification(title, content, NotificationType.WARNING).notify(project)
    }

    fun error(project: Project?, title: String, content: String) {
        makeNotification(title, content, NotificationType.ERROR).notify(project)
    }

    fun noPhpInterpreter(project: Project?) {
        makeNotification(
            title = SymfonyTinkerBundle.message("st.notification.error.noPhpInterpreter.title"),
            type = NotificationType.ERROR,
            actions = listOf(EditPhpInterpreterSettingsAction())
        ).notify(project)
    }

    fun phpInterpreterError(project: Project?, message: String?) {
        makeNotification(
            title = SymfonyTinkerBundle.message("st.notification.error.phpInterpreterError.title"),
            content = message ?: SymfonyTinkerBundle.message(
                "st.notification.error.phpInterpreterError.message",
            ),
            type = NotificationType.ERROR,
            actions = listOf(EditProjectSettingsAction())
        ).notify(project)
    }


    fun symfonyWebRootIndexNotExist(project: Project?, symfonyRootPath: String) {
        makeNotification(
            title = SymfonyTinkerBundle.message("st.notification.error.symfonyWebRootIndexNotExist.title"),
            content = SymfonyTinkerBundle.message(
                "st.notification.error.symfonyWebRootIndexNotExist.message",
                symfonyRootPath
            ),
            type = NotificationType.ERROR,
            actions = listOf(EditProjectSettingsAction())
        ).notify(project)
    }


    fun symfonyVendorDirectoryNotFound(project: Project?, symfonyVendorPath: String) {
        makeNotification(
            title = SymfonyTinkerBundle.message("st.notification.error.vendorDirectoryNotFound.title"),
            content = SymfonyTinkerBundle.message(
                "st.notification.error.vendorDirectoryNotFound.message",
                symfonyVendorPath
            ),
            type = NotificationType.ERROR,
            actions = listOf(EditProjectSettingsAction())
        ).notify(project)
    }

    private fun makeNotification(
        title: String,
        content: String = "",
        type: NotificationType,
        actions: List<NotificationAction> = emptyList()
    ): Notification {
        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup("SymfonyTinker")
            .createNotification(title, content, type)

        actions.forEach {
            notification.addAction(it)
        }

        return notification
    }

}