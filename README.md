
# TAMZ_II

Seznam vyzkoušených SDK včetně jejich hodnocení.

Všechna SDK byla otestována na telefonu [Redmi Note 5](https://mobilni-telefony.heureka.cz/xiaomi-redmi-note-5-3gb-32gb/ "Redmi Note 5") (3GB RAM)

---

### [Easy AR](https://www.easyar.com/ "Easy AR")
**Podporované platformy:** Android, iOS, Windows, Unity

Velmi pěkné SDK s jednoduchými a pochopitelnými samply, které umí také přehrávat videa, včetně videí stažených z internetu. Využívá matic na vykreslování 3D objektů pomocí openGL. Stabilita však není nejsilnější stránka a při snímání více než 4 objektů zároveň přestává RAM zvládat a aplikace občas spadne. Je však možno jej využít zdarma a bez watermarku.

---

### [Vuforia](https://developer.vuforia.com/ "Vuforia")
**Podporované platformy:** Android, iOS, Unity, Windows 10

Velice stabilní SDK s velmi dobrou dokumentací. Vybrané obrázky pro detekci se uploadují na stránky Vuforia Developer, kde jsou převedeny na .dat a .xml soubory, pomocí kterých následně probíhá image recognition. Podobně je tomu u 3D objektů typu box, kde se přidělí každé straně boxu patřičný obrázek. Ačkoliv mají na stránkách pěkný náhled tohoto 3D boxu, tak při použití v samotné aplikaci jsem měl menší problémy se škálováním jeho velikosti. Ačkoliv ho aplikace snímala, tak ho brala velikosti jako 10x menší. Pokoušel jsem si hrát s velikostmi v .xml souboru, bohužel neúspěšně. Pokud bych ale bral v potaz oficiální sample jejich 3D boxu, tak je tento recognition velice kvalitně provedený.

---

### [ARCore](https://developers.google.com/ar "ARCore")
**Podporované platformy:** Android 7.0+, iOS 11+

Toto SDK se jeví jako nejlepší možnost pro android vůbec, jelikož je vytvářeno samotných Googlem, který do něj nejspíš momentálně vráží velkou hromadu financí. Nemohl jsem to však vyzkoušet, jelikož po mě požadovali telefon s 60FPS FullHD kamerou a můj jeskynní telefon tedy není podporovaný.

---

### [Wikitude](https://www.wikitude.com/ "###Wikitude")
**Podporované platformy:** Android, iOS, Unity a Chytré brýle (Epson Moverio, Vuzix M100, ODG R-7)

Rovnocenný konkurent a alternativa Vuforie. Obě SDK jsou velmi podobné. Bohužel však k velikosti projektu (150MB+) jsem nemohl uploadnout svoji upravenou verzi zde na GitHub.

---

### [DeepAR](https://www.deepar.ai/ "DeepAR")
**Podporované platformy:** PC, Android, iOS, Windows, WebGL

Toto SDK není dostupné veřejnosti. Je potřeba napsat mail autorům, kteří využití SDK schválí. Mě však neodpověděli. Jejich ukázkovou aplikaci je však možné najít [zde](https://mrrmrr.me/ "zde").

---

### [ARKit](https://developer.apple.com/augmented-reality/ "ARKit")
**Podporované platformy:** iOS 11/12

Android zde není podporován, takže ačkoliv je tato knihovna prý jedna z nejlepších (řekl jakýsi článek na internetu), tak jsem neměl možnost ji otestovat.

---
### Shrnutí 

Ze všech mnou vyzkoušených SDK mi přišla nejsympatičtější Vuforia. Wikitude se však zdá jako rovnocenný konkurent. Při výběru bych se nejspíš rozhodoval, jestli mi vyhovují spíše měsíční poplatky u Vuforie, nebo jednorázová platba u Wikitude, která však nenabízí následné updaty. U obou se vytvářejí speciální soubory pro detekci obrázků a 3D objektů. Tyto soubory se generují na webové stránce vývojářů, která zároveň i ohodnotí, jak dobře detekovatelné samotné obrázky jsou. Obě knihovny využívají jakýmsi způsobem ARCore. Vzhledem k tržní síle Googlu to bude předpokládám číslo jedna mezi aktuálními SDK a mrzí mě, že mi to mobil nepodporuje.

Easy AR vypadá také slibně, ale těmto dvěma konkurentům se zatím stabilitou nevyrovná. Jeho hlavní výhoda je, že lze použít zdarma a bez watermarku, přičemž je oproti plné verzi ořezaný jen o 3D object tracking a 2 další vlastnosti. 

Našel jsem taktéž pěkné srovnání těchto 3 SDK.
https://www.youtube.com/watch?v=DQ1YJ40Suus
