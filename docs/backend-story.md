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
Body(json)--> {
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
JwtAuthFilter:
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
JwtService(Token Factory):
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

Create Product story ka kaam hai:
“check krna ki kya client product create karne ke layak hai?if yes,to creat product in DB”
we will check: User authenticated hai ya nahi? ,User ADMIN hai ya nahi?

let suppose hum, Client (Postman / frontend) ye request bhejte hai: POST /api/products
Headers -->
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJST0xFX0FETUlOIiwiaWF0IjoxNzY3ODA3OTA0LCJleHAiOjE3Njc4OTQzMDR9.mqQbB-n1zy_mA5l-BhfGPg090RFcDZJng1aaOGCtCFk
Content-Type: application/json
body(json)--> {
"name": "Wireless Mouse",
"description": "Gaming mouse",
"price": 999
}

fir whi kahani, HTTP request request internet se hoke tomacat server pe jaati hai,springB app k paas.
request pehle security pipeline se guzarti hai.
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
---
JwtAuthFilter runs:
Header se ye read karta hai-> Authorization: Bearer <JWT Token>
Token extract karta hai ,Token ko JwtService se validate karwata hai
agar Token VALID hai to, JwtService token se username(admin),role(ROLE_ADMIN) nikalta hai
JwtAuthFilter phir is info ko UsernamePasswordAuthenticationToken me wrap karta hai
Spring Security ke SecurityContext me set kar deta hai
Ab Spring ko pata hai: “Is request ka user ADMIN hai”
⚠Filter yahin rukta hai
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
---
Authorization phase (RULES APPLY HOTE HAIN)
Ab request authorization checks ke paas jaati hai.

1 URL-level rule (SecurityConfig)
.anyRequest().authenticated()
Token present hai,Token valid hai
->Authentication PASS

2 Method-level rule (@PreAuthorize)
ProductController ke method pe likha hai: @PreAuthorize("hasRole('ADMIN')")
Spring kya karta hai? SecurityContext se role uthata hai
check karta hai:
ROLE_ADMIN ?
->Authorization PASS
---
Ab finally request Spring MVC layer me jaati hai.
DispatcherServlet se hoke productController pe
---
Now
→ ProductController
→ ProductService
→ ProductRepository
→ Database
→ Response DTO
→ Client
---
ProductController
Hum jaante hai Conroller ka kaam hota hai,
HTTP request accept karna
JSON → Request DTO me convert karwana (CreateProductRequest (DTO))
Request ko process karna(Validation trigger karna ,Service ko call karna)
Service ka HTTP response client ko dena (reponse dto)
-->For code explanation  prefer ProductController.md
---
ProductService
ProductService product-related saare business rules aur decisions handle karta hai.
Matlab:
product create karna
product fetch karna
product update/delete (future)
filtering, pagination ke rules
Controller sirf request leta hai,
ProductService decide karta hai “kya aur kaise hoga.”

Jab controller bolta hai:
“Product create karo”
ProductService:
DTO se data uthata hai
Naya Product entity banata hai
Business rules apply karta hai
(price > 0, name unique — future)
Repository ko save ke liye bolta hai

Jab controller bolta hai:
“Products list do”
ProductService:
pagination parameters samajhta hai
repository ko fetch ke liye bolta hai
raw entities ko response DTO me convert karta hai

Yhs se DTO return hote hai,controller pe jaate hai,fir wha se spring unko HTTP response me convert karke
cliend ko bhejta hai. bss yehi story thi.
-----------------------------------------------------------------------------------------------------------
ENTITY CLASSES
Entity ek Java class hoti hai jo directly database table se map hoti hai.
1 entity class ≈ 1 table
object ↔ row
field ↔ column

Product Entity
Role:
Product ka database representation.
Isme hota hai:
id
name
description
price
Entity API ke liye nahi, DB ke liye hoti hai
Client kabhi entity ko directly touch nahi karta.

User Entity
Role: User ka database representation (authentication ke liye)
Isme hota hai:
id
username
password (hashed)
role
Important:
password hashed hota hai
role = ROLE_ADMIN / ROLE_USER
Ye entity security ka base hai


@Entity
Hibernate/JPA ko batata hai ki ye class database table se map hogi.
Is class ka har object database me ek row represent karta hai.

@Table
Entity ko kis database table se map karna hai, ye define karta hai.
Tab use hota hai jab table ka naam class name se different ho.

@Column
Class ke field ko database ke column se map karta hai.
Isse column name, nullability, uniqueness jaise rules define kar sakte hain.

@Id
Is field ko primary key mark karta hai.
Hibernate isi key se har row/entity ko uniquely identify karta hai.

@GeneratedValue
Primary key ki value kaise generate hogi, ye batata hai.
Mostly database auto-increment ya sequence ke through ID generate hoti hai.

@RestControllerAdvice
Poore application ke liye global exception handling provide karta hai.
Controller code ko clean rakhta hai aur uniform error responses deta hai.
----------------------------------------------------
DTOs (Data Transfer Objects)
DTOs ka kaam: Layers ke beech clean aur safe data transfer
🔹LoginRequest
Role:
Client se aane wala login data
Contains: username ,password
Why DTO?
Client sirf ye hi bhej sakta hai
Extra fields ignore ho jaate hain
🔹 LoginResponse
Role:
Login ke baad client ko diya jaane wala data
Contains:
JWT token,(optionally username / role)
Why?
Password kabhi wapas nahi jaata
Clean API response
🔹 CreateProductRequest
Role:
Product create karte waqt client se aane wala data
Contains:
name
description
price
With:
@NotBlank
@Positive
Validation yahin hoti hai, entity me nahi.
🔹 ProductResponse
Role:
Client ko product ka safe view dena
Contains:
id
name
price
Why?
Internal fields hide rehte hain
Entity change hone pe API safe rehti hai

-----------------------------------------------------------------
REPOSITORIES
(UserRepository, ProductRepository)
🔹 Repository kya hai?
One-line:
Repository database access layer hota hai.
Iska kaam:
SQL likhe bina DB se baat karna
🔹 UserRepository
Used in:
AuthService
JwtAuthFilter (indirectly)
Main kaam:
findByUsername
Returns:Optional<User>
Authentication ka base.
🔹 ProductRepository
Used in: ProductService
Main kaam:
save(product)
findAll(Pageable)
findByNameContainingIgnoreCase(...)
Spring Data JPA:
method name se SQL bana deta hai
tum SQL likhte hi nahi
Service decides WHAT, Repository decides HOW (DB)
----------------------------------------------------------------
GLOBAL EXCEPTION HANDLER
(@ControllerAdvice)
🔹 Ye kya karta hai?
One-line:
Poore application ke exceptions ko centrally handle karta hai.
🔹 Kyun zaroori hai?
Agar ye na ho:
har controller me try-catch
messy code
inconsistent error responses
With it:
clean controllers
uniform error JSON
easy debugging

@RestControllerAdvice
Poore application ke liye global exception handling provide karta hai.
Controller code ko clean rakhta hai aur uniform error responses deta hai.

@ExceptionHandler
Specific exception ko handle karne ke liye method define karta hai.
Jab wo exception throw hoti hai, ye method automatically execute hota hai.
-----------------------------------------------------------------------------------------------------------------------------------------------
APPLICATION START
└─ Spring Boot Main
└─ Component Scan
├─ Controllers
├─ Services
├─ Repositories
├─ Filters
├─ Configs / Beans
└─ SecurityFilterChain READY

CLIENT REQUEST
└─ HTTP Request
└─ Tomcat
└─ HttpServletRequest

SECURITY PIPELINE
└─ Security Filter Chain
└─ JwtAuthFilter
├─ Read Authorization Header
├─ JWT Present?
│   ├─ NO → Forward
│   └─ YES
│       ├─ Validate Token
│       ├─ Extract Username
│       ├─ Extract Role
│       └─ Set Authentication in SecurityContext
└─ Authorization Checks
├─ authenticated()
└─ @PreAuthorize (role check)

SPRING MVC
└─ DispatcherServlet
└─ Controller
├─ @RequestBody → DTO
├─ @Valid → Validation
└─ Call Service

SERVICE LAYER
└─ Business Logic
├─ DTO → Entity
├─ Rules / Decisions
└─ Call Repository

REPOSITORY LAYER
└─ Spring Data JPA
└─ Hibernate
└─ SQL Execution

DATABASE
└─ Tables
└─ Rows

RESPONSE FLOW
└─ Entity
└─ Service
└─ Response DTO
└─ Controller
└─ JSON Response
└─ Client

EXCEPTION FLOW (ANYWHERE)
└─ Exception Thrown
└─ @RestControllerAdvice
└─ @ExceptionHandler
└─ Error JSON
└─ Client
