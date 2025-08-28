# Product Module

This module handles product management with support for variants, categories, and media files.

## Entity Structure

### Product
- **Basic Info**: title, slug, description (HTML), note, brand
- **Media**: mediaDescriptions (List of image URLs from cloud storage)
- **Flexible Data**: attributes (JSON), SEO fields
- **Status**: draft, published, archived, deleted
- **Features**: featured, categories (many-to-many)

### ProductVariant
- **Pricing**: price, compareAtPrice, costPrice
- **Attributes**: JSON for color, size, material, etc.
- **Inventory**: quantity, tracking, low stock threshold
- **Physical**: weight, dimensions (JSON)
- **Status**: active/inactive

## Key Features

### Media Management
- Images are uploaded to Cloudflare R2 storage in the `products/` folder
- `mediaDescriptions` stores a list of image URLs
- Supports multiple images per product
- Automatic file upload during product creation/update
- **Post-creation media management**: Add/remove media after product creation
- **Bulk operations**: Add/delete multiple media files at once
- **Cloud storage cleanup**: Automatic deletion from cloud storage when removing media
- **Frontend integration**: Always uses MultipartFile through form data (same as category system)

### Category Integration
- Direct many-to-many relationship with categories
- No junction table needed
- Products can belong to multiple categories
- Uses `ProductCategoryResponse` (separate from main category system)

### Inventory Strategy
- **Variant-level inventory**: Each variant has its own stock quantity
- Better accuracy for e-commerce operations
- Supports different inventory tracking modes

## API Endpoints

### Product Management
- `POST /products` - Create product (multipart form data)
- `PUT /products/{id}` - Update product (multipart form data)
- `GET /products/{id}` - Get product by ID
- `GET /products/slug/{slug}` - Get product by slug
- `DELETE /products/{id}` - Delete product
- `PATCH /products/{id}/status` - Update product status

### Product Listing
- `GET /products` - Get all products (paginated)
- `GET /products/status/{status}` - Get products by status
- `GET /products/search?query={query}` - Search products
- `GET /products/category/{categoryId}` - Get products by category
- `GET /products/featured` - Get featured products

### Media Management
- `POST /products/{id}/media` - Add media files to product (multipart form data)
- `DELETE /products/{id}/media` - Delete media from product (JSON array of URLs)

## Usage Examples

### Creating a Product
```bash
curl -X POST /products \
  -F "title=Sample Product" \
  -F "slug=sample-product" \
  -F "description=<p>Product description</p>" \
  -F "brand=Nike" \
  -F "status=PUBLISHED" \
  -F "categoryIds=uuid1,uuid2" \
  -F "mediaFiles=@image1.jpg" \
  -F "mediaFiles=@image2.jpg"
```

### Adding Media After Creation
```bash
# Add new media files (same pattern as category)
curl -X POST /products/{id}/media \
  -F "mediaFiles=@new-image1.jpg" \
  -F "mediaFiles=@new-image2.jpg"
```

### Deleting Media
```bash
curl -X DELETE /products/{id}/media \
  -H "Content-Type: application/json" \
  -d '["https://example.com/image1.jpg", "https://example.com/image2.jpg"]'
```

## DTOs

### Request DTOs (Classes)
- `CreateProductRequest` - For product creation
- `UpdateProductRequest` - For product updates

### Response DTOs (Records)
- `ProductResponse` - Single product data
- `ProductListResponse` - Paginated product list
- `ProductCategoryResponse` - Category data for products (separate from main category system)

## Business Rules

1. **Slug Uniqueness**: Product slugs must be unique
2. **Media Upload**: Images are automatically uploaded to cloud storage
3. **Category Assignment**: Products can have multiple categories
4. **Status Management**: Products have draft, published, archived, deleted states
5. **Inventory Tracking**: Variant-level inventory management
6. **Media Management**: 
   - Can add/remove media after product creation
   - Deleting media removes files from cloud storage
   - Supports bulk operations for efficiency
   - Frontend always sends MultipartFile through form data

## Dependencies

- **CloudflareR2Service**: For media file uploads and deletions
- **CategoryRepository**: For category lookups
- **ProductMapper**: For entity-DTO conversion
- **Spring Security**: For role-based access control (ADMIN role required for mutations)

## Media Management Features

### Efficient Operations
- **Bulk Add**: Upload multiple files at once through form data
- **Bulk Delete**: Remove multiple media files simultaneously
- **Cloud Storage Integration**: Automatic cleanup when deleting media
- **Post-Creation Support**: Add media without recreating the entire product

### File Handling
- Files are uploaded to `products/` folder in cloud storage
- Automatic URL generation and storage
- Error handling for failed uploads/deletions
- Logging for debugging and monitoring
- **Frontend Integration**: Consistent with category system - always MultipartFile through form data
