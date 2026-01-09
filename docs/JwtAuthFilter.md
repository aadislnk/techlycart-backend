@Component
Spring ko batata hai:
“Is class ka object tum khud bana lo aur manage karo.”(for Generic/helper class)
---
JwtAuthFilter extends OncePerRequestFilter
OncePerRequestFilter Spring Security ka ek base filter hai 
jo guarantee karta hai ki filter ek HTTP request ke liye sirf ek baar chale.
JwtAuthFilter ka kaam:
JWT token check karna ,SecurityContext set karna
Ye kaam sirf ek baar hona chahiye ,same request pe repeat nahi hona chahiye
that's why we extend it
OncePerRequestFilter internally:
Request ko mark karta hai ,Check karta hai: “Is request pe ye filter pehle chal chuka hai ya nahi?”
Agar chal chuka,dobara run nahi karta
Agar nahi: doFilterInternal() call karta hai
hum sirf ye method implement karte h: doFilterInternal(...)
Spring baaki sab sambhaal leta hai.
---
Here also we are injecting jwtService ka obj as we will use it

---
protected void doFilterInternal(
HttpServletRequest request,
HttpServletResponse response,
FilterChain filterChain)
throws ServletException, IOException

Ye method wahi jagah hai jahan tum apna custom filter logic likhte ho.
Har incoming HTTP request ke liye (sirf ek baar), ye method execute hoti hai.

HttpServletRequest request ,Ye kya hai?
Incoming HTTP request ka poora representation
Is object ke through,
headers read kar sakte ho ,URL dekh sakte ho
method (GET/POST) dekh sakte ho ,body access kar sakte ho (advanced)
Jab client (Postman / browser) HTTP request bhejta hai,
to Tomcat (embedded server) us request ko ek Java object me convert karta hai
jiska naam hota hai HttpServletRequest.
Spring kya karta hai? Wo Tomcat se ye object leta hai
Aur tumhare filter / controller method me parameter ke roop me de deta hai

HttpServletResponse response ,Ye kya hai?
Outgoing HTTP response ka handle
Isse tum:
status code set kar sakte ho (401, 403)
headers set kar sakte ho
response body likh sakte ho

Ye object request ko next filter / next step tak bhejne ka rasta hai.
Simple words: filterChain = “aage jaane ka permission”
---
String authHeader = request.getHeader("Authorization");
HTTP request ke headers me se Authorization naam ka header nikaalti hai.
---
if (authHeader == null || !authHeader.startsWith("Bearer ")) {
filterChain.doFilter(request, response);
return;
}
check krta h ki if header is null or its not in correct format ie. (Authorization: Bearer <JWT>)
Agar condition true ho gy “Is request ke paas JWT nahi hai, main authentication try nahi karunga,
request ko aage bhej do.”
Yahan request reject nahi hoti ,Sirf JWT logic skip hota hai
Login, public endpoints, etc. isi wajah se kaam karte hain.
---
SecurityContextHolder.getContext().getAuthentication() == null
pehle se authentication set nahi hai,KYUN ye check zaroori hai?
Taaki same request pe authentication dobara set na ho
---
List<SimpleGrantedAuthority> authorities =
List.of(new SimpleGrantedAuthority(role));
Spring Security roles ko directly nahi samajhta ,Wo samajhta hai Authorities.
Isliye:role string ko SimpleGrantedAuthority me wrap kiya
ROLE_ADMIN → SimpleGrantedAuthority("ROLE_ADMIN")
---
Authentication object banana
UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
username,
null,
authorities
);
Ye object Spring Security ko bolta hai:
“Is request ka user = username
credentials = null (kyunki JWT hai)
authorities = role list”
Password yahan nahi hota,kyunki password login ke time verify ho chuka hota hai.
---
Request details set karna
authentication.setDetails(
new WebAuthenticationDetailsSource()
.buildDetails(request)
);
Isme:
IP address
session info (agar ho)
attach hota hai.
Mostly logging / auditing ke kaam aata hai.
---
SecurityContextHolder.getContext()
.setAuthentication(authentication);
Iska matlab: “Spring Security, yaad rakhna
is request ka user authenticated hai aur ye uski identity hai.”
Iske baad:
@PreAuthorize kaam karega
hasRole() kaam karega
authenticated() true hoga
---
Request
→ Header check
→ no JWT → forward
→ JWT present →
→ extract token
→ extract username + role
→ create Authentication
→ set SecurityContext
→ filterChain.doFilter()
→ Authorization checks
→ Controller
---
