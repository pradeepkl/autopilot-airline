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

# About me

<div class="author-grid">

<div class="book-stack">

<div class="book-row">
  <img src="/book-spring-boot-on-autopilot.webp" alt="Spring Boot: On Autopilot cover" />
  <div>
    <div class="book-title">Spring Boot: On Autopilot</div>
    <small>Engineering Beyond Defaults · Spring Boot 4</small>
    <div class="book-url">spring-boot.classpath.in</div>
  </div>
</div>

<div class="book-row">
  <img src="/book-modern-java.jpg" alt="Modern Java: The Mindset Shift cover" />
  <div>
    <div class="book-title">Modern Java: The Mindset Shift</div>
    <small>From Implementation-Oriented Code to Intent-Oriented Design · Java 25</small>
    <div class="book-url">modern-java.classpath.in</div>
  </div>
</div>

<div class="book-row">
  <img src="/book-k8s-mastery.webp" alt="21 Days to Kubernetes Mastery cover" />
  <div>
    <div class="book-title">21 Days to Kubernetes Mastery</div>
    <small>An Actionable Roadmap to Kubernetes</small>
    <div class="book-url">k8s-mastery.classpath.dev</div>
  </div>
</div>

</div>

<div class="about-me">

<img class="speaker-photo" src="/speaker.jpeg" alt="Pradeep Kumar L" />

## Pradeep Kumar L

- Consultant
- Just Author

</div>

</div>

<!--
[0:30 | 30 sec]
Quick intro: I am a consultant and the author of three books. Spring Boot: On Autopilot (Spring Boot 4) is where this talk's framework comes from. Modern Java: The Mindset Shift (Java 25) is the companion on the language side. 21 Days to Kubernetes Mastery covers running it all in production.
Covers: spring-boot.classpath.in, modern-java.classpath.in and k8s-mastery.classpath.dev.
Do not linger - one line, then move on to the autopilot idea.
-->

---

# The magic of autoconfiguration

- **Behind the scenes:** Spring Boot autoconfiguration wires your infrastructure from sensible defaults, before you write any configuration
- **Your role:** developers wear the pilot's hat, flying in **autopilot mode**
- **How you read it:** the instruments are **declarative**, not imperative: Actuator, the conditions report, starters for dependencies
- **The catch:** defaults are great for bootstrapping, but to **extend and customize** beyond them you must understand how the app is configured


<div class="mt-8 text-xl opacity-90">
✨ Spring Boot works like magic. Magic is good, but to be a <b>magician</b> you need to know the tricks.
</div>

<!--
[1:00 | 1 min]
Foundation of the talk: before we override anything, agree on what the autopilot is and how to read it.
Autoconfiguration = defaults chosen from classpath, properties and existing beans. Starters bring the dependencies, conditions decide what is wired.
Instruments (Actuator, conditions report) are how a pilot checks what the autopilot is doing, without writing wiring code.
Caption: magic is good - nobody is saying don't use it. But a magician knows the trick behind it, and that is what lets you change the show.
Land: sensible defaults are great for bootstrapping - and to go beyond them you need to know the tricks.
-->

---

# What the autopilot wires for you

<div class="layers">

<div class="uc layer l-jars">
  <div class="uc-tag">1 · JARS</div>
  <div class="uc-hook">Starters</div>
  <p>One dependency pulls a <b>group of jars</b> with compatible versions. You never pick the versions.</p>
  <ul>
    <li><code>starter-web</code></li>
    <li><code>starter-data-jpa</code></li>
    <li><code>starter-security</code></li>
  </ul>
</div>

<div class="layer-arrow">→</div>

<div class="uc layer l-auto">
  <div class="uc-tag">2 · AUTOCONFIGURATION</div>
  <div class="uc-hook">Conditions</div>
  <p>Evaluates the <b>classpath</b>, <b>properties</b> and <b>existing beans</b>, then decides what to register.</p>
  <ul>
    <li><code>WebMvcAutoConfiguration</code></li>
    <li><code>DataSourceAutoConfiguration</code></li>
    <li><code>JacksonAutoConfiguration</code></li>
  </ul>
</div>

<div class="layer-arrow">→</div>

<div class="uc layer l-beans">
  <div class="uc-tag">3 · INFRA BEANS</div>
  <div class="uc-hook">Registered for you</div>
  <p>The plumbing you never declared:</p>
  <ul>
    <li>Embedded <b>Tomcat</b></li>
    <li><b>DispatcherServlet</b></li>
    <li><b>HandlerMapping</b> / HandlerAdapter</li>
    <li><b>DataSource</b> (HikariCP)</li>
    <li><b>EntityManagerFactory</b>, <b>JsonMapper</b></li>
  </ul>
</div>

</div>

<div class="mt-6 text-xl">
Add a starter, get a working subsystem. <b>Nobody wrote the wiring, but you own the outcome.</b>
</div>

<!--
[2:00 | 1 min]
Walk left to right.
Jars: a starter is a group of jars, versions managed by the Boot BOM. You say "I want web", not "Tomcat 11 + Spring MVC 7 + Jackson x.y".
Autoconfiguration: the conditions engine looks at three inputs - classpath, properties, existing beans - and decides what to register.
Infra beans: embedded Tomcat, DispatcherServlet, handler mappings, DataSource, EntityManagerFactory, JsonMapper. None of them is in our code.
Strength AND risk: it is engaged before you touch anything. "Autopilot is not the enemy. The skill is knowing when to intervene."
-->

---

# Read the cockpit instruments

<div class="cockpit-wrap">

<div class="instr-table">

| Instrument | Reads | Maps to |
|---|---|---|
| 🎛️ Autopilot status | `--debug` | What engaged, and why not |
| 📦 Cargo manifest | `mvn dependency:tree` | **Classpath** |
| 🎚️ Cockpit settings | `/actuator/env` | **Properties** |
| 🗺️ Wiring diagram | `/actuator/beans` | **Bean registry** |

</div>

<div class="pilot-focus">
  <div class="pf-head">👨‍✈️ THE PILOT'S FOCUS</div>
  <div class="pf-card pf-atc">
    <div class="pf-icon">📡</div>
    <div><b>ATC communication</b><small>REST contracts, JSON formats, CORS, partner integrations</small></div>
  </div>
  <div class="pf-card pf-safe">
    <div class="pf-icon">🛟</div>
    <div><b>Passenger safety</b><small>authentication, authorization, validation, transactions</small></div>
  </div>
  <div class="pf-auto">⚙️ The autopilot does the heavy lifting: wiring, pooling, threading, serialization</div>
</div>

</div>

<!--
[3:00 | 1 min]
A pilot does not hand-fly every system. They read the instruments and trust the autopilot for the heavy lifting.
Instruments map to the three inputs the conditions engine evaluates: cargo manifest = classpath, cockpit settings = properties, wiring diagram = bean registry.
Autopilot status (--debug / conditions report) is the proof - the same gesture every time: before / change / after.
The pilot's focus is the mission: ATC communication (our API contracts, partner systems) and passenger safety (security, validation, transactions). Let the instruments and the autopilot handle the internals.
Every override in this talk changes ONE of the three inputs.
-->

---

# Defaults, effort and control

<ConfigSlider />

<div class="mt-6 text-xl">
Most of an app stays on the defaults. <b>Every step away from them costs effort and buys control.</b>
</div>

<!--
[4:00 | 1 min]
Interactive: drag the slider. It starts at 80% defaults - most of a real app is Spring Boot's sensible defaults.
Left: effort. Right: control. Both rise as you move away from the defaults, and effort rises faster than control (the percentages are illustrative, not measured).
The chip under the slider names the level you are on - the ladder is next: properties, customizers, dependency swap, ownership.
Land: leave the slider where it is until you NEED more control. Do not buy effort you do not need.
-->

---

# Why customize?

<div class="needs">

<div class="need n1">
  <div class="n-icon">📈</div>
  <div class="n-text"><b>Production load is higher than the defaults assume</b><small>connection pool size, JSON null handling</small></div>
  <div class="n-arrow">→</div>
  <div class="n-chip"><span>1</span> Tune a property</div>
</div>

<div class="need n2">
  <div class="n-icon">🤝</div>
  <div class="n-text"><b>Partners and web apps expect something specific</b><small>CORS, correlation ID, <code>22-Nov-2026</code> dates</small></div>
  <div class="n-arrow">→</div>
  <div class="n-chip"><span>2</span> Plug into an extension point</div>
</div>

<div class="need n3">
  <div class="n-icon">🧩</div>
  <div class="n-text"><b>The library itself is the constraint</b><small>company standard says Jetty and DBCP2, not Tomcat and Hikari</small></div>
  <div class="n-arrow">→</div>
  <div class="n-chip"><span>3</span> Swap the dependency</div>
</div>

<div class="need n4">
  <div class="n-icon">🛂</div>
  <div class="n-text"><b>Your rules replace the framework's</b><small>your own security filter chain and users</small></div>
  <div class="n-arrow">→</div>
  <div class="n-chip"><span>4</span> Own the bean</div>
</div>

</div>

<div class="mt-4 text-xl">
The question is not <i>can</i> you override, but at <b>which level</b>.
</div>

<!--
[4:00 | 1 min]
Defaults are great until they no longer fit. Real requirements arrive: load, partners, company standards, security rules.
Teams then reach for the HEAVIEST tool first: bean replacement, framework fights, configuration sprawl.
Each need maps to a different kind of override, from lightest to heaviest. Do not explain them yet - the ladder next slide does.
Land: the question is not CAN you override, but at WHICH LEVEL.
-->

---

# The ladder: four levels of intervention

<div class="ladder-wrap">

<div class="axis ax-effort">
  <div class="ax-label">EFFORT</div>
  <div class="ax-head"></div>
  <div class="ax-line"></div>
</div>

<div class="ladder">
  <div class="rung r4">
    <div class="r-lvl">4</div>
    <div class="r-main"><b>Ownership</b><small>Who declares the bean</small></div>
    <div class="r-rep"><small>Conditions report</small>Default goes <b>negative</b></div>
  </div>
  <div class="rung r3">
    <div class="r-lvl">3</div>
    <div class="r-main"><b>Dependency swap</b><small>The library behind the bean</small></div>
    <div class="r-rep"><small>Conditions report</small><b>Flips</b></div>
  </div>
  <div class="rung r2">
    <div class="r-lvl">2</div>
    <div class="r-main"><b>Customizers</b><small>Behavior via an extension point</small></div>
    <div class="r-rep"><small>Conditions report</small>Unchanged</div>
  </div>
  <div class="rung r1">
    <div class="r-lvl">1</div>
    <div class="r-main"><b>Properties</b><small>A value the autoconfig reads</small></div>
    <div class="r-rep"><small>Conditions report</small>Unchanged</div>
  </div>
</div>

<div class="axis ax-control">
  <div class="ax-label">CONTROL</div>
  <div class="ax-head"></div>
  <div class="ax-line"></div>
</div>

</div>

<div class="mt-4 text-xl">
Climb for more control, pay in effort. Rule: stop at the <b>lowest level that works</b>.
</div>

<!--
[6:00 | 1 min]
THE slide of the talk. Come back to it at the end.
Control and effort both rise as you climb the ladder: the two arrows on the sides.
-->

---

# Level 1 — Property tuning

Turn the dial, don't rewire the cockpit. **No bean changes, no behavior changes**, just different inputs to the bean Spring Boot already builds.

<div class="grid grid-cols-2 gap-6 mt-4">
<div style="--slidev-code-font-size: 0.95rem; --slidev-code-line-height: 1.5">

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST}:5432/airline
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
management:
  endpoint:
    health:
      probes:
        enabled: true
```

</div>
<div class="flex flex-col gap-3 justify-center">
  <div class="pf-card" style="--c: #4aa3ff">
    <div class="pf-icon">🗄️</div>
    <div><b>DB configuration</b><small>which database the DataSource connects to, secrets from the environment</small></div>
  </div>
  <div class="pf-card" style="--c: #f5b83d">
    <div class="pf-icon">🏊</div>
    <div><b>Hikari pool settings</b><small>how many connections the pool keeps open</small></div>
  </div>
  <div class="pf-card" style="--c: #6db33f">
    <div class="pf-icon">🩺</div>
    <div><b>Health endpoints and probes</b><small>liveness and readiness for Kubernetes</small></div>
  </div>
</div>
</div>

<div class="mt-4 opacity-80">Autopilot still flies — you changed a setting.</div>

<!--
[7:00 | 1 min]
Level 1 = tweak the parameters. The bean is still built by Spring Boot, same type, same behavior - only the inputs change.
Walk the three highlights:
1. DB configuration: url/username/password. Same DataSource bean, pointed somewhere else. Secrets come from environment variables, not the repo.
2. Hikari pool settings: spring.datasource.hikari.* feeds the pool Boot already creates.
3. Health endpoints and probes: management.endpoint.health.probes.enabled=true adds /actuator/health/liveness and /actuator/health/readiness. No code.
Environment-specific files (dev/staging/prod) = no code change between deployments.
Cue: "pilot adjusts a setting".
-->

---

# Property tuning in practice

<div class="instr-table">

| Use case | Property | Effect |
|---|---|---|
| 🛡️ Protect the schema | `spring.jpa.hibernate.ddl-auto=validate` | Hibernate checks the schema, never changes it |
| 🧹 Clean JSON | `spring.jackson.default-property-inclusion=non_null` | Null fields disappear from API responses |
| 🛬 Graceful landing | `server.shutdown=graceful` | In-flight requests finish before stop |
| 🔊 Quieter cockpit | `logging.level.org.hibernate.SQL=DEBUG` | Turn SQL logging up or down per environment |

</div>

<div class="mt-4 text-xl">
Same bean. Same behavior. <b>Different inputs.</b> The conditions report does not move.
</div>

<!--
[7:45 | 45 sec]
Practical, everyday Level 1 tweaks. Each one is a value an autoconfiguration already reads.
Link to the demo: the JSON row is exactly what we change next (nulls on the seat item disappear).
Analogy: turning a knob on the instrument panel, not changing the instrument.
-->

---

# Level 2 — Extension points

Plug into the slot, don't replace the bean. **Spring Boot still owns the bean**; it asks for implementations of an interface and applies each one. Properties are schema, extension points are contracts.

<div class="grid grid-cols-2 gap-6 mt-4">
<div style="--slidev-code-font-size: 0.95rem; --slidev-code-line-height: 1.5">

```java
class BookingWebConfig
    implements WebMvcConfigurer
class SqlTaggingCustomizer
    implements HibernatePropertiesCustomizer
class DepartureDateFormatCustomizer
    implements JsonMapperBuilderCustomizer
```

</div>
<div class="flex flex-col gap-3 justify-center">
  <div class="pf-card" style="--c: #4aa3ff">
    <div class="pf-icon">🌐</div>
    <div><b>Web MVC</b><small>CORS for the web app, a correlation ID on every request</small></div>
  </div>
  <div class="pf-card" style="--c: #b48cff">
    <div class="pf-icon">🗄️</div>
    <div><b>Persistence</b><small>tag every SQL statement with the service name</small></div>
  </div>
  <div class="pf-card" style="--c: #2dd4bf">
    <div class="pf-icon">🧾</div>
    <div><b>JSON</b><small>dates as <code>22-Nov-2026</code> for partner systems</small></div>
  </div>
</div>
</div>

<div class="mt-4 opacity-80">Autopilot still flies — you added a waypoint.</div>

<!--
[12:00 | 1 min]
Level 2 = plug into a slot the autoconfiguration was built to accept. Not a replacement, not a new bean for the same type.
Walk the three highlights:
1. Web MVC: WebMvcAutoConfiguration collects every WebMvcConfigurer bean. CORS and interceptors are exactly what it delegates. No @EnableWebMvc - that would switch Boot's MVC autoconfig off and be an ownership transfer.
2. Hibernate: HibernateJpaAutoConfiguration applies every HibernatePropertiesCustomizer before it builds the EntityManagerFactory. Good observability story for a DBA.
3. Jackson: JacksonAutoConfiguration applies every JsonMapperBuilderCustomizer to the builder before it creates the JsonMapper.
Cue: "pilot adds a waypoint". The test to remember: if the conditions report does NOT change, it is Level 2 (or Level 1).
-->

---

# Extension points in practice

<div class="instr-table">

| Use case | Extension point | Effect |
|---|---|---|
| 🌐 CORS for the web app | `WebMvcConfigurer` | The booking site's origin may call `/v1/**` |
| 🔗 Correlation ID | `WebMvcConfigurer` interceptor | Every request can be traced across services |
| 🏷️ Tag SQL for the DBA | `HibernatePropertiesCustomizer` | `/* booking-service */` on every statement |
| 📅 Partner date format | `JsonMapperBuilderCustomizer` | `22-Nov-2026` instead of ISO-8601 |

</div>

<div class="mt-4 text-xl">
Same autoconfiguration. Same owner. <b>Different behavior.</b> If the report changes, it is not Level 2.
</div>

<!--
[13:00 | 1 min]
Everyday Level 2 customizations. Each one is a hook the autoconfiguration publishes.
Looks like Level 2 but isn't:
- SecurityFilterChain: declare one and the default chain BACKS OFF. The report changes, so it is Level 4 (Demo 4). Expected usage, but you own the chain.
- DataSource: no DataSourceCustomizer exists. Tune the pool with spring.datasource.hikari.* (Level 1) or swap the pool (Level 3). JdbcConnectionDetails supplies URL and credentials only.
-->

---

# Level 3 — Dependency replacement

Swap the engine, keep the airframe. When the **implementation itself** is the constraint, change the jars: **no bean changes in your code**, and the conditions engine activates a different path.

<div class="grid grid-cols-2 gap-6 mt-4">
<div style="--slidev-code-font-size: 0.95rem; --slidev-code-line-height: 1.6">

```diff
- spring-boot-starter-tomcat
+ spring-boot-starter-jetty
- HikariCP
+ commons-dbcp2
```

</div>
<div class="flex flex-col gap-3 justify-center">
  <div class="pf-card" style="--c: #4aa3ff">
    <div class="pf-icon">🏗️</div>
    <div><b>Web server</b><small>Tomcat → Jetty</small></div>
  </div>
  <div class="pf-card" style="--c: #f5b83d">
    <div class="pf-icon">🏊</div>
    <div><b>Connection pool</b><small>HikariCP → Commons DBCP2</small></div>
  </div>
  <div class="pf-card" style="--c: #6db33f">
    <div class="pf-icon">📝</div>
    <div><b>Logging backend</b><small>Logback → Log4j2</small></div>
  </div>
</div>
</div>

<div class="mt-4 opacity-80">Autopilot still flies — you swapped the engine.</div>

<!--
[17:00 | 1 min]
Level 3 = the implementation is the constraint, not its config or behavior. Change the classpath and the conditions engine does the rest.
Walk the highlights:
1. Tomcat -> Jetty: exclude spring-boot-starter-tomcat from starter-web, add spring-boot-starter-jetty.
2. HikariCP -> DBCP2: exclude HikariCP from starter-data-jpa, add commons-dbcp2 (version managed by the Boot BOM). No Java touched.
Gotcha: spring.datasource.hikari.* would be silently ignored after the swap, so the YAML moves to dbcp2.max-total.
Undertow was dropped in Boot 4 (verify) - use Jetty.
Cue: "swap the engine". Level 1 and 2 left the report unchanged; Level 3 is where it FLIPS.
-->

---

# Dependency replacement in practice

<div class="instr-table">

| Use case | Swap | Effect |
|---|---|---|
| 🏗️ Company standard server | Tomcat → Jetty | Jetty positive, Tomcat entries vanish |
| 🏊 A different pool | HikariCP → Commons DBCP2 | Dbcp2 positive, Hikari negative |
| 📝 A different logging backend | Logback → Log4j2 | Same SLF4J calls, new backend |
| ⚠️ Provider-specific code | Hibernate → another ORM | Not a jar swap: you import its classes |

</div>

<div class="mt-4 text-xl">
Same code. Different jars. <b>The report flips.</b> Safe when you code to the abstraction.
</div>

<!--
[18:00 | 1 min]
Safe when code depends on an ABSTRACTION (DataSource, SLF4J, Servlet API). Costly when you import provider-specific classes.
Do NOT promise "swap Hibernate for another ORM" - that is not a jar swap; the last row is the warning.
Conditions report: Jetty positive; Tomcat entries VANISH (module not on the classpath); Dbcp2 positive, Hikari negative.
Maven downloads are the #1 live failure - if you demo this, have a recorded fallback.
-->

---

# Level 4 — Ownership transfer

Disengage the autopilot, fly manually. **You declare the bean**, the default backs off, and you own everything it was doing, including what you didn't know about.

<div class="grid grid-cols-2 gap-6 mt-4">
<div style="--slidev-code-font-size: 0.95rem; --slidev-code-line-height: 1.6">

```java
@Bean SecurityFilterChain
@Bean UserDetailsService
@Bean JsonMapper
```

</div>
<div class="flex flex-col gap-3 justify-center">
  <div class="pf-card" style="--c: #ef5b5b">
    <div class="pf-icon">🛂</div>
    <div><b>Security</b><small>the default chain backs off, every rule is now yours</small></div>
  </div>
  <div class="pf-card" style="--c: #f5b83d">
    <div class="pf-icon">👤</div>
    <div><b>Users</b><small>the generated password is gone, you supply the accounts</small></div>
  </div>
  <div class="pf-card" style="--c: #2dd4bf">
    <div class="pf-icon">🧾</div>
    <div><b>JSON</b><small>a hand-built mapper silently drops the Level 2 date format</small></div>
  </div>
</div>
</div>

<div class="mt-4 opacity-80">Autopilot disengaged — you are flying manually.</div>

<!--
[21:00 | 1 min]
Level 4 = transfer of responsibility, not "bean replacement".
The guard is the handover agreement: @ConditionalOnMissingBean in its many forms (Spring Security uses the composed @ConditionalOnDefaultWebSecurity; JsonMapper uses the plain one). Same idea.
Declare the bean -> the default backs off, and the conditions report records it (negative match naming your bean).
Walk the highlights:
1. SecurityFilterChain: ManagementWebSecurityAutoConfiguration backs off. Your chain starts EMPTY.
2. UserDetailsService: UserDetailsServiceAutoConfiguration backs off, no more generated password.
3. JsonMapper: JacksonAutoConfiguration#jacksonJsonMapper goes negative; the date customizer and spring.jackson.* silently stop applying. Nothing fails at startup.
Cue: "disengage the autopilot".
-->

---

# Ownership in practice

<div class="instr-table">

| Use case | Bean you declare | What you now own |
|---|---|---|
| 🛂 Your own security rules | `SecurityFilterChain` | Every request rule, CSRF, sessions |
| 👤 Your own users | `UserDetailsService` | Accounts and password storage |
| 🧾 A hand-built mapper | `JsonMapper` | Date format and `spring.jackson.*`, silently dropped |
| 🔄 Any default bean | `@ConditionalOnMissingBean` guard | Every future Boot change to that bean |

</div>

<div class="mt-4 text-xl">
Declare the bean, the default backs off. <b>Whatever you leave out is not there.</b>
</div>

<!--
[22:00 | 1.5 min]
The Demo 4 story, if you tell it: declare a chain and forget the authorization rule -> curl with NO credentials returns 200, and /actuator/beans too. A missing rule is an open door.
Fix: say what the default used to do silently - anyRequest().authenticated(), role rules, stateless sessions, CSRF off for a header-authenticated API.
The conditions report is the same before and after the fix: it proves who owns the bean, not whether the bean is correct. Correctness is now your code and your tests.
-->

---

# How the defaults back off

<div class="flow">

<div class="fl-head">Spring Boot default</div>
<div class="fl-head">Condition checked at startup</div>
<div class="fl-head">Our app registers</div>

<div class="fl-default fl-off">
  <b>ManagementWebSecurityAutoConfiguration</b>
  <small>default chain <code>managementSecurityFilterChain</code></small>
  <div>
    <div class="fl-status ok">✓ registered</div>
    <div class="fl-status no">✕ backs off</div>
  </div>
</div>
<div class="fl-cond">
  <code>@ConditionalOnDefaultWebSecurity</code>
  <div>
    <small>no <code>SecurityFilterChain</code> bean found → register the default</small>
    <small>found <code>securityFilterChain</code> → do not register</small>
  </div>
</div>
<div class="fl-ours">
  <b>securityFilterChain</b>
  <small>our <code>BookingSecurityConfig</code></small>
</div>

<div class="fl-default fl-off">
  <b>UserDetailsServiceAutoConfiguration</b>
  <small>in-memory user, generated password</small>
  <div>
    <div class="fl-status ok">✓ registered</div>
    <div class="fl-status no">✕ backs off</div>
  </div>
</div>
<div class="fl-cond">
  <code>@ConditionalOnMissingBean</code>
  <div>
    <small>no <code>UserDetailsService</code> / <code>AuthenticationProvider</code> bean → register the default</small>
    <small>found <code>userDetailsService</code> → do not register</small>
  </div>
</div>
<div class="fl-ours">
  <b>userDetailsService</b>
  <small>our in-memory <code>passenger</code> and <code>admin</code> users</small>
</div>

</div>

<pre class="fl-report">Negative matches:
  ManagementWebSecurityAutoConfiguration:
    Did not match: found beans of type 'SecurityFilterChain' securityFilterChain</pre>

<!--
[22:00 | 1.5 min]
Start with what we register: a SecurityFilterChain and a UserDetailsService. Our beans enter the ApplicationContext.

Now the conditions engine evaluates the defaults against the bean registry. It finds our beans, so both defaults back off. Nothing is deleted - the default is simply never registered.

This is the evidence: a negative match that names the exact bean that caused it. One line per handover.
Before this slide the defaults are green: the guard found no bean, so Boot registered them.
-->

---

# The cost of ownership

<div class="instr-table">

| Cost | What it means |
|---|---|
| 🔄 Upgrades | Spring Boot updates the autoconfig, **not your bean** |
| 🧪 Testing | You inherit the framework's integration-testing burden |
| 🔓 Security | The default required auth on every request; **a missing rule is an open door** |
| 🧠 Knowledge | You must know what the default was doing before you replaced it |

</div>

<div class="mt-4 text-xl">
Your chain starts empty. Know the cost <b>before</b> the decision, not after.
</div>

<!--
[23:30 | 1 min]
Not a reason to avoid ownership - an accurate price tag.
What the default SecurityFilterChain was doing for you: every request authenticated (the actuator too), HTTP Basic and form login, CSRF protection and session handling, and a user to log in with.
Ask the room: "what happens if I declare a chain and forget the authorization rule?" Answer: open door.
Aside: JsonMapper is the same story - the date customizer silently stops applying.
-->

---

# Choose the right level

<div class="decide">

<div class="d-row">
  <div class="d-q">Is there a <b>property</b> for it?</div>
  <div class="d-arrow ok">yes →</div>
  <div class="d-lvl l1"><span>1</span> Tune it</div>
  <div class="d-no">no ↓</div>
</div>

<div class="d-row">
  <div class="d-q">Is there an <b>extension point</b>?</div>
  <div class="d-arrow ok">yes →</div>
  <div class="d-lvl l2"><span>2</span> Customize it</div>
  <div class="d-no">no ↓</div>
</div>

<div class="d-row">
  <div class="d-q">Is only the <b>library</b> wrong?</div>
  <div class="d-arrow ok">yes →</div>
  <div class="d-lvl l3"><span>3</span> Swap the dependency</div>
  <div class="d-no">no ↓</div>
</div>

<div class="d-row">
  <div class="d-q">Nothing above fits</div>
  <div class="d-arrow ok">then →</div>
  <div class="d-lvl l4"><span>4</span> Own the bean</div>
</div>

</div>

<div class="mt-4 text-xl">
Stop at the first <b>yes</b>: the <b>lowest level that works</b>.
</div>

<!--
[28:00 | 1 min]
Walk the ladder top to bottom. Stop at the first "yes" — that is the whole rule.

First question: is there a property? If yes, use it. Cheapest, safest, and Spring Boot keeps ownership.

No property? Ask whether the autoconfiguration publishes an extension point — a Customizer or Configurer. You change behavior, Boot keeps the bean.

No extension point? Check whether only the library is the problem. Then it is a classpath change: exclude one jar, add another, and the conditions engine does the rest.

Only when none of those fit do you own the bean. Say the cost again: every future Boot change to that bean is now yours.
-->

---

# Takeaways

1. **Magic is good, but know the tricks**: read the instruments and the conditions report before touching anything
2. Stop at the **lowest level** that works: properties, extension points, dependency swap, ownership
3. Ownership is a **decision**, not a reflex: you own everything the default was doing

<!--
[29:00 | 1 min]
Close both metaphors: a good magician knows the trick, and a good pilot knows when NOT to disengage the autopilot.
Then Q&A / repo URL.
-->

---
layout: center
class: text-center
---

# ▶ DEMO — Climb the ladder

`git checkout demo-0.0-baseline`  →  1.0  →  2.0  →  3.0  →  4.0  →  4.1  →  4.2

<div class="mt-6 opacity-70">
Properties · Customizers · Dependency swap · Own the chain · the conditions report at every step
</div>

<!--
[26:00 | 5 min]  Repo: autopilot-airline. Tags, in order:
demo-0.0-baseline -> demo-1.0-property-tuning -> demo-2.0-extension-points -> demo-3.0-dependency-swap -> demo-4.0-security-defaults -> demo-4.1-own-the-chain -> demo-4.2-own-the-chain-fixed
Same gesture every time: before / change / after, with scripts/conditions.sh and scripts/snapshot.sh.

0. BASELINE  ./mvnw spring-boot:run ; curl -s localhost:8080/v1/bookings/3 | python3 -m json.tool  (nulls on extras, ISO dates)
   scripts/conditions.sh 'JacksonAutoConfiguration#|DataSourceConfiguration\.|TomcatServlet' ; scripts/snapshot.sh before
1. LEVEL 1   application.yaml only: JSON nulls disappear (spring.jackson.default-property-inclusion). snapshot diff: IDENTICAL.
2. LEVEL 2   curl -si -H "Origin: https://book.example-air.com" localhost:8080/v1/bookings/1 | grep -iE 'access-control|x-correlation'
   departureDate -> 22-Nov-2026 (DepartureDateFormatCustomizer). snapshot diff: IDENTICAL, jacksonJsonMapper still positive.
3. LEVEL 3   git show demo-3.0-dependency-swap -- pom.xml : two exclusions + jetty + commons-dbcp2, no Java touched.
   scripts/conditions.sh 'Tomcat|Jetty|DataSourceConfiguration\.' : Jetty positive, Tomcat entries vanish, Dbcp2 positive, Hikari negative.
   FALLBACK if Maven is slow: recorded clip.
4. LEVEL 4   4.0 defaults: generated password in the log, 401 without credentials.
   4.1 own it: BookingSecurityConfig -> generated password gone; report shows both defaults NEGATIVE.
       THE MISTAKE: curl -i localhost:8080/v1/bookings -> 200 with NO credentials; /actuator/beans -> 200 too.
   4.2 fix: no creds 401 | passenger:passenger123 -> 403 on the list, 200 on /v1/bookings/1 | admin:admin123 -> 200.
       The conditions report is the SAME as 4.1: it proves ownership, not correctness.
CUT-IF-LATE: skip Level 1 and Level 3, keep Level 2 and the Level 4 mistake.
-->

---
layout: center
class: text-center
---

# Questions?
