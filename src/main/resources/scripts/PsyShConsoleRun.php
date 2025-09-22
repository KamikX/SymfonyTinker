/*<?php /**/

use App\Kernel;
use Psy\Configuration;
use Psy\ExecutionLoopClosure;
use Psy\Shell;
use scripts\CustomShell;
use Symfony\Component\Console\Output\ConsoleOutput;
use Symfony\Component\Dotenv\Dotenv;
use Symfony\Component\VarDumper\Cloner\Stub;
use Symfony\Component\VarDumper\Cloner\VarCloner;
use Symfony\Component\VarDumper\Dumper\AbstractDumper;
use Symfony\Component\VarDumper\Dumper\CliDumper;
use Symfony\Component\VarDumper\Dumper\HtmlDumper;
use Symfony\Bundle\FrameworkBundle\Console\Application;
use Symfony\Component\Console\Input\ArrayInput;
use Symfony\Component\Console\Output\BufferedOutput;


echo ">>START-OUTPUT<<";

$kernel = null;

// Get settings
$projectSettings = json_decode($argv[2]) ?? new stdClass();
$appSettings = json_decode($argv[3]) ?? new stdClass();

$composerJsonFile = ($projectSettings->vendorRoot ?: __DIR__) . '/composer.json';
if (!file_exists($composerJsonFile)) {
    throw new Exception($composerJsonFile . ' does not exist. Please check your project settings.');
}
$psyshComposerJsonFile = ($projectSettings->vendorRoot ?: __DIR__) . '/vendor/psy/psysh/composer.json';
if (!file_exists($psyshComposerJsonFile)) {
    throw new Exception($psyshComposerJsonFile . ' does not exist. Make sure your composer dependencies are installed and psy/psysh is required.');
}

$isSymfonyProject = file_exists(($projectSettings->vendorRoot ?: __DIR__) . '/vendor/symfony/framework-bundle/composer.json');
if ($isSymfonyProject) {
    define('SYMFONY_START', microtime(true));
}

require ($projectSettings->vendorRoot ?: __DIR__) . '/vendor/autoload.php';


//Load custom shell class
$customShellCode = $argv[4] ?? null;
eval($customShellCode);


// NOTE: not used
// Load Class Alias Autoloader
//$classAliasAutoloader = $argv[4] ?? null;
//eval($classAliasAutoloader);


try {
    (new Dotenv())->bootEnv($projectSettings->symfonyRoot ?: __DIR__.'/.env');
    if ($isSymfonyProject) {
        $kernel = new Kernel($_SERVER['APP_ENV'], (bool) $_SERVER['APP_DEBUG']);
        $kernel->boot();
    }
} catch (\Throwable $e) {
    throw new Exception("Symfony kernel initialization failed. Error message {$e->getMessage()}");
}

// Shell config
$config = new Configuration([
    'updateCheck' => 'never',
    'usePcntl' => false,
    'useReadline' => false,
    'prompt' => '',
    'theme' => 'compact',
]);
if (method_exists($config, 'setRawOutput')) {
    $config->setRawOutput(true);
}
if (method_exists($config, 'setInteractiveMode')) {
    $config->setInteractiveMode(Configuration::INTERACTIVE_MODE_DISABLED);
}
$config->setColorMode(Configuration::COLOR_MODE_FORCED);
$config->setHistoryFile(defined('PHP_WINDOWS_VERSION_BUILD') ? 'nul' : '/dev/null');


$shell = new CustomShell($config);
$shell->setVarDumperEnabled($appSettings->varDumperEnabled ?? true);

// Set scope variables
$container = $kernel->getContainer();
$doctrine = $container->get('doctrine');
$parameters = $container->getParameterBag()->all();
$em = $container->get('doctrine.orm.entity_manager');
$router = $container->get('router');

$runConsole = function($command): string {
    global $kernel;
    $application = new Application($kernel);
    $command = $application->find($command);
    $input = new ArrayInput([]);
    $output = new BufferedOutput();
    $command->run($input, $output);
    return $output->fetch();
};


$shell->setScopeVariables([
    'kernel' => $kernel,
    'container' => $container,
    'doctrine' => $doctrine,
    'em' => $em,
    'router' => $router,
    'parameters' => $parameters,
    'runConsole' => $runConsole,
]);



$output = new ConsoleOutput();
$shell->setOutput($output);
$closure = new ExecutionLoopClosure($shell);

// NOTE: this is not needed anymore, because we use power of IDE: full access to  Symfony classes—backed by autocompletion, syntax highlighting, and error checks
// Register class alias autoloader
//$autoloadClassMap = ($projectSettings->vendorRoot ?: __DIR__) . '/vendor/composer/autoload_classmap.php';
//$loader = CustomClassAliasAutoloader::register($shell, $autoloadClassMap);


$unsanitizedRunCode = token_get_all($argv[1]);
$sanitizedRunCode = '';
foreach ($unsanitizedRunCode as $token) {
    if (!is_string($token)) {
        list($id, $token) = $token;
        if (in_array($id, [T_COMMENT, T_DOC_COMMENT, T_OPEN_TAG, T_OPEN_TAG_WITH_ECHO, T_CLOSE_TAG], true)) {
            continue;
        }
    }

    $sanitizedRunCode .= $token;
}

$shell->addInput($sanitizedRunCode, true);
if ($isSymfonyProject && $projectSettings->terminateApp) {
    $shell->addInput('echo ">>END-OUTPUT<<";', true);
    $shell->addInput('$kernel->shutdown();', true);
}
$shell->addInput('usleep(250000); throw new \Psy\Exception\BreakException(">>END-OUTPUT<<");', true);
$closure->execute();

if (isset($loader)) {
    $loader->unregister();
}
