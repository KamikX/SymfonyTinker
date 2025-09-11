package sk.kamikx.symfonytinker.action.notification

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import sk.kamikx.symfonytinker.SymfonyTinkerBundle
import sk.kamikx.symfonytinker.settings.ProjectSettingsConfigurable


class EditProjectSettingsAction :
    NotificationAction(SymfonyTinkerBundle.message("st.notification.action.projectSetting.title")) {

    override fun actionPerformed(e: AnActionEvent, notification: Notification) {
        e.project?.let {
            ShowSettingsUtil.getInstance().showSettingsDialog(it, ProjectSettingsConfigurable::class.java)
        }
    }
}
