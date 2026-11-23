# autopilot-airline: step 3 of 7

## Level 2: extension points

**Tag:** `demo-2.0-extension-points` · **Level 2** · ~3 min

Demo project for **Spring Boot Beyond the Defaults: A Practical Framework for Overriding Autoconfiguration**.

```bash
git checkout demo-2.0-extension-points     # you are here
```

## What to say

- Properties are schema. Extension points are contracts: the autoconfiguration asks for every bean of an interface and applies each one before building its bean.
- Three real use cases: CORS and a correlation ID (Web MVC), partner-friendly date format (Jackson), SQL tagging so a DBA can trace slow queries (Hibernate).
- No `@EnableWebMvc`, no replaced bean. What the bean does changed, not who owns it, so the report is unchanged.
- The test to remember: if the conditions report changes, it is not Level 2. A `SecurityFilterChain` bean and a hand-built `JsonMapper` both make a default back off. There is no `DataSourceCustomizer`.

## Key files to show

| File | Point at |
|---|---|
| `src/main/java/com/autopilot/airline/config/BookingWebConfig.java` | `WebMvcConfigurer`: CORS mapping and the interceptor registration. |
| `src/main/java/com/autopilot/airline/config/CorrelationIdInterceptor.java` | Echoes or generates `X-Correlation-Id` and puts it in the logging MDC. |
| `src/main/java/com/autopilot/airline/config/DepartureDateFormatCustomizer.java` | `JsonMapperBuilderCustomizer` (Jackson 3, `tools.jackson`): dates as `22-Nov-2026`. |
| `src/main/java/com/autopilot/airline/config/SqlTaggingCustomizer.java` | `HibernatePropertiesCustomizer`: every SQL statement tagged `/* booking-service */` (optional). |
| `src/main/resources/application.yaml` | `spring.jpa.show-sql: true` was added so the SQL tag is visible. |

## Run it

```bash
git show demo-2.0-extension-points --stat
./mvnw spring-boot:run
curl -si -H "Origin: https://book.example-air.com" localhost:8080/v1/bookings/1 | grep -iE 'access-control|x-correlation'
curl -s localhost:8080/v1/bookings/1                              # "departureDate": "22-Nov-2026"
scripts/snapshot.sh after && diff snapshots/before.txt snapshots/after.txt
```

## What to expect

- `Access-Control-Allow-Origin` and `X-Correlation-Id` headers appear. Dates read `22-Nov-2026`. The console shows `Hibernate: /* booking-service */ select ...`.
- The diff prints nothing: `JacksonAutoConfiguration#jacksonJsonMapper` is **still positive**.
- Remember the Jackson customizer. The Level 4 teaser (slide only) shows it silently disappearing.

## Next

```bash
git checkout demo-3.0-dependency-swap
```

## The demo path

| # | Checkout | Topic |
|---|---|---|
| 1 | `git checkout demo-0.0-baseline` | The autopilot is engaged |
| 2 | `git checkout demo-1.0-property-tuning` | Level 1: property tuning |
| 3 | ➡ `git checkout demo-2.0-extension-points` | Level 2: extension points |
| 4 | `git checkout demo-3.0-dependency-swap` | Level 3: dependency replacement |
| 5 | `git checkout demo-4.0-security-defaults` | Level 4 (part 1): the security defaults |
| 6 | `git checkout demo-4.1-own-the-chain` | Level 4 (part 2): own the chain, and make a mistake |
| 7 | `git checkout demo-4.2-own-the-chain-fixed` | Level 4 (part 3): the fix, and the cost of ownership |

## Quick reference

- Run: `./mvnw spring-boot:run` (port 8080, Java 21). Stop other copies first: `pkill -f autopilot-airline`.
- Cockpit: `scripts/conditions.sh [regex]` filters the Conditions Evaluation Report. `scripts/snapshot.sh <label>` saves it to `snapshots/<label>.txt`.
- From `demo-4.0-security-defaults` onward the actuator is protected: `CURL_AUTH=admin:admin123 scripts/conditions.sh` (at 4.0 use `user:<generated password>`).
- Demo accounts: `passenger:passenger123` (PASSENGER), `admin:admin123` (ADMIN). Passwords are `{noop}`, demo only.
- Slides: `cd slides && npm install && npm run dev`. The deck is in every commit, so checking out a step never removes it.
- Decision rule: property, then extension point, then dependency swap, then own the bean. If the conditions report changes, it is not Level 2.
