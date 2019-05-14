# ipritz

### Klonen

Zum Klonen einfach in Android Studio auf File -> New -> Project from version control -> git, dann https://github.com/swestfechtel/ipritz.git als URL angeben.

### Android Studio Version Control

Android Studio hat eine eingebaute Version Control. Wenn man neue Dateien zum Projekt hinzufuegt, fragt Android Studio automatisch, ob diese zur Versionskontrolle hinzugefuegt werden sollen. Dies am besten immer bestaetigen.

## Commit

Nachdem man eigene Aenderungen vorgenommen hat, auf den gruenen Haken in der toolbar oben rechts (git, shortcut strg + k) druecken und commiten. Dann ggf. VCS -> git -> pull, um remote changes zu mergen. Dann die Aenderungen pushen (ggf. auf einen neuen Zweig).

### MainActivity

MainActivity ist der Einstiegspunkt in die App. Sie beinhaltet ein GoogleMaps-Fragment sowie die AppBar mit Suchleiste und Menu.
Neue intents können in der Methode public boolean onNavigationItemSelected(MenuItem item) eingehängt werden.

### Values

Farben, Strings, Text styles u.a. können in den entsprechenden Dateien im values-Ordner definiert und dann mit @ referenziert werden.
