🎯 Project Overview
This is a production-ready Spring Boot backend designed for companies that import products from international suppliers to serve Bangladeshi customers. The project emphasizes clean architecture, robust error handling, and frontend-friendly APIs.
🌟 What Makes This Special

🛡️ Production-Grade Error Handling - Comprehensive error management that frontend developers will love
🔐 Secure Authentication System - JWT with refresh token rotation and multi-device support
📱 Frontend-Friendly APIs - Consistent response structures with detailed error information
🧪 Test-Driven Development - Built with testing and maintainability in mind
🇧🇩 Bangladesh-Focused - Designed for local import business requirements

<div align="center">
CategoryTechnologyVersionPurpose☕ LanguageJava17+Core Development🌱 FrameworkSpring Boot3.2+Application Framework🔒 SecuritySpring Security6.2+Authentication & Authorization💾 DatabasePostgreSQL15+Primary Data Store⚡ CacheRedis7+Caching📦 BuildMaven3.9+Dependency Management🐳 DeployDockerLatestContainerization📖 DocsOpenAPI 3LatestAPI Documentation
</div>

📊 Response Format
Our API follows a consistent, frontend-friendly response structure:
✅ Success Response
json{
  "success": true,
  "message": "User created successfully",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "age": 25
  },
  "timestamp": "2024-12-07T10:30:00Z"
}
❌ Error Response
json{
  "success": false,
  "message": "Validation failed",
  "errorCode": "VAL_001",
  "path": "/api/v1/users",
  "timestamp": "2024-12-07T10:30:00Z",
  "validationErrors": [
    {
      "field": "email",
      "rejectedValue": "invalid-email",
      "message": "Invalid email format"
    }
  ]
}
📄 Paginated Response
json{
  "success": true,
  "data": {
    "content": [...],
    "page": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8,
    "first": true,
    "last": false
  },
  "timestamp": "2024-12-07T10:30:00Z"
}
