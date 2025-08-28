# Cloudflare R2 Configuration Guide

## Public URL Configuration

The `cloudflare.r2.public-url` property is **REQUIRED** for proper image URL generation. Without it, you'll get relative paths instead of full public URLs.

## Setting the Public URL

### Option 1: Default Cloudflare R2 Domain
```properties
cloudflare.r2.public-url=https://pub-9cda468137d1fd4eb0b930ade6113b21.r2.dev
```

### Option 2: Custom Domain (Recommended)
If you have a custom domain pointing to your R2 bucket:
```properties
cloudflare.r2.public-url=https://cdn.yourdomain.com
```

### Option 3: Your Current Setup
Based on your current configuration, you should use:
```properties
cloudflare.r2.public-url=https://9cda468137d1fd4eb0b930ade6113b21.r2.cloudflarestorage.com
```

## Complete Configuration Example

```properties
# Cloudflare R2 Configuration
cloudflare.r2.endpoint=https://9cda468137d1fd4eb0b930ade6113b21.r2.cloudflarestorage.com
cloudflare.r2.access-key-id=97ce6d7917dd642150f05f8c605978bd
cloudflare.r2.secret-access-key=27cea66ff479ba56b48acb6acea0b3aef382579fd035a4fcaf15a3125c9ac1dd
cloudflare.r2.bucket-name=taqreem
cloudflare.r2.public-url=https://9cda468137d1fd4eb0b930ade6113b21.r2.cloudflarestorage.com
cloudflare.r2.max-file-size=10485760
cloudflare.r2.allowed-image-types=jpg,jpeg,png,gif,webp
cloudflare.r2.allowed-video-types=mp4,webm,mov,avi
```

## What Happens Without Public URL

❌ **Without public URL configured:**
```json
{
  "profilePicture": "/categories/d808a23b-f8f3-47c9-b4cb-f9b22c27eb6b.jpg",
  "coverImage": "/categories/f9ac2158-e8fe-42d6-b24e-c8793ffa52e9.jpg"
}
```

✅ **With public URL configured:**
```json
{
  "profilePicture": "https://9cda468137d1fd4eb0b930ade6113b21.r2.cloudflarestorage.com/categories/d808a23b-f8f3-47c9-b4cb-f9b22c27eb6b.jpg",
  "coverImage": "https://9cda468137d1fd4eb0b930ade6113b21.r2.cloudflarestorage.com/categories/f9ac2158-e8fe-42d6-b24e-c8793ffa52e9.jpg"
}
```

## Testing the Configuration

After setting the public URL:

1. Restart your application
2. Try uploading a category image
3. Check the logs for: "Constructing file URL: ..."
4. Verify the response contains full URLs

## Troubleshooting

- **Error**: "Cloudflare R2 public URL is not configured"
  - **Solution**: Set `cloudflare.r2.public-url` in application.properties
  
- **Still getting relative paths**: 
  - Check that the property is not commented out
  - Ensure no extra spaces or quotes
  - Restart the application after changes
