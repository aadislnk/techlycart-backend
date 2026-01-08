8/1/26
Jab hum app start karte hai (TechlycartBackendApplication.java)
main() method run hua
@SpringBootApplication ne:
component scan kiya
controllers, services, repositories ko memory me load kiya
embedded Tomcat start kiya (port 8080)
-->Ab app requests receive karne ke liye ready hai

so abhi hmare app me do flow hai : login,product creation(admin), product access (user/admin)

------------------------------------------------------------------------------------------------
**Login Ki Kahani(Authentication flow) :**
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
-----
##For Code Understanding of class **SecurityConfig.java** Refer to SecurityConfig.md
SecurityConfig request handle nahi karta.
Ye sirf rules define karta hai jinke basis pe Spring Security ka system kaam karta hai.
Simple words me:
kaunse URL public rakhne hain
kaunse protected rakhne hain
kaunse filter lagege
session kaise behave karega,etc
-----
##For Code Understanding of class **JwtAuthFilter.java** Refer to JwtAuthFilter.md
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
-----
Ab Request **AuthController** pr aati hai:
##For Code Understanding of class **AuthController.java** Refer to AuthController.md
AuthController sirf ye kaam karta hai:
HTTP request accept karna
JSON → Request DTO me convert karna (LoginRequest (DTO))
Request ko process karna(Validation trigger karna ,AuthService ko call karna)
Service ka HTTP response client ko dena (reponse dto)

request body se data leta hai
LoginRequest DTO banwa deta hai
validation (agar annotations hain) yahin trigger hoti hai
service ko request forword hoti hai
AuthService return karta hai:
ya to LoginResponse DTO (token ke saath)
ya exception (invalid credentials)
Controller:
exception handle nahi karta
success response dto simply return karta hai to client
LoginResponse DTO → HTTP Response (JSON) by spring
-----
AuthService:
login ke rules yahin hote hain
decision yahin hota hai
controller yahan se order leta hai
AuthController sirf request receive karta hai,
AuthService decide karta hai “login allowed hai ya nahi.”
AuthService k kaam:
1️ User ko database se nikalna
2️ Password verify karna
3️ Agar credentials galat → error
4️ Agar sahi → JWT generate karwana
5️ Response data banana
AuthController
↓
AuthService
↓
UserRepository   →  (user data) (Read/Write done on Entity(data/Table) using repo layer (interface))
PasswordEncoder  →  (password check)
JwtService       →  (token generate)
↓
AuthController
-----
JwtService:
JwtService ka kaam JWT token banana, validate karna aur usse data nikaalna hai.
jwtservice k kaam:
1️ JWT TOKEN BANANA (Login ke time)
Jab user successfully login karta hai:username ,role ,expiry time
In sab ko ek token me pack kar diya jaata hai
aur secret key se sign kar diya jaata hai.
Ye token client ko de diya jaata hai.

2️ JWT TOKEN VERIFY KARNA (Har request pe)
Jab client token bhejta hai:
Authorization: Bearer <JWT>
JwtService check karta hai:token tampered to nahi? token expired to nahi? signature valid hai ya nahi?
Agar invalid:request reject

TOKEN SE DATA NIKAALNA
Valid token se:
username,role nikale jaate h
Ye data JwtAuthFilter use karta hai
taaki Spring Security ko bata sake: “Is request ka user kaun hai”
------
now AuthController sends this response DTO (from method login()) to spring and spring sends thsi
as HTTP Response to the client.
###This was all about Login Procedure,
Now we will create a product (admin role),also use that created token :)
------------------------------------------------------------------------------------
**Admin Role & Product Creation Ki Kahani**
