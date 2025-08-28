# Product Module

This module contains all product-related functionality including categories and products.

## Category Module

The category module provides complete CRUD operations for product categories with hierarchical support and image handling.

### Features

- **Create Category**: Add new categories with title, description, profile image, cover image, and optional parent
- **Read Categories**: Get single category, paginated list, root categories, or children categories
- **Update Category**: Modify existing category information, images, and parent relationships
- **Delete Category**: Remove categories and associated images (admin only, no children allowed)
- **Search Categories**: Search by title or description
- **Hierarchical Structure**: Support for parent-child category relationships
- **Image Support**: Profile picture and cover image uploads via multipart form data

### API Endpoints

#### Create Category
```
POST /categories
Authorization: Bearer <token> (STAFF, ADMIN, SUPER_ADMIN)
Content-Type: multipart/form-data

Form Data:
- title: "Electronics" (required)
- description: "Electronic devices and gadgets" (optional)
- profileImage: [file] (optional)
- coverImage: [file] (optional)
- parentId: "uuid" (optional - for subcategories)
```

#### Get Category by ID
```
GET /categories/{id}
Authorization: None (public)
```

#### Get All Categories
```
GET /categories?page=0&size=10
Authorization: None (public)
```

#### Get Root Categories (No Parent)
```
GET /categories/root
Authorization: None (public)
```

#### Get Children Categories
```
GET /categories/{parentId}/children
Authorization: None (public)
```

#### Search Categories
```
GET /categories/search?q=electronics&page=0&size=10
Authorization: None (public)
```

#### Update Category
```
PUT /categories/{id}
Authorization: Bearer <token> (STAFF, ADMIN, SUPER_ADMIN)
Content-Type: multipart/form-data

Form Data:
- title: "Updated Electronics" (optional)
- description: "Updated description" (optional)
- profileImage: [file] (optional)
- coverImage: [file] (optional)
- parentId: "uuid" (optional - can change parent)
```

#### Delete Category
```
DELETE /categories/{id}
Authorization: Bearer <token> (ADMIN, SUPER_ADMIN)
```

### Entity Structure

```java
Category {
    id: UUID (auto-generated)
    title: String (required, unique at same level)
    description: String (optional)
    profilePicture: String (optional, URL)
    coverImage: String (optional, URL)
    parent: Category (optional - for hierarchical structure)
    children: List<Category> (auto-populated)
    createdAt: LocalDateTime (auto-generated)
    updatedAt: LocalDateTime (auto-updated)
}
```

### Hierarchical Structure

Categories now support a tree-like structure:

```
Electronics (root)
├── Computers
│   ├── Laptops
│   └── Desktops
├── Phones
│   ├── Smartphones
│   └── Feature Phones
└── Accessories
    ├── Cables
    └── Cases

Clothing (root)
├── Men
├── Women
└── Kids
```

### Security

- **Public Access**: Read operations (get, search, list, root, children)
- **Staff+ Access**: Create and update operations
- **Admin+ Access**: Delete operations

### Image Handling

The category module supports image uploads through multipart form data:

- **Profile Image**: Small image for category representation
- **Cover Image**: Larger image for category banners
- **Automatic Cleanup**: Old images are deleted when updated
- **Error Handling**: Image upload failures don't block category operations
- **Cloud Storage**: Images are stored in the "categories" folder

### Business Rules

- **Title Uniqueness**: Category titles must be unique at the same level (siblings)
- **Parent Validation**: Parent category must exist and not create circular references
- **Deletion Protection**: Categories with children cannot be deleted
- **Circular Prevention**: A category cannot be its own parent or descendant

### Frontend Integration

```javascript
// Example frontend form submission for category with parent
const formData = new FormData();
formData.append('title', 'Laptops');
formData.append('description', 'Portable computers');
formData.append('parentId', 'electronics-category-uuid');
formData.append('profileImage', profileFile);
formData.append('coverImage', coverFile);

fetch('/categories', {
    method: 'POST',
    headers: { 'Authorization': 'Bearer ' + token },
    body: formData
});

// Get root categories for main navigation
fetch('/categories/root')
    .then(response => response.json())
    .then(data => {
        // Display main category navigation
        displayMainCategories(data.data.categories);
    });

// Get subcategories when user clicks on main category
fetch(`/categories/${categoryId}/children`)
    .then(response => response.json())
    .then(data => {
        // Display subcategory navigation
        displaySubCategories(data.data.categories);
    });
```

### Usage Example

```java
@Service
public class ProductService {
    
    private final CategoryService categoryService;
    
    public Product createProduct(CreateProductRequest request) {
        // Verify category exists
        CategoryResponse category = categoryService.getCategoryById(request.getCategoryId());
        
        // Create product with category
        Product product = new Product();
        product.setName(request.getName());
        product.setCategoryId(category.id());
        // ... other fields
        
        return productRepository.save(product);
    }
}
```

## Next Steps

- Product entity and CRUD operations
- Product-Category relationships
- Product image handling
- Product search and filtering
