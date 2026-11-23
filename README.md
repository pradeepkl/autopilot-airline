# autopilot-airline: step 1 of 7

## The autopilot is engaged

**Tag:** `demo-0.0-baseline` · **Setup and baseline** · ~2 min

Demo project for **Spring Boot Beyond the Defaults: A Practical Framework for Overriding Autoconfiguration**.

```bash
git checkout demo-0.0-baseline     # you are here
```

## What to say

- Nothing here is configured. We never wrote a server, a connection pool, an ORM or a JSON setup. Spring Boot assembled all of it from the starters in `pom.xml`.
- Read the instruments before touching any control. The Conditions Evaluation Report shows what Boot decided and why.
- Remember the baseline: nulls appear in the JSON, dates are ISO-8601, Tomcat is the server, HikariCP is the pool. Every later step is judged against this snapshot.

## Key files to show

| File | Point at |
|---|---|
| `pom.xml` | Five starters and no versions. This is the entire infrastructure declaration. |
| `src/main/resources/application.yaml` | Only the app name and the actuator exposure (the cockpit). Nothing else is configured. |
| `src/main/java/com/autopilot/airline/booking/Booking.java` | The aggregate: a booking with many line items. |
| `src/main/java/com/autopilot/airline/booking/BookingItem.java` | A line item: a flight SEGMENT or an extra (BAGGAGE, SEAT, MEAL). Extras have null flight number and date. |
| `src/main/java/com/autopilot/airline/booking/BookingController.java` | `GET /v1/bookings`, `GET /v1/bookings/{id}`, `POST /v1/bookings`. |
| `scripts/conditions.sh and scripts/snapshot.sh` | The instruments: filter the report, and save it so two steps can be diffed. |

## Run it

```bash
./mvnw spring-boot:run
curl -s localhost:8080/v1/bookings/3 | python3 -m json.tool      # nulls on the SEAT and MEAL items, ISO dates
scripts/conditions.sh 'JacksonAutoConfiguration#|DataSourceConfiguration\.|TomcatServlet'
scripts/snapshot.sh before                                      # every later step diffs against this
```

## What to expect

- `JacksonAutoConfiguration#jacksonJsonMapper`, `DataSourceConfiguration.Hikari` and `TomcatServletWebServerAutoConfiguration` are in **positive matches**.
- Each one is there because its guard (`@ConditionalOnMissingBean`, `@ConditionalOnClass`) passed.

## Next

```bash
git checkout demo-1.0-property-tuning
```

## The demo path

| # | Checkout | Topic |
|---|---|---|
| 1 | ➡ `git checkout demo-0.0-baseline` | The autopilot is engaged |
| 2 | `git checkout demo-1.0-property-tuning` | Level 1: property tuning |
| 3 | `git checkout demo-2.0-extension-points` | Level 2: extension points |
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
