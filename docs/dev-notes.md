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