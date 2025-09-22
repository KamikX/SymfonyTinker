namespace scripts;
/*<?php /**/

use Psy\Shell;

class CustomClassAliasAutoloader
{
    /**
     * The shell instance.
     *
     * @var \Psy\Shell
     */
    protected Shell $shell;

    /**
     * All of the discovered classes.
     *
     * @var array
     */
    protected array $classes = [];

    /**
     * Path to the vendor directory.
     *
     * @var string
     */
    protected string $vendorPath;

    /**
     * Explicitly included namespaces/classes.
     *
     * @var array
     */
    protected array $includedAliases;

    /**
     * Excluded namespaces/classes.
     *
     * @var array
     */
    protected array $excludedAliases;

    /**
     * Register a new alias loader instance.
     *
     * @param  \Psy\Shell  $shell
     * @param  string  $classMapPath
     * @param  array  $includedAliases
     * @param  array  $excludedAliases
     * @return static
     */
    public static function register(Shell $shell, string $classMapPath, array $includedAliases = [], array $excludedAliases = []): static
    {
        $loader = new static($shell, $classMapPath, $includedAliases, $excludedAliases);
        spl_autoload_register([$loader, 'aliasClass']);
        return $loader;

    }

    /**
     * Create a new alias loader instance.
     *
     * @param  \Psy\Shell  $shell
     * @param  string  $classMapPath
     * @param  array  $includedAliases
     * @param  array  $excludedAliases
     * @return void
     */
    public function __construct(Shell $shell, string $classMapPath, array $includedAliases = [], array $excludedAliases = [])
    {
        $this->shell = $shell;
        $this->vendorPath = dirname($classMapPath, 2);
        $this->includedAliases = $includedAliases;
        $this->excludedAliases = $excludedAliases;

        $classes = require $classMapPath;

        foreach ($classes as $class => $path) {
            if (! $this->isAliasable($class, $path)) {
                continue;
            }

            $name = $this->class_basename($class);

            if (! isset($this->classes[$name])) {
                $this->classes[$name] = $class;
            }
        }
    }

    /**
     * Find the closest class by name.
     *
     * @param  string  $class
     * @return void
     */
    public function aliasClass(string $class): void
    {
        if (str_contains($class, '\\')) {
            return;
        }

        $fullName = $this->classes[$class] ?? false;

        if ($fullName) {
            $this->shell->writeStdout("[!] Aliasing '{$class}' to '{$fullName}' for this session.\n");

            class_alias($fullName, $class);
        }
    }

    /**
     * Unregister the alias loader instance.
     *
     * @return void
     */
    public function unregister(): void
    {
        spl_autoload_unregister([$this, 'aliasClass']);
    }

    /**
     * Handle the destruction of the instance.
     *
     * @return void
     */
    public function __destruct()
    {
        $this->unregister();
    }

    /**
     * Whether a class may be aliased.
     *
     * @param  string  $class
     * @param  string  $path
     */
    public function isAliasable(string $class, string $path): bool
    {
        if (! str_contains($class, '\\')) {
            return false;
        }


        $filteredIncludedAliases = $this->filter($this->includedAliases, function ($alias) use ($class) {
            return str_starts_with($class, $alias);
        });

        if (! empty($filteredIncludedAliases)) {
            return true;
        }

        if (str_starts_with($path, $this->vendorPath)) {
            return false;
        }

        $filteredExcludedAliases = $this->filter($this->excludedAliases, function ($alias) use ($class) {
            return str_starts_with($class, $alias);
        });

        if (! empty($filteredExcludedAliases)) {
            return false;
        }


        return true;
    }

    function class_basename($class): string
    {
        $class = is_object($class) ? get_class($class) : $class;
        return basename(str_replace('\\', '/', $class));
    }

    function filter(array $array, $callback): array
    {
        return array_filter($array, $callback, ARRAY_FILTER_USE_BOTH);
    }

}
