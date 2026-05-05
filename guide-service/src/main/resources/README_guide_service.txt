GUIDE-SERVICE README

1. OPIS MIKROSERVISA

Guide-service je mikroservis u okviru sistema Eco-Travel & Adventure Management System zadužen za upravljanje profilima vodiča, jezicima koje vodiči govore, njihovim sertifikatima i eksternom komunikacijom sa avantura-service mikroservisom.

Servis je razvijen po principima slojevite arhitekture:
- entity sloj za modele baze,
- repository sloj za pristup podacima,
- service sloj za poslovnu logiku i validacije,
- controller sloj za izlaganje REST endpoint-a,
- DTO i mapper sloj za razmenu podataka bez direktnog izlaganja entiteta,
- client sloj za komunikaciju sa drugim mikroservisom.

Pored lokalnih CRUD i poslovnih operacija nad vodičima, servis podržava i udaljene pozive ka avantura-service mikroservisu pomoću OpenFeign klijenta. Ti pozivi su dodatno zaštićeni Resilience4j mehanizmima Retry i Circuit Breaker. Takođe je implementiran Facade pattern kako bi eksterna komunikacija bila pojednostavljena i jasno odvojena od controller sloja.

--------------------------------------------------

2. TEHNIČKE SPECIFIKACIJE

Naziv aplikacije: guice-service
Port: 8082

Tehnologije:
- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- H2 Database
- Maven
- Swagger / OpenAPI
- OpenFeign
- Resilience4j (Retry + Circuit Breaker)
- Spring Boot Actuator
- Lombok

Konfiguracija servisa:
- spring.application.name=guice-service
- server.port=8082
- baza: jdbc:h2:mem:guidedb
- korisničko ime baze: kimi
- Hibernate DDL režim: create-drop
- automatska inicijalizacija baze: uključena
- H2 konzola: /h2-console
- Swagger UI: /swagger-ui.html

Resilience4j konfiguracija:
- Retry instanca: adventureRetry
- max-attempts = 3
- wait-duration = 2s

- Circuit Breaker instanca: adventureCB
- sliding-window-size = 5
- failure-rate-threshold = 50
- wait-duration-in-open-state = 10s
- permitted-number-of-calls-in-half-open-state = 3
- register-health-indicator = true

Monitoring konfiguracija:
- management.endpoints.web.exposure.include=health,circuitbreakers
- management.endpoint.health.show-details=always
- management.health.circuitbreakers.enabled=true

--------------------------------------------------

3. POKRETANJE I TESTIRANJE

Koraci za lokalno pokretanje:
1. Otvoriti projekat u IntelliJ IDEA ili drugom Java IDE okruženju.
2. Sačekati da Maven učita sve zavisnosti.
3. Pokrenuti glavnu Spring Boot klasu mikroservisa.
4. Nakon pokretanja servis će biti dostupan na adresi:
   http://localhost:8082

Dodatne korisne adrese:
- Swagger UI:
  http://localhost:8082/swagger-ui.html

- H2 Console:
  http://localhost:8082/h2-console

- Actuator health:
  http://localhost:8082/actuator/health

- Actuator circuit breakers:
  http://localhost:8082/actuator/circuitbreakers

Testiranje se može vršiti preko:
- Swagger UI interfejsa,
- Postman alata,
- H2 Console za proveru podataka u bazi,
- Actuator endpoint-a za prikaz stanja osigurača.

Napomena:
Za testiranje OpenFeign komunikacije potrebno je da avantura-service bude pokrenut na portu 8081, jer Guide-service poziva rute mikroservisa na adresi http://localhost:8081/api/adventures.

--------------------------------------------------

4. MODEL PODATAKA I ENTITETI

4.1 GuideProfile
Glavni entitet servisa koji predstavlja profil jednog vodiča.

Polja:
- id: jedinstveni identifikator vodiča
- firstName: ime vodiča
- lastName: prezime vodiča
- bio: biografija ili kratak opis iskustva vodiča
- rating: prosečan rejting vodiča
- contactEmail: kontakt email vodiča
- phoneNumber: kontakt telefon vodiča

Relacije:
- OneToMany sa Certification
- OneToMany sa GuideLanguage

Uloga u sistemu:
- predstavlja centralni entitet guide-service mikroservisa,
- na njega se vezuju sertifikati i jezici,
- koristi se za lokalne poslovne odluke kao što su ready-check, elite status i bonus za jezike.

4.2 Certification
Predstavlja sertifikat ili licencu vodiča.

Polja:
- id: jedinstveni identifikator sertifikata
- name: naziv sertifikata
- issuingBody: organizacija ili institucija koja je izdala sertifikat
- expiryDate: datum isteka sertifikata

Relacije:
- ManyToOne sa GuideProfile
- ManyToMany sa GuideLanguage

Uloga u sistemu:
- služi za proveru da li vodič poseduje važeće licence,
- koristi se u logici spremnosti vodiča za teren,
- omogućava administrativni pregled isteklih sertifikata.

4.3 GuideLanguage
Predstavlja vezu između vodiča i jezika koji govori, uz nivo znanja.

Polja:
- id: jedinstveni identifikator veze
- proficiencyLevel: nivo znanja jezika, na primer A1, B2, C1 ili Native

Relacije:
- ManyToOne sa GuideProfile
- ManyToOne sa Language
- ManyToMany sa Certification

Uloga u sistemu:
- omogućava modelovanje činjenice da jedan vodič može govoriti više jezika,
- koristi se za proveru kompetencije vodiča za vođenje ture na određenom jeziku,
- učestvuje u logici za bonus na rejting kod poliglota.

4.4 Language
Predstavlja jezik iz kataloga jezika koji sistem koristi kao referentni skup dozvoljenih jezika.

Polja:
- id: jedinstveni identifikator jezika
- languageName: puni naziv jezika, na primer Engleski, Nemački ili Italijanski
- languageCode: standardizovani kod jezika, na primer EN, DE ili IT

Relacije:
- indirektno je povezan sa vodičima preko entiteta GuideLanguage

Uloga u sistemu:
- Language je mali, ali veoma važan kataloški entitet,
- obezbeđuje da se jezici ne unose proizvoljno kao običan tekst,
- omogućava jedinstven katalog jezika za ceo servis,
- koristi se za pretragu vodiča po jeziku,
- koristi se pri proveri da li vodič može da vodi turu na određenom jeziku,
- omogućava validaciju unikatnosti naziva jezika i unikatnosti ISO koda,
- olakšava održavanje sistema jer se naziv i kod jezika čuvaju na jednom mestu umesto da se ponavljaju u više tabela.

--------------------------------------------------

5. REPOSITORY SLOJ

U servisnom sloju korišćeni su sledeći repozitorijumi:

GuideProfileRepository
Podržane metode:
- existsByContactEmail(String contactEmail)
- findEliteGuides()
- findAllByLanguageId(Long languageId)

Napomena:
- findEliteGuides koristi JPQL upit koji pronalazi vodiče sa rejtingom iznad prosečnog rejtinga svih vodiča.
- findAllByLanguageId koristi JOIN logiku preko GuideLanguage veze.

GuideLanguageRepository
Podržane metode:
- countByGuideProfileId(Long guideId)
- findByGuideProfileIdAndLanguageId(Long guideId, Long languageId)

CertificationRepository
Podržane metode:
- countValidCertifications(Long guideId, LocalDate today)
- findByExpiryDateBefore(LocalDate date)

LanguageRepository
Podržane metode:
- existsByLanguageName(String languageName)
- existsByLanguageCode(String languageCode)

--------------------------------------------------

6. POSLOVNA LOGIKA I VALIDACIJE

6.1 GuideProfileService

GuideProfileService upravlja profilima vodiča i implementira i lokalnu i eksternu poslovnu logiku.

Implementirane lokalne funkcionalnosti:
- kreiranje vodiča,
- pregled svih vodiča,
- pregled vodiča po ID-u,
- izmena vodiča,
- brisanje vodiča,
- pronalaženje elite vodiča,
- pronalaženje zamene za vodiča po jeziku,
- provera da li je vodič spreman za teren,
- dodela bonus rejtinga poliglotama,
- statistika raspodele vodiča po kvalitetu.

Poslovna pravila:
- email vodiča mora biti jedinstven,
- vodič je elite ako ima rejting iznad proseka svih vodiča,
- vodič je spreman za teren ako ima rejting najmanje 4.0 i makar jedan važeći sertifikat,
- vodiču se dodaje bonus na rejting ako govori 3 ili više jezika,
- bonus ne može podići rejting preko 5.0,
- zamena za vodiča se traži među vodičima koji govore zadati jezik i imaju rejting najmanje 4.0.

Statistika rejtinga:
- Gold: rejting 4.5 i više
- Silver: rejting od 3.5 do 4.5
- Bronze: rejting ispod 3.5

6.2 CertificationService

CertificationService upravlja sertifikatima vodiča.

Implementirane funkcionalnosti:
- kreiranje sertifikata,
- pregled svih sertifikata,
- pregled sertifikata po ID-u,
- izmena sertifikata,
- brisanje sertifikata,
- pronalaženje email adresa vodiča sa isteklim sertifikatima.

Poslovna pravila:
- vodič mora postojati pre kreiranja sertifikata,
- sertifikat ne sme biti već istekao u trenutku unosa,
- pri ažuriranju se proverava i postojanje vodiča na kog se sertifikat vezuje.

6.3 GuideLanguageService

GuideLanguageService upravlja jezicima koje vodiči govore.

Implementirane funkcionalnosti:
- kreiranje veze vodič-jezik,
- pregled svih veza vodič-jezik,
- pregled veze po ID-u,
- izmena veze,
- brisanje veze,
- provera da li vodič može da vodi turu na određenom jeziku,
- brojanje aktivnih jezika vodiča.

Poslovna pravila:
- vodič mora postojati,
- jezik mora postojati,
- ista kombinacija vodiča i jezika ne sme se duplirati,
- vodič može da vodi turu na jeziku samo ako ima nivo znanja B2, C1, C2 ili Native.

6.4 LanguageService

LanguageService upravlja katalogom jezika.

Implementirane funkcionalnosti:
- kreiranje jezika,
- pregled svih jezika,
- pregled jezika po ID-u,
- izmena jezika,
- brisanje jezika.

Poslovna pravila:
- naziv jezika mora biti jedinstven,
- ISO kod jezika mora biti jedinstven,
- pri izmeni se proverava da naziv ne bude zauzet.

--------------------------------------------------

7. DESIGN PATTERN-I

7.1 Facade Pattern

U mikroservisu je implementiran Facade pattern preko interfejsa AdventureFacade.

Interfejs AdventureFacade definiše sledeće metode:
- getPremiumReport(Long guideId, Double minPrice)
- getRevenue(Long guideId)
- checkOverload(Long guideId)
- getAllInCategory(Long categoryId)

GuideProfileService implementira ovaj interfejs i na taj način objedinjuje svu eksternu komunikaciju sa avantura-service mikroservisom iza jedinstvene i jednostavne apstrakcije.

Prednosti ovakvog pristupa:
- controller sloj ne zna detalje OpenFeign poziva,
- komunikacija sa spoljnim servisom je centralizovana,
- fallback i otpornost se nalaze na jednom mestu,
- lakša je zamena implementacije u budućnosti.

7.2 OpenFeign klijent

Za udaljenu komunikaciju koristi se AdventureClient.

AdventureClient poziva sledeće rute avantura-service mikroservisa:
- GET /guide/{guideId}/premium
- GET /guide/{guideId}/revenue
- GET /guide/{guideId}/check-overload
- GET /category/{categoryId}

Klijent je deklarisan sa:
@FeignClient(name = "avantura-service", url = "http://localhost:8081/api/adventures")

7.3 Remote DTO pristup

Za prijem odgovora iz drugog mikroservisa koristi se klasa RemoteAdventureDTO.

Polja:
- id
- adventureName
- price
- difficultyLevel

Specifičnosti:
- polje adventureName mapira se sa udaljenog polja title pomoću @JsonProperty("title")
- polje price mapira se sa udaljenog polja basePrice pomoću @JsonProperty("basePrice")
- @JsonIgnoreProperties(ignoreUnknown = true) sprečava pucanje servisa ako drugi mikroservis vrati dodatna polja

7.4 DTO i Mapper pristup

Servis koristi DTO obrasce za razdvajanje spoljnog API sloja od JPA entiteta. Na taj način se entiteti ne izlažu direktno kroz REST odgovore, što povećava bezbednost, kontrolu nad podacima i čitljivost arhitekture.

--------------------------------------------------

8. BAZA PODATAKA I POČETNI PODACI

Baza je H2 in-memory baza, što znači da se podaci brišu nakon gašenja aplikacije.

U import skripti definisani su početni podaci:
- 15 jezika,
- 15 profila vodiča,
- 15 sertifikata,
- 15 veza vodič-jezik.

Primeri test scenarija iz inicijalnih podataka:
- postoje vodiči sa različitim nivoima rejtinga, što omogućava testiranje elite vodiča i raspodele Gold/Silver/Bronze,
- postoje vodiči sa više jezika, što omogućava testiranje language bonus logike,
- postoje sertifikati sa različitim datumima isteka, što omogućava testiranje logike važećih i isteklih sertifikata.

--------------------------------------------------

9. ENDPOINT-I

U mikroservisu su implementirani REST endpoint-i za upravljanje vodičima, jezicima, sertifikatima i vezama vodič-jezik, kao i endpoint-i za eksternu komunikaciju sa avantura-service mikroservisom. Svi endpoint-i su dokumentovani Swagger (OpenAPI) anotacijama, što omogućava njihovo testiranje i pregled kroz Swagger UI interfejs.

9.1 Guide Profile Controller
Osnovna putanja: /api/guides

GET /api/guides
- Vraća listu svih vodiča.

GET /api/guides/{id}
- Vraća jednog vodiča na osnovu ID vrednosti.

POST /api/guides
- Kreira novog vodiča.
- Koristi GuideProfileRequest DTO.

PUT /api/guides/{id}
- Menja postojeći profil vodiča.
- Koristi GuideProfileRequest DTO.

DELETE /api/guides/{id}
- Briše vodiča po ID-u.

GET /api/guides/elite
- Vraća sve elite vodiče.
- Elite vodiči su oni čiji je rejting iznad proseka sistema.

GET /api/guides/replacements?languageId={languageId}
- Vraća listu mogućih zamena za vodiča na osnovu jezika i visokog rejtinga.

GET /api/guides/{id}/ready-check
- Proverava da li vodič sme na teren.
- Potrebni uslovi su dovoljan rejting i makar jedan važeći sertifikat.

POST /api/guides/{id}/apply-bonus
- Dodeljuje bonus na rejting ako vodič govori 3 ili više jezika.

GET /api/guides/stats/rating-distribution
- Vraća raspodelu vodiča po kategorijama Gold, Silver i Bronze.

GET /api/guides/{id}/premium-adventures?minPrice={minPrice}
- Preko fasade i OpenFeign klijenta dobavlja premium avanture određenog vodiča iz avantura-service mikroservisa.

GET /api/guides/{id}/total-revenue
- Preko fasade i OpenFeign klijenta dobavlja ukupan prihod vodiča iz avantura-service mikroservisa.

GET /api/guides/{id}/is-overloaded
- Preko fasade i OpenFeign klijenta proverava da li je vodič preopterećen turama.

GET /api/guides/search/category/{categoryId}
- Preko fasade i OpenFeign klijenta vrši globalnu pretragu avantura po kategoriji.

--------------------------------------------------

9.2 Certification Controller
Osnovna putanja: /api/certifications

GET /api/certifications
- Vraća sve sertifikate.

GET /api/certifications/{id}
- Vraća sertifikat po ID-u.

POST /api/certifications
- Kreira novi sertifikat.
- Koristi CertificationRequest DTO.

PUT /api/certifications/{id}
- Menja postojeći sertifikat.

DELETE /api/certifications/{id}
- Briše sertifikat po ID-u.

GET /api/certifications/risk-report/expired-emails
- Vraća listu email adresa vodiča čiji su sertifikati istekli.
- Ovaj endpoint je namenjen administrativnom nadzoru i upozorenjima.

--------------------------------------------------

9.3 Guide Language Controller
Osnovna putanja: /api/guide-languages

GET /api/guide-languages
- Vraća sve veze vodič-jezik.

GET /api/guide-languages/{id}
- Vraća jednu vezu po ID-u.

POST /api/guide-languages
- Dodeljuje jezik vodiču.
- Koristi GuideLanguageRequest DTO.

PUT /api/guide-languages/{id}
- Menja postojeću vezu vodič-jezik.

DELETE /api/guide-languages/{id}
- Briše vezu vodič-jezik.

GET /api/guide-languages/check-fluency?guideId={guideId}&languageId={languageId}
- Proverava da li vodič može da vodi turu na zadatom jeziku.
- Potrebni nivo je B2 ili viši.

GET /api/guide-languages/count/{guideId}
- Vraća broj aktivnih jezika koje vodič govori.

--------------------------------------------------

9.4 Language Controller
Osnovna putanja: /api/languages

GET /api/languages
- Vraća sve jezike iz kataloga.

GET /api/languages/{id}
- Vraća jezik po ID-u.

POST /api/languages
- Kreira novi jezik.
- Koristi LanguageRequest DTO.

PUT /api/languages/{id}
- Menja postojeći jezik.

DELETE /api/languages/{id}
- Briše jezik po ID-u.

--------------------------------------------------

9.5 DTO validacija

Za ulazne podatke koriste se posebni Request DTO objekti sa Bean Validation pravilima:

GuideProfileRequest
- firstName: obavezno
- lastName: obavezno
- bio: maksimalno 1000 karaktera
- rating: obavezno, u opsegu od 1 do 5
- contactEmail: obavezno i mora biti validan email
- phoneNumber: validira se preko regex obrasca za telefonski broj

CertificationRequest
- name: obavezno
- issuingBody: obavezno
- expiryDate: obavezno i mora biti u budućnosti
- guideProfileId: obavezno

GuideLanguageRequest
- guideProfileId: obavezno
- languageId: obavezno
- proficiencyLevel: mora biti jedna od vrednosti A1, A2, B1, B2, C1, C2 ili Native

LanguageRequest
- languageName: obavezno
- languageCode: obavezno, dužine između 2 i 5 karaktera

--------------------------------------------------

9.6 Response DTO objekti

Za izlaz iz sistema koriste se Response DTO klase, čime se JPA entiteti ne izlažu direktno klijentu.

GuideProfileResponse sadrži:
- id
- firstName
- lastName
- bio
- rating
- contactEmail
- phoneNumber
- languages
- activeCertifications

CertificationResponse sadrži:
- id
- name
- issuingBody
- expiryDate
- expired
- guideFullName

GuideLanguageResponse sadrži:
- id
- guideFullName
- languageName
- proficiencyLevel

LanguageResponse sadrži:
- id
- languageName
- languageCode

--------------------------------------------------

10. OPENFEIGN I OTPORNOST SISTEMA

U mikroservisu je implementirana interna komunikacija sa avantura-service mikroservisom preko AdventureClient interfejsa.

Pozivi ka drugom servisu zaštićeni su sledećim mehanizmima:
- Retry mehanizam sa maksimalno 3 pokušaja za čitanje podataka,
- Circuit Breaker koji otvara osigurač kada broj neuspeha pređe definisani prag,
- fallback metode koje vraćaju adekvatan odgovor umesto serverske greške.

Fallback metode:
- fallbackPremium(Long guideId, Double minPrice, Throwable t) -> vraća praznu listu
- fallbackRevenue(Long guideId, Throwable t) -> vraća 0.0
- fallbackOverload(Long guideId, Throwable t) -> vraća false
- fallbackCategoryOnly(Long categoryId, Throwable t) -> vraća praznu listu

Na ovaj način guide-service može stabilno da funkcioniše čak i kada avantura-service trenutno nije dostupan.

--------------------------------------------------

11. MONITORING I INSPEKCIJA ZDRAVLJA

U mikroservisu je konfigurisan Spring Boot Actuator.

Osnovna putanja za monitoring endpoint-e: /actuator

Izloženi endpoint-i:
- /actuator/health
- /actuator/circuitbreakers

Podešeno je:
- management.endpoints.web.exposure.include=health,circuitbreakers
- management.endpoint.health.show-details=always
- management.health.circuitbreakers.enabled=true

Na ovaj način je omogućen osnovni uvid u stanje servisa i u stanje osigurača tokom rada.

--------------------------------------------------

12. ZAKLJUČAK

Guide-service predstavlja centralni mikroservis za upravljanje vodičima u okviru sistema za ekološki turizam. Kroz slojevitu arhitekturu, validacije u servisnom sloju, DTO obrasce, JPA repozitorijume, OpenFeign komunikaciju, Resilience4j mehanizme otpornosti i Facade pattern, obezbeđena je čista organizacija koda, kontrolisan unos podataka i stabilna integracija sa drugim mikroservisom.
