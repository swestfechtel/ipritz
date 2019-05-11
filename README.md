# ipritz

### Klonen

Zum Klonen einfach in Android Studio auf File -> New -> Project from version control -> git, dann https://github.com/swestfechtel/ipritz.git als URL angeben.

### MainActivity

MainActivity ist der Einstiegspunkt in die App. Sie beinhaltet ein GoogleMaps-Fragment sowie die AppBar mit Suchleiste und Menu.
Neue intents können in der Methode public boolean onNavigationItemSelected(MenuItem item) eingehängt werden.

### Values

Farben, Strings, Text styles u.a. können in den entsprechenden Dateien im values-Ordner definiert und dann mit @ referenziert werden.
