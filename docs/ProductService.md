we will use ProductRespository object,
Entity (Table/data) pe read/write perform krne k liye.
Service later me hee to hum hmare saare buisness logics likhte hai,rules,decision lete h.
hum methods banate hai:

ProductResponse createProduct(CreateProductRequest request)
inside it: hum ek fresh entity object banate hai(new row created),
request dto se data lekar entity me fill krte hai(row data is filled)
productRepository.save(newEntity) ka use krte use db me save krte hai
than we send this created entity as response,lekin!!!
Hum direct entity response me send nhi kr skte,we wanna send response dto
so first we convert that entity to response dto using mapToResponse method and send it.

List<ProductResponse> getAllProducts()
humne repository ka use krke saari rows access kari,than we converted all to response dto 
and than we returned list containing all those ProductResponse

Page<ProductResponse> getProducts(int page, int size)
yha hum kush nya nhi kr rhe h,we are returning page of producResponse
PageRequest pageable = PageRequest.of(page, size); create krte hai
//Internally, Hibernate converts this to SQL like:
// LIMIT size OFFSET (page * size)
//PageRequest is a class provided by Spring Data JPA.
aur given parameter k hisaab se,entities(table rows)(productResponse) ko page me daal ke return krte h.

Page<ProductResponse> searchProducts(String keyword, int page, int size)
nothing new,we search by name containing keyword, ans same process

ProductResponse mapToResponse(Product product) Entity-->Response DTO conversion method
hum dto ka obeject bna k,entity se data leke dto me fill krte hai
and than we return it.

---
ProductResponse createProduct(CreateProductRequest request) vs
Product createProduct(Product product)

ProductResponse createProduct(CreateProductRequest request)
= CLEAN, SAFE, INDUSTRY-LEVEL API design
Ye kya karta hai?
Input: CreateProductRequest (DTO)
Output: ProductResponse (DTO)
Iska matlab:
Client se aane wala data control me hai
Client ko jaane wala data control me hai
-
Product createProduct(Product product)
= TIGHT COUPLING, RISKY, BEGINNER-LEVEL design
Ye kya karta hai?
Input: Entity
Output: Entity
Matlab:
Client directly DB model se baat kar raha hai X
---