# m:tel app takmičenje 2021/2022
## Galić Mijo

## Naziv aplikacije: myTour

## Kratak opis aplikacije (47): Vaš turistički vodičkroz Bosnu i Hercegovinu.

## Puni opis aplikacije (1108): Ovaj turistički vodičće vam pružiti jedinstvenu

sliku Bosne i Hercegovine u jednom drugom svjetlu, kakva ona sa svojim
kulturnim i prirodnim bogatstvima zaista i jeste. Ovaj vodič vam je na
raspolaganju dvadeset i četiri sata, tako da bez brige možete pristupiti u bilo
koje vrijeme pomoću vašeg pametnog telefona ili tableta. Ovdje možete
pronaći preko stotinu gradova kao i četiri stotine atrakcija koje možete posjetiti
u Bosni i Hercegovini. Nakon svake posjećene atrakcije, odnosno grada, sela,
ili nacionalnog parka imate mogućnost da ostavite recenziju, da ukratko
opišete vaše iskustvo, da napišete čak i nešto što vam se nije svidjelo,
potpuno anonimno. Nakon uspješno ostavljene recenzije, u karticu sa vašom
statistikom dodaje se nekoliko myTour tokena, kao i istražena atrakcija, grad,
selo, ili nacionalni park. Za one takmičarskog duha tu je i ljestvica sa petnaest
najboljih iz svake kategorije uključujući i myTour tokene. Ipak, ako odlučite
posjetiti neko od ovih mjesta,a ne znate kako doći do njega, ne brinite se, tu je
pomoćnik koji će vas odvesti do tog mjesta, u svega nekoliko klikova.

## Opis funkcionalnosti aplikacije:


Konkretno što setiče same funkcionalnosti aplikacije za početak imamoSplashScreenzajedno sa
uvodnom animacijom planete, nakon čega imamoLoginScreensa sljedećim
funkcionalnostima:Prijava (username:password), kaoiPrijava putem
Google-a (Google OAuth).U slučaju pokušaja provaljivanjau korisnički nalog
putem password logina, tu imamo dodatnu“brute forcezaštitukoja će nakon
što korisnik, pogriješi svoju lozinku nekoliko puta prikazati grešku. Ukoliko
korisnik nema nalog (myTour), i ne želi da se prijavi putem Google-a, ima
opcijuRegistracije. Ovdje kao dodatnu pomoć imamojedan dialog koji nakon
što korisnik ne uspije da izvrši registraciju nakon nekoliko puta izbacuje tkz.
“Tips”odnosnouputeprilikom registracije. Nakonregistracije, na korisnikov
e-mail šalje se poruka za potvrdu e-mail adrese, čija je potvrda neophodna za
korištenje aplikacije.


Važno je istaći da korisnik prilikom registracije,sam može odabrati korisničko
ime, naravno ukoliko je slobodno, dok sa prijave putemGoogle-a, tonije
slučaj, te ukoliko je ime zauzeto, aplikacijasama generišenovo na osnovu
korisničkog e-maila. Ukoliko korisnik pokuša da se registruje, a već se nekada
prijavio putem Google-a registracija će biti onemogućena, što je također slučaj
za prijavu putem Google-a ukoliko se korisnik već registrovao. Ako korisnik
izgubi svoju lozinku postoji opcija zaresetovanjelozinkeputem maila, te sve
što treba korisnik jeste dazahtjeva resetovanje,te link za reset će biti poslat
na njegov e-mail, nakon čega može daponovo postavisvoju lozinku. Nakon
uspješne prijave korisnik biva prebačen u novi tkz “Landing Activity”, čiji je
osnovni elementdonji navigacijski meni uz pomoćkojeg korisnik može
mijenjatifragmente odnosnoprebacivatise sa jednogelementa na drugi.
Elementi navigacijskog menija su:

1. Početna
2. Istraži
3. Ljestvica
4. Profil

# Početna:

Pored pozdravne poruke, ovdje imamo nekoliko elemenata a to su:
● Izdvojeni gradovi
● Izdvojene atrakcije
● Izdvojena sela
● Izdvojeni nacionalni parkovi
Dakle, iz baze podataka, nasumično se izdvaja po10+gradova, atrakcija,
sela, zajedno sa naslovnimslikamaza gradove, kaoikratkom deskripcijom.
Prilikom klika na bilo koju od ovih kartica otvara se noviactivitysa
pojedinostimao tommjestu, koji uključujevelikunaslovnu sliku, nekoliko
drugih slikamjesta,kratki opis,dugi opis. Poredsvega navedenog u kartici
bilo kog grada možemo pronaći atrakcije u blizinitog grada, na koje klikom se
otvaraactivitysa pojedinostima o tom mjestu. Ukolikose korisnik odluči da
posjeti to mjesto, ili ga barem interesuje gdje se ono geografski nalazi tu su na
raspolaganju dvije opcije tj.“Prikaz na mapi”, kojeprikazuje mjesto na mapi,
kao i“Upute”koje postavljaju upute (directions)do tog mjesta na osnovu
korisnikove lokacije. Isto ovo je slučaj i sa atrakcijama koje se nalaze u blizini
nekog grada.


# Istraži:

Ovdje korisnik ima mogućnost da po svojoj voljipretražigradove,sela,
atrakcije,nacionalne parkovei slično. Nakon izvršenepretrage, ukoliko mjesta
postoje, korisnik dobijamijesta koja ulaze u kriterijipretrage. Pretraga se vrši
posebno za gradove, sela, nacionalne parkove kao i atrakcije. Kao rezultate
pretrage korisnik dobija kartice mjesta koja zadovoljavajukriterijena koje
možekliknutiipogledativiše pojedinosti o tom mjestu.Ova kartica mjesta je
slična onoj kartici na početnoj, odnosno sadrži sve isto :naslovnu sliku, ime
mjesta, kratku, dugu deskripciju, ostale slike tog mjesta, kao i dodatnu funkciju
recenzija i ocjena.Ovdje je prikazanaprosječna ocjenatog mjesta na osnovu
korisničkih recenzija. Za svako mjesto koje ima ocjenupreko 4. 5 ,dodatna
myTourmedaljaće biti prikazana za ocjenu preko 4.5.Korisnik ima
mogućnost da otvori ipregleda ocjene ostalih korisnikakoje suanonimne.
Ukoliko korisnik se odluči daostavi recenzijutomože učiniti klikom nagumb
zadodavanje recenzijenakon čega mu se otvara meniza dodavanje recenzije
kao i ocjene. Ukoliko je korisnikveć dao recenzijunjegov zahtjev će biti
odbijen uz istu poruku, ukoliko to nije učinio imaprostor da da numeričku
ocjenu od 1 do 5, kao i svojkomentarne kraći od15 karaktera kao i duži od
240 karaktera. Nakon uspješno date recenzije korisnik dobijanekoliko myTour
tokena, kao iistraženo mjestou svoju statistikushodno kojoj kategoriji
pripada.

# Ljestvica:
U ljestvici korisnik ima mogućnost da vidiporedak,odnosnoTOP 15 korisnika
po sljedećim kriterijima:
● TOP 15 myTour Tokeni
● TOP 15 Istraženi gradovi
● TOP 15 Istražena sela
● TOP 15 Istražene atrakcije
● TOP 15 Istraženi nacionalni parkovi
Pored gore navedenog korisnik može izvršitimanualnusinkronizacijuodnosno
updejtovanje poretkau realnom vremenu, na taj načinšto će potezom prsta
od vrha ekrana ka dnu izvršiti refrešovanje poznatije kao “swipeto refresh”


# Profil:

Ovdje korisnik može pogledati svoj profil, svoju statsitiku, te ažurirati isti.
Pored avatara korisnika, ovdje možemo naći i puno ime korisnika, kao i
korisničko ime ispod.
Također imamo navigacijski meni sa komponentama:

1. Korisnički podaci - ovdje korisnik može pregledatisvoje podatke,
    ime i prezime, korisničko ime, email, broj telefona, i korisničku
    grupukoju ima.
2. Statistika - ovdje korisnik može pogledati svoju statistiku
    uključujićibroj myTour tokena, broj istraženih gradova,sela,
    atrakcijakao inacionalni parkova.
3. Postavke - klikom na ovu komponentu korisniku se otvaraju
    postavke. Postavke imaju opciju brisanja korisničkog naloga,
    odnosno, klikom na ovu komponentu korisnički nalog, kao i svi
    podaci biti će u potpunosti obrisani.
4. Odjava - prilikom klika korisnik biva odjavljen sa svog naloga te
    prebačen na Login Screen.
Pored navigacijskih komponenata gore navedenih imamo i button za
uređivanje profila koji pokreće novi prozor za uređivanje profila koji sadrži:
1. Uređivanje korisničkog imena i prezimena - korisnik može
promijeniti svoje ime i prezime
2. Uređivanje korisničkog imena - korisnik nema mogućnost
uređivanja ovoga jer korisničko ime je unique odnosno
jedinstveno.
3. Uređivanje e-maila - također korisnik nema mogućnost
uređivanja.
4. Promjena lozinke - korisnik može promijenit lozinku, odnosno
postaviti novu lozinku.
5. Verifikacija broja telefona - ukoliko nije postavljen korisnik će moći
da klikne na gumb verifikuj, odnosno ukoliko jeste korisnik će
imati mogućnost da ukloni broj telefona.
Prilikom klika na verifikuj korisniku se otvara dialog gdje unosi svoj broj
telefona, nakon čega se na taj broj šalje 6 znamenkasti kod, koji korisnik unosi
u aplikaciji nakon čega je verifikacija uspješno završena.
Nakon izvršene verifikacije korisnik dobija beđ (badge) na svom profilu, te
postaje 100% verifikovan korisnik. Pored ovog badge-a postoje još 2 badge-a
Administrator platforme (korisnička grupa Administrator 1), te Developer


platforme (korisnička grupa Administrator 2). Ove grupe isključivo se
postavljaju iz firebase konzole.

Ukoliko korisnik napravi neke promjene, a odluči se izaći to će biti
detektovano od strane aplikacije, odnosno korisnik će biti pitan da li želi
spremiti promjene koje je napravio.

## Kratak opis koda:

Što se tiče samog koda aplikacije potruditi ću se da budem kratak koliko je to
god mogućebudući da aplikacija sadrži oko 7000 linijakoda (.java). Aplikacija je
rađena u Android Studio-u (2020.3.1 patch 3) u programskom jeziku Java. Što se
tiče dependencies, odnosno zavisnosti korištene su sljedeće:
Zbog same sigurnosti korisnika kao i njihovih podataka za autentifikaciju odnosno
samu prijavu u aplikaciju korištena je Google implementacija (Auth i Firebase).
Za dobavljanje slika korištena je implementacija Jsoup (Web Scraping), odnosno
dobavljenje linkova slika, te za prikaz isti korištena je implementacija Picasso.
Što se tiče samih metoda i procedura korištenih unutar aplikacije, mogu reći da su
sve metode i procedure custom, odnosno pisane isključivo za ovu aplikaciju. Ispod
se nalazi kratko objašnjenje koda:

Sama aplikacija podijeljena je u nekoliko package-a, od kojih je glavni
com.gmijo.mytour, koji sadrži nekoliko sub-package-sa koji grade sljedeću strukturu:

com.gmijo.mytour
└ com.gmijo.mytour.database
└ com.gmijo.mytour.dataparser
└ com.gmijo.mytour.ui
└ com.gmijo.mytour.ui.explore
└ com.gmijo.mytour.ui.leaderboard
└ com.gmijo.mytour.ui.pocetna
└ com.gmijo.mytour.ui.profil

Konkretnih metoda neću se doticati budući da bi objašnjavanje svake metode moglo
biti mukotrpno i za mene koji pišem a i za onoga tko čita ovo, samo ću ukratko
opisati kako kod funkcioniše.

Dakle, korisnik pali aplikaciju, nakon čega ga dočekuje animacija, učitavanje, nakon
čega dolazi iz splash screen, nakon kojeg korisnik biva prebačen na login, ukoliko je
prijavljen. U suprotnom, ukoliko korisnik nije prijavljen, korisnik mora da izvrši
registraciju ili prijavu putem googla, odnosno prijavu putem myTour naloga koji može
kreirati u istoj aplikaciji. Dakle, ukoliko korisnik nema korisnički nalog i želi ga kreirati,


odlazi u registar formu koju popunjava, nakon čega šalje zahtjev za registracijom koji
se procesuira. Nakon toga, ukoliko je sve uredu, odnosno korisnik je spojen na
internet, vrše se provjere da li je korisničko ime postoji u bazi, te ukoliko postoji
korisniku bila ispisana poruka da to korisničko ime postoji u bazi, ukoliko ne vrši se
kreiranje naloga te na korisnički mail koji je korisnik unio biva posla te e poruka za
potvrdu. Ukoliko je se korisnim prethodno registrovao putem Google, registracija će
biti onemogućena zbog prethodne registracije, uz odgovarajuću poruku. Nakon što
se izvrši registracija, vrši se pisanje podataka u cloud bazu podataka.Te se vrši
kloniranje istih tih podataka u lokalnu bazu, odnosno u SQLite, kako bi se izbjeglo
nepotrebno čitanje iz baze. Ipak, ukoliko se korisnik odluči da se prijavi putem
Google neće morati ponavljati nikakvu formu, nego će podaci automatski biti
izvučeni sa njegovog Google naloga će biti upisan direktno u bazu. U ovom slučaju
korisničko ime biti će generirano na osnovu korisničkog e maila, a ukoliko se desi da
to korisničko ime već postoji aplikacija će generirati novo korisničko ime. Ukoliko se
desi da je korisnik zaboravi svoju lozinku, ima mogućnost da je vrati uz pomoć svog
je emaila tako što će zahtijevati reset lozinke koji će biti poslat na njegov email.
Nakon što se korisnik uspješno prijavi u aplikaciju, vrši se re-autentifikacija kako bi
se provjerilo da li korisnik još uvijek postoji u bazi podataka. Ukoliko se desi da
korisnik ne postoji, vraća ga na login a ukoliko to uspješno prođe, korisnik može
nastaviti koristiti aplikaciju. U početnoj nalaze izdvojeni gradovi, izdvojene atrakcije,
izdvojena sela, izdvojeni nacionalni parkovi, koji se nasumičnim odabirom iz lokalne
baze podataka odabiru, nakon čega se vrši parsovanje slika na osnovu imena grada
koji se kasnije prikazuju korisniku.Ukoliko se desi da korisnik klikne na bilo koji od
ovih kartica, korisniku se otvara novi i activity u kojem se nalazi svi podaci u tom
gradu koji su izvučeni iz lokalne baze podataka. Također, tu se nalaze i još nekoliko
slika koji su parsovane i prikazane korisniku. Ovo sve prikazivanje gradova, sela, itd
funkcionira na taj način što se podaci šalju u “RecyclerView” koji prikazuje sve te
podatke u obliku kartica, odnosno kako sam ih ja dizajnirao. U funkciji istraži korisnik
može da pretražuje lokalnu bazu podataka, korisnik čak može i odabrati koju
kategoriju želi pretraživati. Ovo funkcionira na taj način što se vrši query nad
lokalnom bazom podataka na osnovu ovih podataka koje je korisnik unio. Nakon što
se korisniku prikažu rezultati, sve u potpunosti, isto kao i kad korisnik otvori karticu
na početnoj. Međutim, postoji 1 razlika. Ta razlika je što korisnik u ovoj funkciji može
da vidi recenzije drugih korisnika, odnosno da vidi prosječnu ocjenu tog mjesta. To
funkcioniše način što se je s cloud bazi podataka sumiraju sve ocjene i dijele sa
brojem recenzija. Ukoliko korisnik klikne da prikaže sve recenzije, izvršit će se query
nad cloud bazom podataka te će svi ovi review-ovi biti ispisani. Pored toga korisnik
može dati recenziju, koja se upisuje u cloud bazu podataka, nakon čega korisniku
biva dodatao nekoliko myTour tokena, i naravno istraženo mjesto na osnovu onoga
gdje je recenziju dao. Što se tiče ljestvice, tu nema ništa specijalno još query nad
cloud bazom podataka. Ovaj query zapravo izvlači top 15 korisnika po kriterijima koji
tu imaju. Što se tiče korisničkog profila, tu su prikazani korisnički podaci koji se
također izvlače iz lokalne baze podataka, potom se prikazuju korisniku. Ukoliko
korisnik želi da prikaže svježe podatke iz clouda bazi podataka. Može to uraditi tako


što će vrhom prsta povući odozgo prema dole i da taj način će se osvježiti podatke u
lokalnoj bazi. Također ovdje imamo i statistiku o korisniku koji je također još jedan
query iz lokalne baze podataka.Pored toga, korisnik može uređivati svoje podatke o
profilu .Na taj način što će direktno u cloud bazu podataka biti upisani podaci uz
prethodne provjere. Ovaj opis koda napisan je uz pomoć voice to text funkcije, pa su
sitne greške moguće. Ukoliko bude potrebe VCS odnosno Version Control Sistem
čitave aplikacije može biti dat na uvid.


