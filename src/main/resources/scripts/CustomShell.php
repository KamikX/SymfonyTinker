namespace scripts;
namespace scripts;
/*<?php /**/

use Psy\Shell;
use Symfony\Component\VarDumper\Cloner\VarCloner;
use Symfony\Component\VarDumper\Dumper\HtmlDumper;

class CustomShell extends Shell
{


    private VarCloner $cloner;
    private HtmlDumper $dumper;

    public function __construct()
    {
        $this->cloner = new VarCloner();
        $this->dumper = new HtmlDumper();

        parent::__construct();
    }

    public function writeReturnValue($ret, bool $rawOutput = false): void
    {
        //dump($ret);
        $data = $this->cloner->cloneVar($ret);
        $this->dumper->dump($data);
    }

}
