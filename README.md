# autopilot-airline: step 7 of 7

## Level 4 (part 3): the fix, and the cost of ownership

**Tag:** `demo-4.2-own-the-chain-fixed` · **Level 4** · ~2 min

Demo project for **Spring Boot Beyond the Defaults: A Practical Framework for Overriding Autoconfiguration**.

```bash
git checkout demo-4.2-own-the-chain-fixed     # you are here
```

## What to say

- Fix it by stating what the default used to do silently: `anyRequest().authenticated()`, role rules, stateless sessions and CSRF off for a header-authenticated API.
- The conditions report is the same as in the previous step. It proves who owns the bean, not whether the bean is correct. Correctness is now your code and your tests.
- Teaser (slide only): a hand-built `@Bean JsonMapper` makes `jacksonJsonMapper` negative and silently drops the Level 2 date format and the `spring.jackson.*` properties.
- Close with the decision rule: property, then extension point, then dependency swap, and only then ownership. Stop at the lowest level that works.

## Key files to show

| File | Point at |
|---|---|
| `src/main/java/com/autopilot/airline/config/BookingSecurityConfig.java` | Same file, now with `authorizeHttpRequests`: health open, `/actuator/**` ADMIN, `GET /v1/bookings` ADMIN, `anyRequest().authenticated()`. |

## Run it

```bash
git diff demo-4.1-own-the-chain demo-4.2-own-the-chain-fixed -- src/main/java
./mvnw spring-boot:run
curl -s -o /dev/null -w '%{http_code}\n' localhost:8080/v1/bookings                                        # 401
curl -s -o /dev/null -w '%{http_code}\n' -u passenger:passenger123 localhost:8080/v1/bookings             # 403
curl -s -o /dev/null -w '%{http_code}\n' -u admin:admin123 localhost:8080/v1/bookings                     # 200
curl -s -o /dev/null -w '%{http_code}\n' -u passenger:passenger123 localhost:8080/v1/bookings/1           # 200
```

## What to expect

- 401 with no credentials, 403 for a passenger on the admin-only list, 200 for an admin and for a passenger on their own booking.
- The conditions report is **identical** to part 2.

## Next

**This is the final step.** Wrap up with the decision rule and the takeaways slide.

```bash
git checkout main                      # same commit as this tag: the latest
git checkout demo-0.0-baseline   # start over
```

## The demo path

| # | Checkout | Topic |
|---|---|---|
| 1 | `git checkout demo-0.0-baseline` | The autopilot is engaged |
| 2 | `git checkout demo-1.0-property-tuning` | Level 1: property tuning |
| 3 | `git checkout demo-2.0-extension-points` | Level 2: extension points |
| 4 | `git checkout demo-3.0-dependency-swap` | Level 3: dependency replacement |
| 5 | `git checkout demo-4.0-security-defaults` | Level 4 (part 1): the security defaults |
| 6 | `git checkout demo-4.1-own-the-chain` | Level 4 (part 2): own the chain, and make a mistake |
| 7 | ➡ `git checkout demo-4.2-own-the-chain-fixed` | Level 4 (part 3): the fix, and the cost of ownership |

## Quick reference

- Run: `./mvnw spring-boot:run` (port 8080, Java 21). Stop other copies first: `pkill -f autopilot-airline`.
- Cockpit: `scripts/conditions.sh [regex]` filters the Conditions Evaluation Report. `scripts/snapshot.sh <label>` saves it to `snapshots/<label>.txt`.
- From `demo-4.0-security-defaults` onward the actuator is protected: `CURL_AUTH=admin:admin123 scripts/conditions.sh` (at 4.0 use `user:<generated password>`).
- Demo accounts: `passenger:passenger123` (PASSENGER), `admin:admin123` (ADMIN). Passwords are `{noop}`, demo only.
- Slides: `cd slides && npm install && npm run dev`. The deck is in every commit, so checking out a step never removes it.
- Decision rule: property, then extension point, then dependency swap, then own the bean. If the conditions report changes, it is not Level 2.
