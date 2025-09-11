namespace scripts;
namespace scripts;
/*<?php /**/

use Symfony\Component\VarDumper\Cloner\VarCloner;
use Symfony\Component\VarDumper\Dumper\HtmlDumper;
use Symfony\Component\VarDumper\Dumper\AbstractDumper;
use Symfony\Component\VarDumper\Cloner\Stub;

class ObjectCaster
{
    public static function cast($object, array $a, Stub $stub, $isNested, $filter = 0): array
    {
        $cloner = new VarCloner();
        $dumper = new HtmlDumper();
        $depth = null;
        $data = $cloner->cloneVar($object);

        if (null !== $depth) {
            $data = $data->withMaxDepth($depth);
        }

        // Work around https://github.com/symfony/symfony/issues/23572
        $oldLocale = \setlocale(\LC_NUMERIC, 0);
        \setlocale(\LC_NUMERIC, 'C');

        $output = '';
        $dumper->dump($data, function ($line, $depth) use (&$output) {
            if ($depth >= 0) { //  first line contains Sf dump JS script and styles, is not necessary
                if ('' !== $output) {
                    $output .= \PHP_EOL;
                }
                $output .= $line;
            }
        });

        // Now put the locale back
        \setlocale(\LC_NUMERIC, $oldLocale);
        return ['htmlDump' => $output];
    }
}
