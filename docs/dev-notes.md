# DevNotes

## Logovanie
- treba zapnut `DEBUG` respektive `FINE log level cez `Help | Diagnostic Tools | Debug Log Settings`  bez  prefixu **#** 
```
sk.kamikx.symfonytinker:all
```
- Nasledne sa zacnu logovat vsetky urovne do `.idea-sandbox/log_xyz/idea.log` v projekte samotneho pluginu nie prostredia kde bezi plugin!
- Pre pohodlne debugovanie sa da pouzit plugin `Grep Console` ktory tailuje logy a zobrazuje ich v konzole. Spusta sa priamo kliknuti na `idea.log` (PK) a potom `Open in | Tail in Console`, nasedne treba cez PK  uz v otvorenej konzole pridat samotny grep na priklad na plugin name `sk.kamikx.symfonytinker`


## Icons fuckup
https://youtrack.jetbrains.com/issue/IJPL-134374/New-UI-Tool-window-icon-for-3rd-party-plugins-not-updating-state-when-selected

## Dostupne objekty

```shell
$container = $kernel->getContainer();
$doctrine = $container->get('doctrine');
$em = $container->get('doctrine.orm.entity_manager');
$router = $container->get('router');
$shell->setScopeVariables([
    'kernel' => $kernel,
    'container' => $container,
    'doctrine' => $doctrine,
    'em' => $em,
    'router' => $router,
]);
```
## TODO:
- [x] parameters (https://github.com/theofidry/PsyshBundle/blob/master/src/DependencyInjection/PsyshExtension.php)
- [x] class autoloader (https://github.com/theofidry/PsyshBundle/issues/79#issuecomment-2036665728) nema zmysel IDE plni tuto ulohu
- [ ] performance isssue pri opatovnom spustani (TESTUJEM)
### Dalsie moznosti 
- [x] twig – renderovanie šablón pre rýchle testy.
    - problem s pristupo na twig cez autowire alias
    - problem s pristupom na staticke css a images asset() helper nefunguje nevytvara dobre url
    - JBCefBrowser potrebuje absolutnu cestu k statickym obrazkom v twigu
  
- [ ]logger – otestovať logovanie, prípadne debugnúť, či sa správy ukladajú do súboru.
- event_dispatcher – ručne odpáliť event a sledovať poslucháčov.
- serializer – rýchlo previesť entity do JSON alebo iného formátu.
- validator – otestovať validáciu entít alebo DTO.
- security.password_hasher – testovať hashovanie hesiel.
- http_client – skúšať integrácie s externými API priamo z konzoly.
