
@Configuration :
Ye annotation Spring ko batata hai:
“Is class ke andar configuration (rules) likhe hue hain”
App startup pe Spring is class ko read karega n iska obj bnayega.
---
@EnableMethodSecurity:
Spring ko bolta hai:
“Controller / Service methods pe security annotations allow karo”
Iske baad hee role-based checks possible hote hain.
---
private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }
->humne jwtAuthFilter ek instance variable banaya
ye variable JwtAuthFilter ke object ko refrence karega
constructor ke through is variable ko value milti hai
Spring jab SecurityConfig ka object banata hai, tab wo khud JwtAuthFilter ka object inject karega
kyunki Spring already sab objects bana ke rakhta hai.
->SecurityConfig ke paas ek dependency hai
Is dependency ka type JwtAuthFilter hai
Ye object SecurityConfig ke andar use hoga
This what dependency injection is .
---
@Bean :
bolta hai Spring ko:
“Is method ka jo object return hoga, usse tum apne paas rakh lo aur manage karo.”
---
public SecurityFilterChain securityFilterChain(HttpSecurity http)
throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
SecurityFilterChain is an object that represents a list/chain of security filters
that will run for every incoming HTTP request.(It belongs to Spring Security).
Is method ka kaam:
Spring Security ke liye “rules + filters” ko combine karke
ek final SecurityFilterChain (security pipeline) object banana

Spring startup pe
is method ko call karta hai ,is method ke andar:
rules define hote hain ,filters add hote hain
http.build() se final chain banti hai
Spring us chain ko use karta hai for every request
Ye method request ke time nahi chalta ,Ye startup time pe chalta hai.

http object kahan se aaya? HttpSecurity http
Spring ne khud HttpSecurity ka object banaya
aur iss method me inject kar diya

HttpSecurity ek builder/configurator object hai
jisse hum Spring Security ko bolte ha ki kese behave karna hai (by applying our rules and filters).

ab hum is object k sath khelenge(apply our rules and filters):

http.csrf(csrf -> csrf.disable());
CSRF browser + cookies ke liye hota hai
JWT + REST API me useless hota hai ,Isliye disable krdiya

.authorizeHttpRequests(auth -> auth
.requestMatchers("/api/auth/**").permitAll()
.anyRequest().authenticated()
)
matlab : /api/auth/** → public,http request ko aage jaane do without token.
baaki sab → token required
role check nahi hota,sirf authentication hota hai

Custom JWT filter add karna
.addFilterBefore(
jwtAuthFilter,
UsernamePasswordAuthenticationFilter.class
);
“JWT filter ko Spring ke default auth filter se pehle chalao”
“JWTAuthFilter ko
UsernamePasswordAuthenticationFilter se pehle chala do.”


Why this method throws exception?
Kyunki HttpSecurity internally bahut saari cheezein configure karta hai
aur unme checked exceptions ho sakti hain.
jese invalid configuration ,conflicting rules ,filter setup issues

return http.build(): sabhi rules,filter ko pack krke SecurityFilterChain bana k return krdo

---
public PasswordEncoder passwordEncoder() {
return new BCryptPasswordEncoder();
}
Ye method Spring ko ek PasswordEncoder ka object deti hai
PasswordEncoder ek interface hai and BCryptPasswordEncoder is its implemetation.(both from spring security)
PasswordEncoder object ko use krke hum passwords ko encode krna (hash banana) + verify karne ka kaam krenge.
---
UsernamePasswordAuthenticationFilter ka role kya hai?
Ye filter:
/login request handle karta hai
Username + password check karta hai
Authentication object banata hai
JWT se pehle ye chala to problem ho jaati hai, kyunki:
JWT request me username/password hota hi nahi
Sirf token hota hai

jwtAuthFilter kya karta hai?
Normally ye karta hai:
Request se Authorization header uthata hai
Bearer <token> extract karta hai
Token valid hai ya nahi check karta hai
Agar valid hai → SecurityContext me user set karta hai