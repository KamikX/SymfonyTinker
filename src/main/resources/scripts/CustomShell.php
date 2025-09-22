namespace scripts;
/*<?php /**/

use Psy\Shell;
use Psy\CodeCleaner\NoReturnValue;
use Psy\Output\ShellOutput;
use Symfony\Component\VarDumper\Cloner\VarCloner;
use Symfony\Component\VarDumper\Dumper\HtmlDumper;

class CustomShell extends Shell
{


    private VarCloner $cloner;
    private HtmlDumper $dumper;
    private bool $varDumperOutputEnabled = true;

    public function __construct()
    {
        $this->cloner = new VarCloner();
        $this->dumper = new HtmlDumper();

        parent::__construct();
    }

    public function setVarDumperEnabled(bool $state): void
    {
        $this->varDumperOutputEnabled = $state;
    }

    /**
     * Write a return value to stdout.
     *
     * The return value is formatted or pretty-printed, and rendered in a
     * visibly distinct manner (in this case, as cyan).
     *
     * @see self::presentValue
     *
     * @param mixed $ret
     * @param bool  $rawOutput Write raw var_export-style values
     */
    public function writeReturnValue($ret, bool $rawOutput = false): void
    {
        if ($this->varDumperOutputEnabled) {
            // Symfony var dumper style
            $data = $this->cloner->cloneVar($ret);
            $this->dumper->dump($data);
        } else {
            // Render raw string (html twig template)
            if (is_string($ret)) {
                $this->writeStdout($ret);
            }
        }
    }


}
