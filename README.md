# autopilot-airline: step 6 of 7

## Level 4 (part 2): own the chain, and make a mistake

**Tag:** `demo-4.1-own-the-chain` · **Level 4** · ~2 min

Demo project for **Spring Boot Beyond the Defaults: A Practical Framework for Overriding Autoconfiguration**.

```bash
git checkout demo-4.1-own-the-chain     # you are here
```

## What to say

- Ownership transfer. Declare a `SecurityFilterChain` and a `UserDetailsService` and the defaults back off. Show the diagram slide: our beans in, the defaults out.
- The report records it precisely: `found beans of type 'SecurityFilterChain' securityFilterChain`. The generated password is gone from the log.
- Now the cost. I wrote only `httpBasic()`. The default chain authenticated every request. Mine does not, because I never said so. Call the API with no credentials.

## Key files to show

| File | Point at |
|---|---|
| `src/main/java/com/autopilot/airline/config/BookingSecurityConfig.java` | Two beans: `securityFilterChain` (only `httpBasic()`) and `userDetailsService` (in-memory `passenger` and `admin`). Point at the missing `authorizeHttpRequests`. |

## Run it

```bash
./mvnw spring-boot:run                                           # no generated password in the log
CURL_AUTH=admin:admin123 scripts/conditions.sh 'WebSecurity|UserDetails'
curl -i localhost:8080/v1/bookings                                # 200: wide open, no credentials
curl -i localhost:8080/actuator/beans                             # 200: the actuator is open too
```

## What to expect

- `ManagementWebSecurityAutoConfiguration` and `UserDetailsServiceAutoConfiguration` are **negative**, each naming the bean that caused it.
- The API and the actuator answer **200 without credentials**. The app starts fine and nothing fails.

## Next

```bash
git checkout demo-4.2-own-the-chain-fixed
```

## The demo path

| # | Checkout | Topic |
|---|---|---|
| 1 | `git checkout demo-0.0-baseline` | The autopilot is engaged |
| 2 | `git checkout demo-1.0-property-tuning` | Level 1: property tuning |
| 3 | `git checkout demo-2.0-extension-points` | Level 2: extension points |
| 4 | `git checkout demo-3.0-dependency-swap` | Level 3: dependency replacement |
| 5 | `git checkout demo-4.0-security-defaults` | Level 4 (part 1): the security defaults |
| 6 | ➡ `git checkout demo-4.1-own-the-chain` | Level 4 (part 2): own the chain, and make a mistake |
| 7 | `git checkout demo-4.2-own-the-chain-fixed` | Level 4 (part 3): the fix, and the cost of ownership |

## Quick reference

- Run: `./mvnw spring-boot:run` (port 8080, Java 21). Stop other copies first: `pkill -f autopilot-airline`.
- Cockpit: `scripts/conditions.sh [regex]` filters the Conditions Evaluation Report. `scripts/snapshot.sh <label>` saves it to `snapshots/<label>.txt`.
- From `demo-4.0-security-defaults` onward the actuator is protected: `CURL_AUTH=admin:admin123 scripts/conditions.sh` (at 4.0 use `user:<generated password>`).
- Demo accounts: `passenger:passenger123` (PASSENGER), `admin:admin123` (ADMIN). Passwords are `{noop}`, demo only.
- Slides: `cd slides && npm install && npm run dev`. The deck is in every commit, so checking out a step never removes it.
- Decision rule: property, then extension point, then dependency swap, then own the bean. If the conditions report changes, it is not Level 2.
