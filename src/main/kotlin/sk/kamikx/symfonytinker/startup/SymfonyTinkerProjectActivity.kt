package sk.kamikx.symfonytinker.startup

import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import sk.kamikx.symfonytinker.services.DynamicGutterService

class SymfonyTinkerProjectActivity : ProjectActivity {


    override suspend fun execute(project: Project) {

        // Init a dynamic gutter service
        val dynamicGutterService = project.getService(DynamicGutterService::class.java)
    }

}