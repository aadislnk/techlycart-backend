@RestController tells spring 3 things:
This class handles http request
Method inside it maps to URL
Return Value should go directly to HTTP response body (json/text)

@RestController(= @Controller + @ResponseBody) Spring ko batata hai:
“Ye class HTTP requests handle karegi aur data (JSON) return karegi.”
@Controller → “ye class HTTP requests handle karti hai”
@ResponseBody → “jo return karoge, usse directly HTTP response me bhejo”
Matlab:
hum String, DTO, Object return karge
Spring usse JSON / text me convert karega
Agar RestController na ho to hume har method pe ResponseBody likhna pdega.
---
@RequestMapping("/api/auth")
is controller ke saare endpoints ka common URL prefix set karta hai.
base path for a controller
instead of repeating path on every method,wwe define base path
here : every endpoint in this controller starts with /api/auth
/api/ -> root
/api/auth -> auth
/api : seperates backend api from frontend routes,versionary later,reverse prox and gateway rely on it.
---
private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService; 
    }
It Dependency Injection (constructor injection)
AuthController will use authService (authService obj is injected by spring while creating obj of AuthCont)
---
@PostMapping("/login")
Jab "/login" URL par POST type ki HTTP request aaye,
tab is method ko call karo.”
Humne is method ko ek specific URL se map kar diya hai.
Mapping POST type ki hai, matlab client request ke body me data bhej raha hai. request ka purpose data bhejna h
Agar mapping GET type ki hoti(@GetMapping), to request ka purpose data fetch karna hota.
---
public LoginResponse login(@RequestBody LoginRequest request) {
return authService.login(request);
}
This Method takes request DTO as an argument and 
return response DTO.
@RequestBody : Spring take the json from HTTP request and convert it into java obj of type LoginRequest.(spring do it using jackson)

now as we are using authService obj,
hum uski ek method use krte hai ie. .login(request);
this method takes request DTO as argument and  returns response DTO (LoginResponse object)