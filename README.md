# autopilot-airline: step 4 of 7

## Level 3: dependency replacement

**Tag:** `demo-3.0-dependency-swap` · **Level 3** · ~4 min

Demo project for **Spring Boot Beyond the Defaults: A Practical Framework for Overriding Autoconfiguration**.

```bash
git checkout demo-3.0-dependency-swap     # you are here
```

## What to say

- When the implementation itself is the constraint, neither a property nor a customizer helps. Change the classpath and the conditions engine picks a different path.
- Two swaps, one commit, `pom.xml` only: Tomcat to Jetty, and HikariCP to Apache Commons DBCP2. No Java file is touched.
- It is safe because the code depends on abstractions (the Servlet API, `javax.sql.DataSource`). It is costly when you import provider-specific classes.
- Watch out: properties are tied to the implementation. `spring.datasource.hikari.*` would be silently ignored, so the YAML moved to `dbcp2.max-total`.

## Key files to show

| File | Point at |
|---|---|
| `pom.xml` | Two exclusions (`spring-boot-starter-tomcat`, `HikariCP`) and two additions (`spring-boot-starter-jetty`, `commons-dbcp2`). |
| `src/main/resources/application.yaml` | The pool size property moved from `hikari.maximum-pool-size` to `dbcp2.max-total`. |

## Run it

```bash
./mvnw dependency:tree | grep -iE 'jetty|tomcat|hikari|dbcp'
git show demo-3.0-dependency-swap -- pom.xml
./mvnw spring-boot:run                                           # log: Jetty started on port 8080
scripts/conditions.sh 'Tomcat|Jetty|DataSourceConfiguration\.'
```

## What to expect

- `JettyServletWebServerAutoConfiguration` and `DataSourceConfiguration.Dbcp2` are **positive**.
- `DataSourceConfiguration.Hikari` is **negative** (class not found). The Tomcat entries **vanish** from the report because the module is no longer on the classpath.
- This is the first level where the report changes.

## Next

```bash
git checkout demo-4.0-security-defaults
```

## The demo path

| # | Checkout | Topic |
|---|---|---|
| 1 | `git checkout demo-0.0-baseline` | The autopilot is engaged |
| 2 | `git checkout demo-1.0-property-tuning` | Level 1: property tuning |
| 3 | `git checkout demo-2.0-extension-points` | Level 2: extension points |
| 4 | ➡ `git checkout demo-3.0-dependency-swap` | Level 3: dependency replacement |
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
