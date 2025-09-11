package sk.kamikx.symfonytinker

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object SymfonyTinkerIcons {

    @JvmField
    val ToolWindowIcon: Icon = load("/icons/toolWindowIcon.svg")
    @JvmField
    val RunIcon: Icon = load("/icons/runIcon.svg")

    @JvmStatic
    fun load(path: String): Icon {
        return IconLoader.getIcon(path, SymfonyTinkerIcons::class.java)
    }
}