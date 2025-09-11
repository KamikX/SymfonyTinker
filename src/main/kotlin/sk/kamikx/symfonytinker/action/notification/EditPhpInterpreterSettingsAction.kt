package sk.kamikx.symfonytinker.action.notification

import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.jetbrains.php.config.PhpProjectConfigurable
import sk.kamikx.symfonytinker.SymfonyTinkerBundle


class EditPhpInterpreterSettingsAction :
    NotificationAction(SymfonyTinkerBundle.message("st.notification.action.phpInterpreter.title")) {

    override fun actionPerformed(e: AnActionEvent, notification: Notification) {
        e.project?.let {
            ShowSettingsUtil.getInstance().showSettingsDialog(it, PhpProjectConfigurable::class.java)
        }
    }
}
