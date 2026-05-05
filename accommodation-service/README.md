# Accommodation Service

Ovo je mikroservis za rad sa smještajnim jedinicama u okviru Eco-Travel & Adventure sistema. 

## Tehnologije

- Spring Boot 3.4.1
- Java 21
- Port: 8080
- Baza: H2 (in-memory)
- Schema: `accommodationdb`

## Šta servis radi?

Servis pokriva sve vezano za smještaj:

- **Jedinice (Units)**  
  Tipovi poput šatora, koliba, glamping-a, kuća na drvetu itd. Svaka ima osnovnu cijenu i lokaciju.

- **Sadržaji (Facilities)**  
  Jedinica može imati više sadržaja (npr. bazen), i neki od njih povećavaju cijenu.

- **Sezone (Price tiers)**  
  Cijena se mijenja po sezoni.

- **Dostupnost (Availability)**  
  Prati se kada je jedinica zauzeta da ne dođe do duplih rezervacija.

- **Računanje cijene**  
  Može se izračunati ukupna cijena za određeni period.