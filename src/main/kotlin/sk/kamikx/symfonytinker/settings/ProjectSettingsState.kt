package sk.kamikx.symfonytinker.settings

data class ProjectSettingsState (
    @JvmField var customSymfonyPath: String = "",
    @JvmField var customVendorPath: String = "",
    @JvmField var terminateApp: Boolean = false,
    @JvmField var defaultConsoleCode: String = "/**\n" +
            " * @var \\Doctrine\\ORM\\EntityManager ${'$'}em\n" +
            " * @var \\Doctrine\\Bundle\\DoctrineBundle\\Registry ${'$'}doctrine\n" +
            " */",

    @JvmField var filesDirectoryName: String = ".symfony-tinker",
)