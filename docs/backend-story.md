8/1/26
Jab hum app start karte hai (TechlycartBackendApplication.java)
main() method run hua
@SpringBootApplication ne:
component scan kiya
controllers, services, repositories ko memory me load kiya
embedded Tomcat start kiya (port 8080)
-->Ab app requests receive karne ke liye ready hai

so abhi hmare app me do flow hai : login,product creation(admin), product access (user/admin)

-----
**##login ki kahani(Authentication flow) :**
Login story ka sirf ek kaam hai:
check karna ki ye user kaun hai(role/username), aur kya hum ispe trust kar sakte hain(password)?
Agar haan → JWT token de do,{kyuki bina filter token k request aage nahi bad skti except login request}
Agar nahi → error bhej do

let suppose hum, Client (Postman / frontend) ye request bhejte hai: POST /api/auth/login
body(json)--> {
                "username": "admin",
                "password": "admin123"
               }
to matlab postmen se humne HTTP request bheji (which contains data in json formate)
request internet se hoke tomacat server pe jaati hai,spring k paas.
request direct controller k paas nhi jayegi,Har request pehle security pipeline se guzarti hai.
-----
Request
↓
Security Filter Chain which also includes JwtAuthFilter (apna custom filter)
↓
Authorization checks (from SecurityConfig + @PreAuthorize)
↓
DispatcherServlet
↓
Controller
-----
Jab app start hoti hai:
Spring SecurityConfig ka use krke
Security Filter Chain banaata hai
Is chain me:
apna JwtAuthFilter bhi hota hai
aur Spring ke default filters bhi hote hain
Ye sab startup time pe hota hai, request time pe nahi

So now Request will go through this security filter chain,
JwtAuthFilter
Authorization header check karta hai 
Header nahi milta (kyuki login me hota hi nahi)
to filter bolta hai:“Thik hai, is request ko aage jaane do,
iske paas Token nahi hai, main kuch nahi karunga”
Isliye login request block nahi hoti.
fir Spring ne SecurityConfig ke rules dekhe:
.requestMatchers("/api/auth/**").permitAll()
Spring bola: “Is endpoint ke liye authentication required nahi", request forword hoti hai.
finally controller call hota hai and
Request controller tak pahunch jaati hai.
->Login request block nahi hoti kyunki
JwtAuthFilter token ke absence me request ko forward kar deta hai
aur permitAll() rule authentication enforce nahi karta<-

Login request → Security Filter Chain → 
JwtAuthFilter(in case of login,token nahi hai : request aage bhej do) →
Authorization rules (permitAll) → 
Controller


##For Code Understanding of class SecurityConfig.java Refer to SecurityConfig.md
SecurityConfig request handle nahi karta.
Ye sirf rules define karta hai jinke basis pe Spring Security ka system kaam karta hai.
Simple words me:
kaunse URL public rakhne hain
kaunse protected rakhne hain
kaunse filter lagege
session kaise behave karega,etc

##For Code Understanding of class JwtAuthFilter.java Refer to JwtAuthFilter.md
JwtAuthFilter har incoming request me JWT token ko check karta hai
aur Spring Security ko batata hai: is request ka user kaun hai aur iska role kya hai.
ye har request pe chalta hai,
Ye kya karta hai?!:
1. Header check:
Filter dekhta hai:
Authorization: Bearer <token>
Agar header hi nahi hai → request ko aage jaane deta hai
(Login ke case me yahi hota hai)
2. Token mila? Toh validate karo
Agar token hai:
signature valid hai?
expired toh nahi?
tampered toh nahi?
Ye kaam JwtService karta hai.
3. Token se data nikalo : username ,role
4. Spring Security ko inform karo
“Is request ka user = X aur iski authority = ROLE_ADMIN / ROLE_USER”
Is info ko Spring SecurityContext me store kar leta hai
5. Request ko aage bhejo.
