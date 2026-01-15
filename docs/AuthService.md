@Service
@Service Spring ko batata hai:
“Ye class business logic ke liye bani hai, iska object tum manage karo.”
hum jaante hai ki we write our buisness logic,rules,decisions in service layer.
---
private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;
private final JwtService jwtService;
we are going to use these 3 objects (which are injected via spring)
UserRepository allows us to perform read/write actions on user entity (user table) in database.(we dont need to write sql)
PasswordEncoder help us in passwords ko secure tareeke se hash aur verify karne me.
JwtService JWT token banane aur samajhne ka kaam karta hai.
---
as we know login method request dto leti hai as argument and it returns response dto,
User user = userRepository.findByUsername(request.getUsername())
.orElseThrow(() -> new RuntimeException("Invalid credentials"));

request.getUsername()
request = LoginRequest DTO
Isme client se aaya hua username nikaal rahe hain

userRepository.findByUsername(username)
Ye method DB me search karta hai ,“User table me aisa row dhundo jiska username ye ho”
Ye method User direct return nahi karta, balki:
Optional<User> return krta h,Optional ka matlab: “Ho sakta hai value mile, ho sakta hai na mile.”

.orElseThrow(() -> new RuntimeException("Invalid credentials"))
Agar user mil gaya: Optional ke andar se User nikaal lo and store it as User Entity 'user'
Agar user nahi mila: turant exception throw kar do
---
request.getPassword() Ye plain text password hai
Example:admin123
Ye kabhi DB me store nahi hota, sirf comparison ke liye use hota hai.

user.getPassword() Ye hashed password hai ,DB se aaya hai
Example (BCrypt hash): $2a$10$kjsdfhskdfhksdfhksdf...
Ye hash one-way hota hai, original password wapas nahi milta.

if (!passwordEncoder.matches(
request.getPassword(),
user.getPassword())) {
throw new RuntimeException("Invalid credentials");
}
Raw password ko hash karke,
stored hashed password se compare karo. aur agar match na ho to exception fake do.
---
agar password match hogya to we will generate token,
String token = jwtService.generateToken(
user.getUsername(),
user.getRole()
);
return new LoginResponse(token);
so here we are generating a token (string) using method of jwtService obj
method takes username,role.
than we return the response dto with token.