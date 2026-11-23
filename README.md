# autopilot-airline: step 2 of 7

## Level 1: property tuning

**Tag:** `demo-1.0-property-tuning` · **Level 1** · ~3 min

Demo project for **Spring Boot Beyond the Defaults: A Practical Framework for Overriding Autoconfiguration**.

```bash
git checkout demo-1.0-property-tuning     # you are here
```

## What to say

- Properties are the schema of an autoconfiguration. Boot still builds every bean. The autoconfiguration class just reads different values.
- This is the cheapest and safest intervention. It needs no Java, and a DevOps team can own it per environment (`application-prod.yaml`).
- The conditions report does not change, because nothing about who owns the bean changed.

## Key files to show

| File | Point at |
|---|---|
| `src/main/resources/application.yaml` | Four settings: Hikari pool size, `open-in-view: false`, `default-property-inclusion: non_null`, graceful shutdown. |

## Run it

```bash
git diff demo-0.0-baseline demo-1.0-property-tuning -- src/main/resources/application.yaml
./mvnw spring-boot:run
curl -s localhost:8080/v1/bookings/3 | python3 -m json.tool      # the nulls are gone
scripts/snapshot.sh after && diff snapshots/before.txt snapshots/after.txt
```

## What to expect

- `flightNumber: null` and `departureDate: null` no longer appear on the SEAT and MEAL items.
- The diff prints nothing: the conditions report is **identical** to the baseline.

## Next

```bash
git checkout demo-2.0-extension-points
```

## The demo path

| # | Checkout | Topic |
|---|---|---|
| 1 | `git checkout demo-0.0-baseline` | The autopilot is engaged |
| 2 | ➡ `git checkout demo-1.0-property-tuning` | Level 1: property tuning |
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
