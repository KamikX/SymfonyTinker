package sk.kamikx.symfonytinker.types

import com.intellij.openapi.fileTypes.LanguageFileType
import com.jetbrains.php.lang.PhpLanguage
import sk.kamikx.symfonytinker.SymfonyTinkerBundle
import org.jetbrains.annotations.Nls
import sk.kamikx.symfonytinker.SymfonyTinkerIcons
import javax.swing.Icon


object SymfonyTinkerConsoleType : LanguageFileType(PhpLanguage.INSTANCE) {

    override fun getName(): String = "SymfonyTinkerConsoleType"

    override fun getDescription(): String =
        SymfonyTinkerBundle.message("st.console.description")

    override fun getDefaultExtension(): String = "st.php"

    override fun getIcon(): Icon = SymfonyTinkerIcons.ToolWindowIcon

    override fun getDisplayName(): @Nls String =
        SymfonyTinkerBundle.message("st.console.name")
}
