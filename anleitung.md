# Leitfaden zur Erstellung einer sicheren REST-API

Bis jetzt ist unsere REST-API in der Lage alle möglichen Ressourcen zu speichern, anzeigen, löschen und upzudaten.
Jedoch kann jeder diese Endpunkte aufrufen um die Daten zu manipulieren. In manchen Bereichen kann das Sinnvoll sein dass
Daten z.B. öffentlich zugänglich sind. Jedoch fällt mir kein Szenario ein in dem man jedem die Möglichkeit geben sollte
Daten zu verändern oder zu löschen. 

Die API muss also geschützt werden. Dazu gibt es für Spring das ``spring-security`` Paket. Dieses beinhaltet mehrere
Verteidigungslinien um sowohl Authentifikation also auch Autorisierung zu gewährleisten. Auch unterscheidet sich der 
Schutz einer API etwas von Schutzmaßnahmen bei einer normalen Webseite. 

Diese sind kurz (und unvollständig) Zusammengefasst:
 - Der Zugriff auf eine URL wird wenn nicht näher spezifiziert für alle verweigert
 - Zum Login kann man einen Zertifikatsbasierten JWT (JSON Webtoken) bekommen, dieser kann auch für andere Services genutzt werden
 - Schickt man bei der Anfrage einen JWT oder eine Basic Autehtification (username+passwort) mit, kann diese im controller einem User zugeordnet werden
 - Man kann sowohl die Benutzer als auch deren Berechtigungen in einer Datenbank speichern und diese einer Autheifizierung zuordnen

Zunächst werde ich vorstellen wie man eine einfache absicherung macht. Also man ist entweder eingeloggt und kann alles sehen,
oder man ist ausgeloggt und kann (fast) nichts sehen. 

Das interessantere Konzept ist aber das der ACLs (Access Control Lists). Dieses ermöglicht es dass ein Benutzer einem
anderen Freigaben zu seinen eigenen Dokumenten erteilt. Das in verschiedenen Berechtigungsstufen von lesen bis vollständig.

## Security Configuration

So wie auch in Firewalls gibt es in spring security das Konzept der Filter Chain. Dabei werden mehrere Regeln aneinander
gekettet. Hier ein einfaches Beispiel einer ``SecurityFilterChain``:

```java

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/register/**").anonymous() // (1)
                        .anyRequest().authenticated() // (2)
                )
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt) // (3)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // (4)
                .httpBasic(Customizer.withDefaults()) // (5)
                .csrf().disable() // (6)
                .build();
    }

}
```

1. Es wird explizit ein anonymer Zugriff auf den Endpunkt `/user/register` und alle darunterliegenden Pfade (`**`) erlaubt
2. Alle weiteren Zugriffe sind nur erlaubt, wenn man authentifiziert ist
   - Man könnte im `authorizeHttpRequests`-Bereich noch genauere Regeln einstellen wie z.B. dass nur ein bestimmter 
     User, oder User die einer Rolle angehören Zugriff haben. 
3. Diese Konfiguration erlaubt die Authenifizierung mittels JWTs, mehr dazu später <!-- todo jwts erklären -->
4. `SessionCreationPolicy.STATELESS` bedeutet dass keine Session erzeugt wird. Sessions eigenen sich mehr wenn man 
   eine Anwendung schreibt welche im Browser arbeitet. Bei einer API, wenn da eine SessionID zurückkommen würde müsste 
   man die extra speichern. Man muss sich also bei jeder Anfrage neu authentifizieren.
5. MIt `httpBasic` erlaubt man eine Basic Authetication. Im Normalfall werden hierbei Benutzername und Passwort im 
   HTTP-Header mitgeschickt. Die API kann daraufhin einen zeitlich begrenzten JWT zurückschicken zur weiteren 
   verwendung. Dabei sollte man darauf achten dass die Daten im Internet bei der Übertragung mit HTTPS verschlüsselt 
   sind, sonst können Dritte sie abfangen. 
6. Nachdem diese API nicht für die Verwendung im Browser vorgesehen ist kann man CSRF abschalten um die Interaktion 
   für http libraries zu vereinfachen. 

> Filter Chains erklärt: https://de.wikipedia.org/wiki/Filter_(Software)

> JWTs erklärt: https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html#oauth2resourceserver-jwt-architecture

> Sessions erklärt: https://www.baeldung.com/spring-security-session

> Basic Authentifaction im RFC: https://www.rfc-editor.org/rfc/rfc1945#section-11.1


## JWT - JSON Web Tokens
> JWT RFC: https://www.rfc-editor.org/rfc/rfc7519

> Erklärung der offiziellen Webseite: https://jwt.io/introduction

Ein JSON Web Token besteht aus drei Teilen
- Header
- Payload
- Signature

Diese Bereiche sind mit base64 kodiert und durch einen Punkt getrennt. Deren Aufbau sieht also ungefähr so aus:
`xxx.yyy.zzz`

Gegenüber normalem JSON-Strings bieten sie den Vorteil dass die Nutzdaten (Payload) signiert bzw. verschlüsselt 
übertragen werden können. Der Server kann damit z.B. einen Session-Key oder auch einfach nur den Usernamen signiert - 
mit Ablaufdatum - an den Client zurückschicken. In einer verteilten Microservice-Architektur kann dann jeder Service 
welcher Zugriff auf das zugrundeliegende Zertifikat hat den JWT verifizieren. Auch ohne (ständigen) Internetzugriff. 

> Blogartikel zur Einrichtung von JWTs in einer Spring-Anwendung: https://www.danvega.dev/blog/2022/09/06/spring-security-jwt/

## Speichern von Nutzerdaten
Spring übernimmt **nicht** automatisch die speicherung der Nutzerdaten. Könnte ja sein dass die Daten in einer 
Datenbank, einem LDAP-Server, via Kerberos usw. gespeichert sind. Spring stellt aber das Interface 
`UserDetailsManager` bereit. Dieses wird von anderen Stellen der Spring-Architektur vorausgesetzt. Hat man noch 
keine Möglichkeit zur Nutzerspeicherung kann man die fertige Klasse `InMemoryUserDetailsManager` verwenden. Dort 
werden die Daten während der Laufzeit in einer Map gespeichert. Um die Daten aus einer Datenbank zu bekommen kann 
man einfach ein Repository nehmen:

```java
@AllArgsConstructor
@Component
public class RepoBasedUserDetailsManager implements UserDetailsManager {

   UserRepository userRepository;
    
   ...
}
```

Das Repository speichert dann eine Entity welche das Interface ``UserDetails`` implementiert.

```java
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements UserDetails {
    
    String username, password; 
    
   ...
    
}
```

`UserDetails` wird ebenfalls von Spring verwendet um die wichtigsten Daten eines Benutzers zu speichern. 

## Management von Berechtigungen
Mit der bisherigen Konfiguration ist es möglich dass sich ein Benutzer einloggt um vollen Zugriff auf die auf dem 
Server befindlichen Daten zu bekommen. Dies wird allerdings den Wünschen vieler Kunden schnell zu wenig sein. 

Benutzer sollen z.B. nicht alle das gleiche dürfen. Oft soll ein Administrator alles dürfen, Abteilungsleiter haben 
umfassenderen Zugriff (dürfen Daten löschen, Berechtigungen vergeben etc.) und ein normaler Mitarbeiter soll nur 
Zugriff auf die für ihn relevanten Ressourcen haben (schreibend, ggf. lesend auf fremde Ressourcen). 

Um solch komplexe Berechtigungen zu speichern, macht es Sinn die Berechtigungen seperat zu den eigentlichen 
Ressourcen zu speichern und beim Zugriff auf eine Ressource zu prüfen ob eine Berechtigung vorliegt. 

### Separate Berechtigungsspeicherung
Um die Berechtigungen granular, seperat und flexibel erweiterbar zu speichern kann man sich am Kopzept der ACL 
(Access Controll List) bedienen. Diese wird z.B. auch unter Windows für Dateiberechtigungen eingesetzt. 

> https://de.wikipedia.org/wiki/Access_Control_List

Wie in dem zugehörigen Wikipedia Artikel beschrieben wird für jedes Objekt gespeichert wer (User bzw. Rolle) welche 
Berechtigung hat. Um dieses Konzept in unserer API umzusetzen brauchen wir eine Entity die festhält wer was mit 
welchem Objekt machen darf. Diese könnte so aussehen:

```java
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionEntity {

    public static final Long ALL_OBJECTS = -1L;
    public static final Long NEW_OBJECT = -2L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Long id; // (1)

    @JoinColumn(nullable = false)
    @OneToOne(cascade = CascadeType.MERGE)
    UserEntity principal; // (2)

    @Column(nullable = false)
    Long objectId; // (3)

    String targetTypeName; // (4)

    @Builder.Default
    PermissionType permName = PermissionType.none; // (5)
}
```

1. Der Primärschlüssel
2. Der Benutzer (oder Rolle) dem die Berechtigung erteilt wird
3. Der Primärschlüssel des Objekts auf das sich die Berechtigung bezieht (wenn Positiv). Spezielle Berechtigungen 
   sind in den obigen Konstanten `ALL_OBJECTS` bzw. `NEW_OBJECT` festgehalten. 
4. Der Typ des Objekts auf das sich die Berechtigung bezieht. Zusammen mit dem Primärschlüssel kann damit jedes 
   Objekt eindeutig identifiziert werden. Um Missverständnissen vorzubeugen, empfiehlt es sich hier den vollen 
   Pfad zu nehmen. Also statt z.B. `Patient` nimmt man `at.spengergasse.mis.api.entities.Patient`. 
5. Die Berechtigung die gegeben wird. Wird im nächsten Punkt genauer beschrieben. 

### Rollen & Berechtigungshierarchie

Um darzustellen dass eine Berechtigung die andere beinhaltet habe ich mich für folgendes Enum entschieden. Die 
Mächtigkeit (`powerLevel`) kann man für jede Berechtigung seperat einstellen. Auch wäre es dadurch möglich die 
Berechtigungen noch genauer aufzuschlüsseln. Wenn in Zukunft die Anforderung kommt dass für das Löschen von 
Datensätzen eine eigene Berechtigung erforderlich ist kann man diese durch `delete(2L, "delete")` nachrüsten. Dann 
wäre es auch weiterhin möglich das jemand mit der höchsten Berechtigungsstufe ``full`` Daten löscht. 

```java
@Getter
@AllArgsConstructor
public enum PermissionType {
   full(4L, "full"),
   grant(2L, "grant"),
   write(2L, "write"),
   read(1L, "read"),
   none(0L, "none");

   final Long powerLevel;
   final String permissionName;
   
   ...
}
```

Ich möchte aber auch erwähnen dass Spring eine eigene Möglichkeit hat Hierarchien zu beschreiben:

```java
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "admin > write \nwrite > read \nread > nothing";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }
```

### Zugriffsprüfung
Wenn man nun ein Repository hat in dem alle möglichen Berechtigungen gespeichert sind kann man sich einen Service 
schreiben um die Berechtigung zu prüfen:

```java
@Service
@AllArgsConstructor
public class PermissionService {
    // check id based permission
    public boolean checkIdBasedPermission(UserEntity username, Long objectId, String objName, PermissionType permName) {
       // check if user is owner
       if (checkOwnership(username, objectId, objName)) {
          return true;
       }

       // check for a specific id
       var permissionEntity = permissionRepository
               .findByPrincipalAndObjectIdAndTargetTypeName(username, objectId, objName);
       ...
    }
    
   ...
}
```

Mit der Spring-EL in der `@PreAuthorize` oder @PostAuthorize`-Annotation kann man diesen Service dann in einem 
Endpunkt in einem Controller aufrufen:

```java
    @PreAuthorize("@permissionService.checkIdBasedPermission(@userRepository.findByUsername(authentication.name).get(),#id,'Assignment', 'write')")
    @PatchMapping("/{id}")
    Assignment updateAssignment(@RequestBody Optional<Assignment> assignment, @PathVariable Long id, Authentication authentication, HttpServletResponse response){
        if (assignment.isPresent()){
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
            return assignmentRepository.save(assignment.get());
        }else {
            throw new ResourceNotFoundException();
        }
    }
```

Die Symbole, welche mit ``@`` beginnen müssen im Scope der Klasse oder Methode auffindbar sein. 