# autopilot-airline: step 5 of 7

## Level 4 (part 1): the security defaults

**Tag:** `demo-4.0-security-defaults` · **Level 4** · ~1.5 min

Demo project for **Spring Boot Beyond the Defaults: A Practical Framework for Overriding Autoconfiguration**.

```bash
git checkout demo-4.0-security-defaults     # you are here
```

## What to say

- We add one dependency and write no security code. Spring Security autoconfigures an entire subsystem.
- Note the generated password in the log and the 401 on every request. The default chain protects everything, including the actuator.
- With Actuator on the classpath the default chain comes from `ManagementWebSecurityAutoConfiguration`. This is what we are about to take over.

## Key files to show

| File | Point at |
|---|---|
| `pom.xml` | One new dependency: `spring-boot-starter-security`. |

## Run it

```bash
./mvnw spring-boot:run                                           # log: Using generated security password: ...
curl -i localhost:8080/v1/bookings                                # 401 Unauthorized
CURL_AUTH=user:<generated-password> scripts/conditions.sh 'WebSecurity|UserDetails'
```

## What to expect

- `ManagementWebSecurityAutoConfiguration` (the default chain) and `UserDetailsServiceAutoConfiguration` (the generated user) are **positive**.
- `SecurityAutoConfiguration` is the entry point and stays positive for the rest of the demo.

## Next

```bash
git checkout demo-4.1-own-the-chain
```

## The demo path

| # | Checkout | Topic |
|---|---|---|
| 1 | `git checkout demo-0.0-baseline` | The autopilot is engaged |
| 2 | `git checkout demo-1.0-property-tuning` | Level 1: property tuning |
| 3 | `git checkout demo-2.0-extension-points` | Level 2: extension points |
| 4 | `git checkout demo-3.0-dependency-swap` | Level 3: dependency replacement |
| 5 | ➡ `git checkout demo-4.0-security-defaults` | Level 4 (part 1): the security defaults |
| 6 | `git checkout demo-4.1-own-the-chain` | Level 4 (part 2): own the chain, and make a mistake |
| 7 | `git checkout demo-4.2-own-the-chain-fixed` | Level 4 (part 3): the fix, and the cost of ownership |

## Quick reference

- Run: `./mvnw spring-boot:run` (port 8080, Java 21). Stop other copies first: `pkill -f autopilot-airline`.
- Cockpit: `scripts/conditions.sh [regex]` filters the Conditions Evaluation Report. `scripts/snapshot.sh <label>` saves it to `snapshots/<label>.txt`.
- From `demo-4.0-security-defaults` onward the actuator is protected: `CURL_AUTH=admin:admin123 scripts/conditions.sh` (at 4.0 use `user:<generated password>`).
- Demo accounts: `passenger:passenger123` (PASSENGER), `admin:admin123` (ADMIN). Passwords are `{noop}`, demo only.
- Slides: `cd slides && npm install && npm run dev`. The deck is in every commit, so checking out a step never removes it.
- Decision rule: property, then extension point, then dependency swap, then own the bean. If the conditions report changes, it is not Level 2.
