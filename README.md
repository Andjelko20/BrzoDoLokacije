# BrzoDoLokacije

## Description

Android aplikacija koja predstavlja drustvenu mrezu cija je ideja deljenje realisticnih slika turistickih lokacija.

## Ideas and prototypes

Figma prototip mozete videti na sledecem [linku](https://www.figma.com/file/X1pf7FB1KpEHQrVT2ziyJA/Untitled?node-id=0%3A1).

## Download

Aplikaciju mozete preuzeti na sledecem [linku](http://softeng.pmf.kg.ac.rs:10051/).

Lozinka za pristup je `123passwd123`

## Example accounts

- filip_bojovic : Sifra123
- Username : password

## App Functionality

### Obavezne funkcionalnosti

#### Korisnik

- Prijava / Registracija / Vracanje zaboravljenje sifre preko mejla
- Menjanje imena i prezimena, opisa profila, korisnickog imena, lozinke i profilne slike `(known bug: ostaje stara profilna dok se ponovo ne pokrene aplikacija ili se menja automatski ako se u isto vreme promeni i username zbog ??? razloga, isto tako ce i image recognition da gleda novu profilnu, ona se ispravno ubaci na server i pozove ali se aplikacija tad zbuni, radilo je kako treba pre nego sto smo dodali prikaz objava na profilu)`
- Lista objava korisika
- Pinovane razlicite lokacije na kojima je bio korisnik
- Lista pratilaca
- Broj objava, pratilaca i ukupan broj lajkova svih objava
- Brisanje profila

#### Objava

- Lokacija, opis, datum i vreme
- Mogucnost lajkovanja
- Mogucnost komentarisanja
- Prikaz lokacije na mapi kada se klikne na nju
- Posecivanje profila korisnika koji je izbacio objavu
- Posecivanje profila korisnika koji je lajkovao ili komentarisao

#### Pretraga lokacija

- Pretraga po gradu ili samo po drzavi
- Prikaz objava na mapi
- Filtriranje po popularnosti (lajkovi i komentari) ili po skorasnjosti
- Prikaz odredjene objave klikom na sliku na mapi
- Posecivanje profila korisnika koji je izbacio objavu

#### Pocetna

- Lista objava korisnika koji su zapraceni kao i objava datog korisnika, sortiranih po skorasnjosti
- Pagination po 3 objave i endless scrolling
- Komentarisanje
- Lajkovanje
- Posecivanje profila korisnika koji je izbacio objavu
- Prikaz lokacije na mapi kada se klikne na nju

#### Bezbednost i korektnosti

- Validacija unosa
- Samo jedan korisnik moze imati odgovarajuci username i email
- Do profila nekog korisnika je moguce doci samo preko preko pretrage objava, u listi pratilaca korisnika, lajkova ili komentara objave

### Pozeljne funkcionalnosti

#### Chat

- Inbox - real time prikaz novih korisnika koji su poslali poruku kao i cuvanje dosadasnjeg stanja
- Direct messages - real time prikaz novih i poslatih poruka kao i cuvanje dosadasnjeg stanja

#### Ikonica aplikacije

- :)

### Opcione funkcionalnosti

#### Image recognition

- Profilna mora da bude formata kao identifikacioni dokument i sadrzi samo jednu osobu
- Na slici objave ne sme da se nalazi korisnik koji je objavljuje

#### Statistika korisnika

- Broj objava, pratilaca i ukupan broj lajkova svih objava
