package sk.kamikx.symfonytinker

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object SymfonyTinkerIcons {

    @JvmField
    val ToolWindowIcon: Icon = load("/icons/toolWindowIcon.svg")

    @JvmStatic
    fun load(path: String): Icon {
        return IconLoader.getIcon(path, SymfonyTinkerIcons::class.java)
    }
}