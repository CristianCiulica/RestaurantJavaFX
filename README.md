# RestaurantFX (JavaFX + PostgreSQL)

Aplicație desktop JavaFX pentru managementul unui restaurant: autentificare pe roluri, mese, comenzi, meniu/produse, reduceri și persistență în PostgreSQL prin JPA/Hibernate.

> Repo: `RestaurantJavaFX`

## Tech Stack

- Java + JavaFX (UI)
- Maven (build)
- PostgreSQL (DB)
- Jakarta Persistence (JPA) + Hibernate (ORM)
- JSON import/export (prin `RestaurantFXExportImport`)

## Funcționalități

- Login cu roluri:
  - **ADMIN**
  - **STAFF**
  - **CLIENT** (guest)
- Management mese (liber/ocupat)
- Creare / modificare comenzi per masă
- Istoric comenzi pentru staff (încărcare async)
- Reduceri (rule-based), ex:
  - **HappyHourDiscount**
  - **MealDealDiscount**
  - **PartyPackDiscount**
- Persistența produselor (cu flag `active`)
- Export/Import JSON pentru produse
- Tema UI prin JavaFX CSS: `src/main/resources/mip/restaurantfx/theme.css`

## Structură proiect (orientativ)

- `src/main/java/mip/restaurantfx` – cod aplicație (UI, entități, repository-uri)
- `src/main/resources/META-INF/persistence.xml` – configurare JPA
- `src/main/resources/mip/restaurantfx/theme.css` – stylesheet JavaFX
- Entry point:
  - `mip.restaurantfx.Launcher` (pornește `RestaurantGUI`)

## Configurare DB (PostgreSQL)

Aplicația folosește un `persistence.xml` în `src/main/resources/META-INF/persistence.xml`.

1. Pornește PostgreSQL.
2. Creează o bază de date (ex.: `restaurant_db`).
3. Verifică/actualizează în `persistence.xml`:
   - `jakarta.persistence.jdbc.url`
   - `jakarta.persistence.jdbc.user`
   - `jakarta.persistence.jdbc.password`

   
## Build & Test

### Maven

```bash
mvn clean test
```

```bash
mvn clean package
```

## Rulare aplicație

- Din IDE (IntelliJ): rulează `mip.restaurantfx.Launcher`.
- Alternativ, folosește scripturile din repo (dacă sunt potrivite pentru mediul tău):
  - `run-restaurantfx.bat`




