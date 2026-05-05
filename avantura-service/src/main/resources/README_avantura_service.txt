AVANTURA-SERVICE README

1. OPIS MIKROSERVISA

Avantura-service je mikroservis u okviru sistema Eco-Travel & Adventure Management System zadužen za upravljanje avanturama i njihovim pratećim podacima. U okviru servisa implementirane su funkcionalnosti za rad sa avanturama, kategorijama avantura, terminima održavanja, potrebnom opremom i recenzijama korisnika.

Servis je razvijen po principima slojevite arhitekture:
- entity sloj za modele baze,
- repository sloj za pristup podacima,
- service sloj za poslovnu logiku i validacije,
- controller sloj za izlaganje REST endpoint-a,
- DTO i mapper sloj za razmenu podataka bez direktnog izlaganja entiteta.

Mikroservis koristi H2 bazu u memoriji, JPA/Hibernate za ORM mapiranje, Swagger/OpenAPI za testiranje endpoint-a i Spring Boot Actuator za osnovni monitoring.

--------------------------------------------------

2. TEHNIČKE SPECIFIKACIJE

Naziv aplikacije: avantura-service
Port: 8081

Tehnologije:
- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- H2 Database
- Maven
- Swagger / OpenAPI
- Lombok

Konfiguracija servisa:
- spring.application.name=avantura-service
- server.port=8081
- baza: jdbc:h2:mem:avanturedb
- korisničko ime baze: kimi
- Hibernate DDL režim: create-drop
- automatska inicijalizacija baze: uključena
- H2 konzola: /h2-console
- Swagger UI: /swagger-ui.html
- monitoring endpoint-i: health, info

--------------------------------------------------

3. POKRETANJE I TESTIRANJE

Koraci za lokalno pokretanje:
1. Otvoriti projekat u IntelliJ IDEA ili drugom Java IDE okruženju.
2. Sačekati da Maven učita sve zavisnosti.
3. Pokrenuti glavnu Spring Boot klasu mikroservisa.
4. Nakon pokretanja servis će biti dostupan na adresi:
   http://localhost:8081

Dodatne korisne adrese:
- Swagger UI:
  http://localhost:8081/swagger-ui.html

- H2 Console:
  http://localhost:8081/h2-console

- Actuator health:
  http://localhost:8081/actuator/health

Testiranje se može vršiti preko:
- Swagger UI interfejsa,
- Postman alata,
- H2 Console za proveru podataka u bazi.

--------------------------------------------------

4. MODEL PODATAKA I ENTITETI

4.1 Adventure
Glavni entitet servisa koji predstavlja jednu avanturu.

Polja:
- id
- title
- description
- difficultyLevel
- basePrice
- guideId

Relacije:
- ManyToOne sa AdventureCategory
- OneToMany sa TimeSlot
- OneToMany sa GearRequirement
- OneToMany sa AdventureReview

4.2 AdventureCategory
Predstavlja kategoriju avanture.

Polja:
- id
- name
- description

Relacije:
- OneToMany sa Adventure

4.3 AdventureReview
Predstavlja recenziju jedne avanture.

Polja:
- id
- rating
- comment
- createdAt

Relacije:
- ManyToOne sa Adventure

4.4 GearRequirement
Predstavlja stavku opreme potrebnu za avanturu.

Polja:
- id
- itemName
- isMandatory

Relacije:
- ManyToOne sa Adventure

4.5 TimeSlot
Predstavlja termin održavanja avanture.

Polja:
- id
- startTime
- endTime
- maxParticipants
- currentOccupancy

Relacije:
- ManyToOne sa Adventure

--------------------------------------------------

5. REPOSITORY SLOJ

U servisnom sloju korišćeni su sledeći repozitorijumi:

AdventureRepository
Podržane metode:
- existsByTitle(String title)
- findByAdventureCategoryIdAndBasePriceLessThanEqual(Long categoryId, Double maxPrice)
- findByGuideId(Long guideId)
- countByGuideId(Long guideId)
- findPremiumAdventuresByGuide(Long guideId)
- findByCategoryId(Long categoryId)

AdventureCategoryRepository
Podržane metode:
- existsByName(String name)

AdventureReviewRepository
- standardne CRUD operacije preko JpaRepository

GearRequirementRepository
Podržane metode:
- existsByItemNameAndAdventureId(String itemName, Long adventureId)

TimeSlotRepository
- standardne CRUD operacije preko JpaRepository

Napomena:
U AdventureRepository implementirani su i složeniji upiti korišćenjem @Query anotacije, uključujući:
- pronalaženje premium avantura za određenog vodiča,
- pronalaženje avantura po kategoriji preko JOIN-a.

--------------------------------------------------

6. POSLOVNA LOGIKA I VALIDACIJE

6.1 AdventureService

AdventureService upravlja glavnim poslovnim pravilima vezanim za avanture.

Implementirane funkcionalnosti:
- kreiranje avanture,
- pregled svih avantura,
- pregled avanture po ID-u,
- izmena avanture,
- brisanje avanture,
- obračun cene sa popustom,
- filtriranje avantura,
- izdvajanje premium avantura vodiča,
- računanje ukupnog prihoda vodiča,
- provera da li je vodič preopterećen,
- prikaz avantura po kategoriji.

Poslovna pravila:
- kategorija mora postojati pre kreiranja ili izmene avanture,
- naslov avanture mora biti jedinstven,
- avantura nivoa Extreme ne može imati cenu manju od 150.0,
- popust mora biti između 0 i 100,
- vodič se smatra preopterećenim ako ima više od 3 avanture.

6.2 AdventureCategoryService

AdventureCategoryService upravlja kategorijama avantura.

Implementirane funkcionalnosti:
- kreiranje kategorije,
- pregled svih kategorija,
- pregled kategorije po ID-u,
- izmena kategorije,
- brisanje kategorije,
- generisanje izveštaja o popularnosti kategorije.

Poslovna pravila:
- ime kategorije mora biti jedinstveno,
- kategorija se ne može obrisati ako ima povezane avanture,
- izveštaj o popularnosti zavisi od broja avantura u kategoriji.

6.3 AdventureReviewService

AdventureReviewService upravlja recenzijama avantura.

Implementirane funkcionalnosti:
- kreiranje recenzije,
- pregled svih recenzija,
- pregled recenzije po ID-u,
- izmena recenzije,
- brisanje recenzije,
- statistika prosečne ocene avanture.

Poslovna pravila:
- ocena mora biti u opsegu 1 do 5,
- avantura mora postojati pre vezivanja recenzije,
- pri izmeni se proverava i eventualna promena povezane avanture.

6.4 GearRequirementService

GearRequirementService upravlja opremom potrebnom za avanture.

Implementirane funkcionalnosti:
- kreiranje stavke opreme,
- pregled svih stavki opreme,
- pregled stavke po ID-u,
- izmena stavke opreme,
- brisanje stavke opreme,
- provera usklađenosti obavezne opreme sa nivoom težine,
- brojanje obaveznih stavki opreme.

Poslovna pravila:
- naziv opreme ne sme biti prazan,
- naziv opreme ne može biti samo broj,
- ista stavka opreme ne može biti više puta dodeljena istoj avanturi,
- za Extreme avanture očekuje se najmanje 3 obavezne stavke opreme.

6.5 TimeSlotService

TimeSlotService upravlja terminima održavanja avantura.

Implementirane funkcionalnosti:
- kreiranje termina,
- pregled svih termina,
- pregled termina po ID-u,
- izmena termina,
- brisanje termina,
- rezervacija mesta u terminu,
- pregled dostupnih termina.

Poslovna pravila:
- startTime i endTime moraju biti definisani,
- vreme završetka mora biti posle vremena početka,
- termin mora trajati najmanje 30 minuta,
- maksimalan broj učesnika ne može biti manji od trenutne popunjenosti,
- termin koji već ima prijavljene učesnike ne može se obrisati,
- rezervacija mesta nije moguća ako je termin popunjen,
- dostupni termini su samo oni koji nisu puni i čiji je početak u budućnosti.

--------------------------------------------------

7. DESIGN PATTERN-I

7.1 Builder Pattern

U klasi Adventure implementiran je Builder pattern ručno, bez oslanjanja na Lombok @Builder anotaciju.

Razlozi za primenu:
- omogućava postepeno i čitljivo kreiranje objekta,
- izbegava velike konstruktore sa mnogo parametara,
- smanjuje rizik od greške pri pravljenju entiteta,
- olakšava dalja proširenja modela.

Builder se koristi pri mapiranju DTO objekta u entitet, gde se objekat Adventure formira tek nakon što servisni sloj izvrši sve validacije i poslovne provere.

7.2 DTO i Mapper pristup

Servis koristi DTO obrasce za razdvajanje spoljnog API sloja od JPA entiteta. Na taj način se entiteti ne izlažu direktno kroz REST odgovore, što povećava bezbednost, kontrolu nad podacima i čitljivost arhitekture.

7.3 DTO, Response i Entity Mapper sloj

U mikroservisu je implementiran poseban mapper sloj koji vrši konverziju između Request DTO objekata, JPA entiteta i Response DTO objekata.

AdventureCategoryEntityMapper
- Konvertuje AdventureCategoryRequest u AdventureCategory entitet.
- Konvertuje AdventureCategory entitet u AdventureCategoryResponse.
- U response objektu dodatno računa numberOfAdventures na osnovu veličine povezane liste avantura.

AdventureEntityMapper
- Konvertuje AdventureRequest u Adventure entitet.
- Prilikom konverzije koristi ručno implementiran Builder pattern u klasi Adventure.
- Konvertuje Adventure entitet u AdventureResponse i dodaje categoryId i categoryName.

AdventureReviewEntityMapper
- Konvertuje AdventureReviewRequest u AdventureReview entitet.
- createdAt vrednost se automatski postavlja na trenutno vreme.
- Konvertuje AdventureReview entitet u AdventureReviewResponse.

GearRequirementEntityMapper
- Konvertuje GearRequirementRequest u GearRequirement entitet.
- Konvertuje GearRequirement entitet u GearRequirementResponse.
- U response objektu dodaje i naziv povezane avanture.

TimeSlotEntityMapper
- Konvertuje TimeSlotRequest u TimeSlot entitet.
- currentOccupancy za novi termin automatski postavlja na 0.
- Konvertuje TimeSlot entitet u TimeSlotResponse.

Prednosti ovog pristupa:
- controller sloj ostaje čist i jednostavan,
- servisni sloj je fokusiran na poslovna pravila,
- entiteti se ne izlažu direktno klijentu,
- response objekti mogu sadržati dodatne korisne podatke iz povezanih entiteta,
- lakše je održavanje i proširenje sistema.

--------------------------------------------------

8. BAZA PODATAKA I POČETNI PODACI

Baza je H2 in-memory baza, što znači da se podaci brišu nakon gašenja aplikacije.

U import skripti definisani su početni podaci:
- 15 kategorija avantura,
- 15 avantura,
- više stavki opreme vezanih za avanture,
- termini održavanja za 2026. godinu,
- recenzije za različite avanture.

Primeri test scenarija iz inicijalnih podataka:
- vodič sa ID 3 ima 4 avanture i smatra se overload,
- više Extreme avantura ima cenu 150 ili više,
- termini imaju različite nivoe popunjenosti,
- recenzije omogućavaju računanje prosečne ocene po avanturi.

--------------------------------------------------

9. ENDPOINT-I

U mikroservisu su implementirani REST endpoint-i za upravljanje avanturama, kategorijama, recenzijama, opremom i terminima. Svi endpoint-i su dokumentovani Swagger (OpenAPI) anotacijama, što omogućava njihovo testiranje i pregled kroz Swagger UI interfejs.

9.1 Adventure Controller
Osnovna putanja: /api/adventures

GET /api/adventures
- Vraća listu svih avantura.

GET /api/adventures/{id}
- Vraća jednu avanturu na osnovu ID vrednosti.

POST /api/adventures
- Kreira novu avanturu.
- Koristi AdventureRequest DTO.
- Validira naslov, opis, težinu, cenu i categoryId.

PUT /api/adventures/{id}
- Menja postojeću avanturu.
- Koristi AdventureRequest DTO i prolazi kroz istu poslovnu validaciju kao kod kreiranja.

DELETE /api/adventures/{id}
- Briše avanturu po ID-u.

GET /api/adventures/{id}/calculate-price?discount={discount}
- Računa finalnu cenu avanture nakon primene popusta.
- Predstavlja matematičku/logičku operaciju u servisnom sloju.

GET /api/adventures/filter?categoryId={categoryId}&maxPrice={maxPrice}&difficulty={difficulty}
- Filtrira avanture po više opcionalnih parametara.
- Podržava filtriranje po kategoriji, maksimalnoj ceni i težini.

GET /api/adventures/guide/{guideId}/premium?minPrice={minPrice}
- Vraća sve avanture datog vodiča čija je cena veća ili jednaka zadatoj vrednosti.

GET /api/adventures/guide/{guideId}/revenue
- Računa ukupan prihod vodiča sabiranjem basePrice vrednosti njegovih avantura.

GET /api/adventures/guide/{guideId}/check-overload
- Vraća informaciju da li je vodič preopterećen.
- Vodič je overload ako ima više od 3 avanture.

GET /api/adventures/category/{categoryId}
- Vraća sve avanture koje pripadaju zadatoj kategoriji.
- Ova funkcionalnost koristi JOIN upit u repository sloju.

--------------------------------------------------

9.2 Adventure Category Controller
Osnovna putanja: /api/categories

GET /api/categories
- Vraća sve kategorije avantura.

GET /api/categories/{id}
- Vraća jednu kategoriju po ID-u.

POST /api/categories
- Kreira novu kategoriju.
- Koristi AdventureCategoryRequest DTO.

PUT /api/categories/{id}
- Menja postojeću kategoriju.

DELETE /api/categories/{id}
- Briše kategoriju ako nema vezanih avantura.

GET /api/categories/{id}/popularity
- Vraća tekstualni izveštaj o popularnosti kategorije.
- Izveštaj zavisi od broja avantura koje pripadaju kategoriji.

--------------------------------------------------

9.3 Adventure Review Controller
Osnovna putanja: /api/reviews

GET /api/reviews
- Vraća sve recenzije.

GET /api/reviews/{id}
- Vraća recenziju po ID-u.

POST /api/reviews
- Kreira novu recenziju.
- Koristi AdventureReviewRequest DTO.
- Validira ocenu, komentar i adventureId.

PUT /api/reviews/{id}
- Menja postojeću recenziju.

DELETE /api/reviews/{id}
- Briše recenziju po ID-u.

GET /api/reviews/stats/adventure/{adventureId}
- Vraća statistiku ocena za jednu avanturu.
- Računa broj recenzija i prosečnu ocenu.

--------------------------------------------------

9.4 Gear Requirement Controller
Osnovna putanja: /api/gear

GET /api/gear
- Vraća svu evidentiranu opremu.

GET /api/gear/{id}
- Vraća jednu stavku opreme po ID-u.

POST /api/gear
- Dodaje novu stavku opreme za avanturu.
- Koristi GearRequirementRequest DTO.

PUT /api/gear/{id}
- Menja postojeću stavku opreme.

DELETE /api/gear/{id}
- Briše stavku opreme.

GET /api/gear/safety-check/{adventureId}
- Proverava da li je oprema usklađena sa težinom avanture.
- Za Extreme avanture proverava da li postoji dovoljan broj obaveznih stavki.

GET /api/gear/count-mandatory/{adventureId}
- Računa broj obaveznih stavki opreme za izabranu avanturu.

--------------------------------------------------

9.5 TimeSlot Controller
Osnovna putanja: /api/time-slots

GET /api/time-slots
- Vraća sve termine.

GET /api/time-slots/{id}
- Vraća termin po ID-u.

POST /api/time-slots
- Kreira novi termin za avanturu.
- Koristi TimeSlotRequest DTO.

PUT /api/time-slots/{id}
- Menja postojeći termin.
- Ne dozvoljava smanjenje maxParticipants ispod currentOccupancy.

DELETE /api/time-slots/{id}
- Briše termin samo ako nema prijavljenih učesnika.

POST /api/time-slots/{id}/book
- Rezerviše jedno mesto u terminu.
- Povećava currentOccupancy za 1 ako termin nije popunjen.

GET /api/time-slots/available
- Vraća samo dostupne termine koji nisu puni i čiji je početak u budućnosti.

--------------------------------------------------

9.6 DTO validacija

Za ulazne podatke koriste se posebni Request DTO objekti sa Bean Validation pravilima:

AdventureCategoryRequest
- name: obavezno, maksimalno 50 karaktera
- description: obavezno

AdventureRequest
- title: obavezno
- description: između 10 i 500 karaktera
- difficultyLevel: dozvoljene vrednosti Easy, Medium, Hard, Extreme
- basePrice: mora biti veće od 0
- categoryId: obavezno

AdventureReviewRequest
- rating: obavezno, u opsegu od 1 do 5
- comment: obavezno, maksimalno 1000 karaktera
- adventureId: obavezno

GearRequirementRequest
- itemName: obavezno, između 2 i 100 karaktera
- isMandatory: obavezno
- adventureId: obavezno

TimeSlotRequest
- startTime: obavezno i mora biti u budućnosti
- endTime: obavezno i mora biti u budućnosti
- maxParticipants: između 1 i 100
- adventureId: obavezno

--------------------------------------------------

9.7 Response DTO objekti

Za izlaz iz sistema koriste se Response DTO klase, čime se JPA entiteti ne izlažu direktno klijentu.

AdventureCategoryResponse sadrži:
- id
- name
- description
- numberOfAdventures

AdventureResponse sadrži:
- id
- title
- description
- difficultyLevel
- basePrice
- categoryId
- categoryName

AdventureReviewResponse sadrži:
- id
- rating
- comment
- createdAt
- adventureId
- adventureTitle

GearRequirementResponse sadrži:
- id
- itemName
- isMandatory
- adventureId
- adventureTitle

TimeSlotResponse sadrži:
- id
- startTime
- endTime
- maxParticipants
- currentOccupancy
- adventureId
- adventureTitle

--------------------------------------------------

10. MONITORING I INSPEKCIJA ZDRAVLJA

U mikroservisu je konfigurisan Spring Boot Actuator.
Osnovna putanja za monitoring endpoint-e: /actuator

Izloženi endpoint-i:
- /actuator/health
- /actuator/info

Podešeno je:
- management.endpoints.web.exposure.include=health,info
- management.endpoint.health.show-details=always

Na ovaj način je omogućen osnovni uvid u stanje servisa tokom rada.

--------------------------------------------------

11. ZAKLJUČAK

Avantura-service predstavlja centralni mikroservis za upravljanje avanturama u okviru sistema za ekološki turizam. Kroz slojevitu arhitekturu, validacije u servisnom sloju, upotrebu DTO obrazaca, JPA repozitorijuma i ručno implementiran Builder pattern, obezbeđena je čista organizacija koda, kontrolisan unos podataka i podrška za dalja proširenja sistema.
