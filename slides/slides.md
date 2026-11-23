---
theme: default
colorSchema: dark
title: "Spring Boot Beyond the Defaults: A Practical Framework for Overriding Autoconfiguration"
info: |
  30-minute live-coding talk. Demo repo: autopilot-airline.
class: text-left
transition: fade
mdc: true
lineNumbers: false
drawings:
  enabled: false
editor: false
---

<div class="title-slide">

<img src="/spring-boot-logo.svg" class="title-logo" alt="Spring Boot" />

<div class="title-text">

<div class="title-kicker">LIVE DEMO · FLYING ON AUTOPILOT</div>

# Spring Boot Beyond the Defaults

<div class="title-sub">A Practical Framework for Overriding Autoconfiguration</div>

<div class="title-levels">Configure &nbsp;·&nbsp; Extend &nbsp;·&nbsp; Replace &nbsp;·&nbsp; Take ownership</div>

<div class="title-speaker">Pradeep Kumar L &nbsp;·&nbsp; WeAreDevelopers 2026</div>

</div>

</div>

<!--
[0:00 | 1 min]
Open with the idea: Spring Boot is an autopilot. This talk is about when to adjust it,
when to feed it inputs, when to swap a component, and when to disengage it.
Repo: autopilot-airline (show URL once).
-->

---

# The problem

<v-clicks>

- Defaults are great — until they no longer fit
- Teams then reach for the **heaviest** tool first
- The result: bean replacement, framework fights, configuration sprawl

</v-clicks>

<!--
[1:00 | 1 min]
Ask: "Who has declared a DataSource or ObjectMapper bean 'just to change one thing'?"
Land: the question is not CAN you override, but at WHICH LEVEL.
-->

---

# Spring Boot is the autopilot

<div class="systems">
  <div class="sys"><b>Server</b><small>Tomcat</small></div>
  <div class="sys"><b>Web MVC</b><small>DispatcherServlet</small></div>
  <div class="sys"><b>JSON</b><small>Jackson</small></div>
  <div class="sys"><b>Validation</b><small>Hibernate Validator</small></div>
  <div class="sys"><b>Logging</b><small>Logback</small></div>
  <div class="sys"><b>DataSource</b><small>HikariCP</small></div>
  <div class="sys"><b>ORM</b><small>Hibernate</small></div>
  <div class="sys"><b>Transactions</b><small>JpaTransactionManager</small></div>
  <div class="sys"><b>Security</b><small>Default filter chain</small></div>
  <div class="sys"><b>Metrics</b><small>Micrometer + Actuator</small></div>
</div>

<div class="mt-6 text-xl">
Engaged before you touch anything — <b>reasonable defaults</b>, assembled for you.
</div>

<!--
[2:00 | 1 min]
Strength AND risk: it is engaged before you touch anything.
Cue: "Autopilot is not the enemy. The skill is knowing when to intervene."
-->

---

# Read the cockpit first

<div class="grid grid-cols-2 gap-8">
<div>

**Three inputs the engine evaluates**

1. Classpath — the jars present
2. Properties — bound values
3. Bean registry — beans already registered

</div>
<div>

**Instruments**

- `--debug` → Conditions Evaluation Report
- `/actuator/conditions`
- `mvn dependency:tree`

</div>
</div>

<div class="mt-8 opacity-80">
Imperative <code>@Bean</code> wiring → <b>declarative conditions</b>
</div>

<!--
[3:00 | 1 min]
Every override in this talk changes ONE of these three inputs.
The report is the proof. Same gesture every time: before / change / after.
-->

---
layout: center
class: text-center
---

# ▶ DEMO 0 — Baseline

`git checkout demo-0.0-baseline`

<div class="mt-6 opacity-70">
Start app · filter conditions report · GET /v1/bookings
</div>

<!--
[4:00 | 2 min]  git checkout demo-0.0-baseline
1. ./mvnw spring-boot:run     (or: java -jar target/*.jar after ./mvnw -DskipTests package)
2. curl -s localhost:8080/v1/bookings/3 | python3 -m json.tool     -> nulls on extras, ISO dates
3. scripts/conditions.sh 'JacksonAutoConfiguration#|DataSourceConfiguration\.|TomcatServlet'
   Point at: JacksonAutoConfiguration#jacksonJsonMapper, DataSourceConfiguration.Hikari, TomcatServletWebServerAutoConfiguration.
4. scripts/snapshot.sh before    <- every later demo diffs against this.
FALLBACK: slides/public/before.txt
-->

---

# The ladder: four levels of intervention

| Level | What changes | Conditions report |
|---|---|---|
| **1 · Properties** | A value the autoconfig reads | Unchanged |
| **2 · Customizers** | Behavior via an extension point | Unchanged |
| **3 · Dependency swap** | The library behind the bean | **Flips** |
| **4 · Ownership** | Who declares the bean | Default goes **negative** |

<div class="mt-6 text-xl">
Rule: stop at the <b>lowest level that works</b>.
</div>

<!--
[6:00 | 1 min]
THE slide of the talk. Come back to it at the end.
Control and effort both rise going down the table.
-->

---

# Level 1 — Property tuning

Fine-tune the default with the **least effort**. Spring Boot still builds the bean.

```yaml {all|2-4|6-8|10-11}
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
  jpa:
    hibernate:
      ddl-auto: validate
  jackson:
    default-property-inclusion: non_null
```

<div class="mt-4 opacity-80">Autopilot still flies — you changed a setting.</div>

<!--
[7:00 | 1 min]
Environment-specific files (dev/staging/prod) = no code change between deployments.
Cue: "pilot adjusts a setting".
-->

---
layout: center
class: text-center
---

# ▶ DEMO 1 — Properties

`git checkout demo-1.0-property-tuning`

<div class="mt-6 opacity-70">Edit YAML · restart · JSON changes · report identical</div>

<!--
[8:00 | 3 min]  git checkout demo-1.0-property-tuning      (show: git show demo-1.0-property-tuning --stat, then application.yaml)
1. Before: curl -s localhost:8080/v1/bookings/3 shows "flightNumber": null on the SEAT item.
2. Four lines of YAML. No Java. Restart.
3. After: the nulls are gone.
4. scripts/snapshot.sh after && diff snapshots/before.txt snapshots/after.txt   -> IDENTICAL. That is the point.
CUT-IF-LATE: skip pool size and graceful shutdown; keep the JSON nulls only.
-->

---

# Level 2 — Extension points

<v-clicks>

- **Properties are schema. Extension points are contracts.**
- The autoconfig *asks* for implementations of an interface and applies each one
- The bean stays Spring Boot's — what it **does** changes, not who owns it

</v-clicks>

<div v-click class="d-note">
  Conditions report: <b>unchanged</b> — the owning autoconfiguration stays in positive matches.
</div>

<!--
[12:00 | 1 min]
A waypoint the autopilot was built to accept. Not a replacement, not a new bean for the same type.
The test to remember: if the conditions report does NOT change, it is Level 2 (or Level 1).
-->

---

# Level 2 in the airline booking API

<div class="uc-grid">

<div v-click class="uc uc-mvc">
  <div class="uc-tag">WEB MVC</div>
  <div class="uc-hook">WebMvcConfigurer</div>
  <p>The booking web app runs on another origin, so the API needs <b>CORS</b>. Every request also needs a <b>correlation ID</b>.</p>
</div>

<div v-click class="uc uc-jpa">
  <div class="uc-tag">PERSISTENCE</div>
  <div class="uc-hook">HibernatePropertiesCustomizer</div>
  <p>Tag every SQL statement with the service name so the DBA can trace a <b>slow query</b> back to the booking service.</p>
</div>

<div v-click class="uc uc-json">
  <div class="uc-tag">JSON</div>
  <div class="uc-hook">JsonMapperBuilderCustomizer</div>
  <p>Partner systems expect departure dates as <b>22-Nov-2026</b>, not ISO-8601.</p>
</div>

</div>

<div v-click class="mt-6 text-xl">
Same autoconfiguration. Same owner. <b>Different behavior.</b>
</div>

<!--
[13:00 | 1 min]
[click] Web MVC: WebMvcAutoConfiguration collects every WebMvcConfigurer bean. CORS and interceptors are exactly what it was designed to delegate. Live demo.

[click] Hibernate: HibernateJpaAutoConfiguration applies every HibernatePropertiesCustomizer before it builds the EntityManagerFactory. Good observability story for a DBA.

[click] Jackson: JacksonAutoConfiguration applies every JsonMapperBuilderCustomizer to the builder before it creates the JsonMapper.

[click] Land the sentence: same autoconfiguration, same owner, different behavior. The report will not move.
-->

---

# Web MVC: CORS and a correlation ID

```java {all|3-7|8-11}
@Configuration
class BookingWebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/v1/**")
                .allowedOrigins("https://book.example-air.com");
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CorrelationIdInterceptor());
    }
}
```

<div class="mt-4 opacity-80">
No <code>@EnableWebMvc</code>. No <code>WebMvcConfigurationSupport</code>. Boot's MVC defaults stay on.
</div>

<!--
[14:00 | 1 min]
Point out what is NOT here: no @EnableWebMvc (that would switch Boot's MVC autoconfig off and be an ownership transfer).
The configurer only adds. Everything Boot configured for MVC is still there.
-->

---

# Looks like Level 2 — but isn't

<div class="trap-grid">

<div v-click class="trap trap-sec">
  <div class="uc-tag">FILTER CHAIN</div>
  <div class="uc-hook">SecurityFilterChain</div>
  <p>Declare one and the default chain <b>backs off</b>. The report changes, so it is <b>Level 4</b> — see Demo 4. Expected usage, but you own the chain.</p>
</div>

<div v-click class="trap trap-ds">
  <div class="uc-tag">DATASOURCE</div>
  <div class="uc-hook">No DataSourceCustomizer exists</div>
  <p>Tune the pool with <code>spring.datasource.hikari.*</code> (Level 1) or swap the pool (Level 3). <code>JdbcConnectionDetails</code> supplies URL and credentials only.</p>
</div>

</div>

<div v-click class="d-note">
  The test: <b>if the conditions report changes, it is not Level 2.</b>
</div>

<!--
[15:00 | 30 sec]
Short callout, not a demo. Pre-empts the question "why isn't there a DataSource customizer?".
SecurityFilterChain: Spring Security's documented extension model - so it is fine to declare - but the report shows the default going negative.
-->

---
layout: center
class: text-center
---

# ▶ DEMO 2 — Customizers

`git checkout demo-2.0-extension-points`

<div class="mt-6 opacity-70">CORS + correlation ID · date format · report still positive</div>

<!--
[15:30 | 3 min]  git checkout demo-2.0-extension-points      (git show demo-2.0-extension-points --stat lists the four new files)
1. MVC:   curl -si -H "Origin: https://book.example-air.com" localhost:8080/v1/bookings/1 | grep -iE 'access-control|x-correlation'
          Files: config/BookingWebConfig.java, CorrelationIdInterceptor.java
2. Jackson: curl -s localhost:8080/v1/bookings/1   -> "departureDate": "22-Nov-2026"
          File: config/DepartureDateFormatCustomizer.java
3. Hibernate (optional): console shows  Hibernate: /* booking-service */ select ...
          File: config/SqlTaggingCustomizer.java
4. scripts/snapshot.sh after && diff snapshots/before.txt snapshots/after.txt  -> IDENTICAL. JacksonAutoConfiguration#jacksonJsonMapper still positive.
Remember the Jackson customizer: the Level 4 teaser takes it away.
-->

---

# Level 3 — Dependency replacement

When the **implementation itself** is the constraint — not its config or behavior.

```text
mvn dependency:tree
 └─ spring-boot-starter-web
     └─ spring-boot-starter-tomcat   ← exclude
```

<v-clicks>

- Classpath changes → the conditions engine activates a **different** path
- Safe when code depends on an **abstraction** (`DataSource`, SLF4J, Servlet API)
- Costly when you import provider-specific classes

</v-clicks>

<!--
[17:00 | 1 min]
Swap the engine, keep the airframe.
Real cases: Tomcat -> Jetty, Hikari -> DBCP2, Logback -> Log4j2.
Undertow was dropped in Boot 4 (verify) - use Jetty.
Do NOT promise "swap Hibernate for another ORM" - not a jar swap.
-->

---
layout: center
class: text-center
---

# ▶ DEMO 3 — Swap the engine

`git checkout demo-3.0-dependency-swap`

<div class="mt-6 opacity-70">Exclude Tomcat · add Jetty · the report flips</div>

<!--
[18:00 | 4 min]  git checkout demo-3.0-dependency-swap      (one commit: Tomcat to Jetty AND HikariCP to DBCP2; both are pom.xml-only changes)
1. ./mvnw dependency:tree | grep -iE 'tomcat|hikari'   -> starter-web brings starter-tomcat; starter-data-jpa brings HikariCP
2. git show demo-3.0-dependency-swap -- pom.xml            -> two exclusions + spring-boot-starter-jetty + commons-dbcp2. No Java touched.
3. Restart. Log: "Jetty started on port 8080".
4. scripts/conditions.sh 'Tomcat|Jetty|DataSourceConfiguration\.'
   Jetty positive; Tomcat entries VANISH (module not on the classpath); Dbcp2 positive, Hikari negative.
   Gotcha: spring.datasource.hikari.* would be silently ignored, so the YAML moved to dbcp2.max-total.
FALLBACK: pre-recorded clip slides/public/demo3.mp4 (Maven downloads are the #1 live failure).
CUT-IF-LATE: show only the Jetty half of the pom diff.
-->

---

# Level 4 — Ownership transfer

```java {all|2|4}
// Actuator is on the classpath, so the default chain lives here:
@ConditionalOnDefaultWebSecurity   // no SecurityFilterChain bean?
class ManagementWebSecurityAutoConfiguration {
    @Bean SecurityFilterChain managementSecurityFilterChain(...) { ... }
}
```

<v-clicks>

- The guard is the **handover agreement** (`@ConditionalOnMissingBean` in its many forms)
- Declare the bean → the default **backs off**, and the report records it
- You now own **everything it was doing** — including what you didn't know about

</v-clicks>

<!--
[21:00 | 1 min]
Disengage the autopilot. Manual flight.
This is not "bean replacement" - it is a transfer of responsibility.
Spring Security's guard is a composed condition (ConditionalOnDefaultWebSecurity); JsonMapper uses plain @ConditionalOnMissingBean. Same idea.
-->

---

# How the defaults back off

<div class="flow">

<div class="fl-head">Spring Boot default</div>
<div class="fl-head">Condition checked at startup</div>
<div class="fl-head">Our app registers</div>

<div class="fl-default" :class="$clicks >= 2 ? 'fl-off' : ''">
  <b>ManagementWebSecurityAutoConfiguration</b>
  <small>default chain <code>managementSecurityFilterChain</code></small>
  <div class="stack">
    <div v-click.hide="2" class="fl-status ok">✓ registered</div>
    <div v-click="2" class="fl-status no">✕ backs off</div>
  </div>
</div>
<div class="fl-cond">
  <code>@ConditionalOnDefaultWebSecurity</code>
  <div class="stack">
    <small v-click.hide="2">no <code>SecurityFilterChain</code> bean found → register the default</small>
    <small v-click="2">found <code>securityFilterChain</code> → do not register</small>
  </div>
</div>
<div v-click="1" class="fl-ours">
  <b>securityFilterChain</b>
  <small>our <code>BookingSecurityConfig</code></small>
</div>

<div class="fl-default" :class="$clicks >= 2 ? 'fl-off' : ''">
  <b>UserDetailsServiceAutoConfiguration</b>
  <small>in-memory user, generated password</small>
  <div class="stack">
    <div v-click.hide="2" class="fl-status ok">✓ registered</div>
    <div v-click="2" class="fl-status no">✕ backs off</div>
  </div>
</div>
<div class="fl-cond">
  <code>@ConditionalOnMissingBean</code>
  <div class="stack">
    <small v-click.hide="2">no <code>UserDetailsService</code> / <code>AuthenticationProvider</code> bean → register the default</small>
    <small v-click="2">found <code>userDetailsService</code> → do not register</small>
  </div>
</div>
<div v-click="1" class="fl-ours">
  <b>userDetailsService</b>
  <small>our in-memory <code>passenger</code> and <code>admin</code> users</small>
</div>

</div>

<pre v-click="3" class="fl-report">Negative matches:
  ManagementWebSecurityAutoConfiguration:
    Did not match: found beans of type 'SecurityFilterChain' securityFilterChain</pre>

<!--
[22:00 | 1.5 min]
[click] Start with what we register: a SecurityFilterChain and a UserDetailsService. Our beans enter the ApplicationContext.

[click] Now the conditions engine evaluates the defaults against the bean registry. It finds our beans, so both defaults back off. Nothing is deleted - the default is simply never registered.

[click] This is the evidence: a negative match that names the exact bean that caused it. One line per handover.
Before this slide the defaults are green: the guard found no bean, so Boot registered them.
-->

---

# What you inherit

```text
The default SecurityFilterChain was doing:
  ├── every request must be authenticated (the actuator too)
  ├── HTTP Basic and form login
  ├── CSRF protection and session handling
  └── a user to log in with (generated password in the log)
```

<div class="mt-6 text-xl">
Your chain starts empty. <b>Whatever you leave out is not there.</b>
</div>

<!--
[23:30 | 0.5 min]
Set up Demo 4: predict what breaks. Ask the room: "what happens if I declare a chain and forget the authorization rule?"
Aside: JsonMapper is the same story - the date customizer silently stops applying.
-->

---
layout: center
class: text-center
---

# ▶ DEMO 4 — Take manual control

`git checkout demo-4.0-security-defaults`  →  4.1  →  4.2

<div class="mt-6 opacity-70">Security starter · own the filter chain · forget a rule · fix it</div>

<!--
[24:00 | 5 min]  Level 4. Tags: demo-4.0-security-defaults -> demo-4.1-own-the-chain -> demo-4.2-own-the-chain-fixed
1. DEFAULTS  git checkout demo-4.0-security-defaults
   - Log: "Using generated security password: ..."
   - curl -i localhost:8080/v1/bookings   -> 401
   - CURL_AUTH=user:<generated> scripts/conditions.sh 'WebSecurity|UserDetails'
     positive: ManagementWebSecurityAutoConfiguration (the default chain), UserDetailsServiceAutoConfiguration
2. OWN IT    git checkout demo-4.1-own-the-chain        (git show demo-4.1-own-the-chain: config/BookingSecurityConfig.java)
   - Generated password is gone. Report: both defaults NEGATIVE, naming securityFilterChain and userDetailsService.
3. THE MISTAKE
   - curl -i localhost:8080/v1/bookings     -> 200, NO credentials. curl localhost:8080/actuator/beans -> 200 too.
4. FIX       git checkout demo-4.2-own-the-chain-fixed
   - no creds 401 | passenger:passenger123 -> 403 on the list, 200 on /v1/bookings/1 | admin:admin123 -> 200
   - The conditions report is the SAME as step 2: it proves ownership, not correctness.
5. TEASER (30s, slide only, not in the repo)
   - A hand-built @Bean JsonMapper (snake_case) makes JacksonAutoConfiguration#jacksonJsonMapper NEGATIVE and silently drops
     the dd-MMM-yyyy format and the null handling. Nothing fails at startup.
-->

---

# The cost of ownership

<v-clicks>

- Spring Boot upgrades update the autoconfig — **not your bean**
- You inherit the framework's integration testing burden
- Security is the clearest case: **a missing rule is an open door**
- Know the cost **before** the decision, not after

</v-clicks>

<!--
[27:00 | 1 min]
Not a reason to avoid ownership - an accurate price tag.
-->

---

# Choose the right level

<div class="decide">

<div v-click class="d-row">
  <div class="d-q">Is there a <b>property</b> for it?</div>
  <div class="d-arrow ok">yes →</div>
  <div class="d-lvl l1"><span>1</span> Tune it</div>
  <div class="d-no">no ↓</div>
</div>

<div v-click class="d-row">
  <div class="d-q">Is there an <b>extension point</b>?</div>
  <div class="d-arrow ok">yes →</div>
  <div class="d-lvl l2"><span>2</span> Customize it</div>
  <div class="d-no">no ↓</div>
</div>

<div v-click class="d-row">
  <div class="d-q">Is only the <b>library</b> wrong?</div>
  <div class="d-arrow ok">yes →</div>
  <div class="d-lvl l3"><span>3</span> Swap the dependency</div>
  <div class="d-no">no ↓</div>
</div>

<div v-click class="d-row">
  <div class="d-q">Nothing above fits</div>
  <div class="d-arrow ok">then →</div>
  <div class="d-lvl l4"><span>4</span> Own the bean</div>
</div>

</div>

<!--
[28:00 | 1 min]
Walk the ladder top to bottom. Stop at the first "yes" — that is the whole rule.

[click] First question: is there a property? If yes, use it. Cheapest, safest, and Spring Boot keeps ownership.

[click] No property? Ask whether the autoconfiguration publishes an extension point — a Customizer or Configurer. You change behavior, Boot keeps the bean.

[click] No extension point? Check whether only the library is the problem. Then it is a classpath change: exclude one jar, add another, and the conditions engine does the rest.

[click] Only when none of those fit do you own the bean. Say the cost again: every future Boot change to that bean is now yours.
-->

---

# Takeaways

1. **Read the report** before touching anything
2. Stop at the **lowest level** that works
3. Ownership is a **decision**, not a reflex

<div class="mt-10 opacity-70">
Next: from owning a bean to extracting a custom starter
</div>

<!--
[29:00 | 1 min]
Close the flight metaphor: a good pilot knows when NOT to disengage the autopilot.
Then Q&A / repo URL.
-->

---
layout: center
class: text-center
---

# Questions?

**github.com/&lt;you&gt;/autopilot-airline**
