@PreAuthorize("hasRole('ADMIN')")
@PostMapping
public ProductResponse createProduct(@Valid @RequestBody CreateProductRequest request) {
return productService.createProduct(request);
}
ye method argumetn me request dto(CreateProductRequest) leta hai aur
product create krwa k ,response dto(ProductResponse) return krta hai
---
Request
→ JwtAuthFilter (authentication set)
→ Authorization phase
→ @PreAuthorize check  👈 YAHAN
→ Controller method

@PreAuthorize("hasRole('ADMIN')") method-level security lagata hai
Ye annotation bolta hai:
“Is method ko sirf ADMIN role wala user hi call kar sakta hai.”
aur ensure karta hai ki sirf ADMIN role wala authenticated user hi is method ko access kar sake.
SecurityContext me authentication hai?
Us authentication ke paas authority hai: ROLE_ADMIN
Spring Security me ek hard convention hai:
All roles are stored as authorities with prefix ROLE_ 
matlb String requiredAuthority = "ROLE_" + roleName;
hasRole("ADMIN") internally ROLE_ADMIN se compare karta hai.
Isliye tum ROLE_ prefix khud nahi likhte.

@Valid
SecurityContext me authentication hai? Us authentication ke paas authority hai:
ROLE_ADMIN
CreateProductRequest ek DTO hai
Us DTO ke fields pe annotations hoti hain jaise:
@NotBlank
@NotNull
@Positive
Validation fail hone par kya hota hai?
Spring MethodArgumentNotValidException throw karta hai
GlobalExceptionHandler usse catch karta hai

@RequestBody : we alereaady know from authController

---
Page<ProductResponse> getProducts(
@RequestParam(required = false) String search,
@RequestParam(defaultValue = "0") int page,
@RequestParam(defaultValue = "5") int size) {
yeh method argument me string,no. of page, size leti hai and 
corrusponding data ProductService k through nikal k deti hai
in form of page containing ProductResponse (response dto)
serach (string) optional hota hai,
aur agar page and size bhi ni di to we take default values 0,5 resp.
@RequestParam HTTP request ke URL se aane wale query parameters ko,
method ke parameter me map karta hai.
---
yha se to hum jaante hee hai,response spring dware http response me convert hoke client k paas jarga
---
service me methods hoti hai,to create, search and get product.