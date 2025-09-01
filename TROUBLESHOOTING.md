# User Service Troubleshooting Guide 🔧

## Common Issues and Solutions

---

## 🚫 Service Won't Start

### Issue: Port 8444 Already in Use
```
Error: Port 8444 is already in use
```

**Solutions:**
```bash
# Find process using port 8444
lsof -ti:8444

# Kill the process
kill -9 <PID>

# Or use a different port
echo "server.port=8445" >> src/main/resources/application.properties
```

### Issue: Java Version Incompatible
```
Error: Java version 11 required, but 17 found
```

**Solutions:**
```bash
# Check Java version
java -version

# Update pom.xml Java version
<properties>
    <java.version>17</java.version>
</properties>
```

### Issue: Database Connection Failed
```
Error: Could not create connection to database server
```

**Solutions:**
```bash
# For H2 (local profile)
# Check if H2 files exist
ls -la userdb*

# Delete corrupted database
rm userdb*

# For MySQL (dev profile)
# Check MySQL service
brew services list | grep mysql

# Start MySQL
brew services start mysql
```

---

## 🔐 Authentication Issues

### Issue: Login Fails with "Invalid credentials"
```json
{
  "success": false,
  "message": "Invalid credentials"
}
```

**Debug Steps:**
```bash
# Check if user exists in database
curl -X GET "http://localhost:8444/h2-console"
# JDBC URL: jdbc:h2:file:./userdb
# Query: SELECT * FROM users WHERE username = 'your_username';

# Check password encoding
# The service uses BCrypt, so passwords in DB are hashed
```

**Common Causes:**
- Wrong username/email
- Wrong password
- User account locked
- Email not verified

### Issue: JWT Token Invalid
```json
{
  "success": false,
  "message": "Invalid token"
}
```

**Debug Steps:**
```bash
# Check token structure at jwt.io
# Verify token hasn't expired
# Check if user still exists

# Validate token programmatically
curl -X POST "http://localhost:8444/oauth2/introspect" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "token=YOUR_TOKEN" \
  --user "product-service:product-service-secret"
```

---

## 🌐 OAuth2 Issues

### Issue: Client Credentials Flow Fails
```json
{
  "error": "invalid_client"
}
```

**Debug Steps:**
```bash
# Bootstrap the client first
curl -X POST "http://localhost:8444/clients/bootstrap/product-service"

# Verify client exists
curl -X GET "http://localhost:8444/clients/product-service"

# Check client credentials
curl -X POST "http://localhost:8444/oauth2/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&scope=user.read" \
  --user "product-service:product-service-secret"
```

### Issue: Authorization Code Flow Not Working
```
Error: redirect_uri_mismatch
```

**Debug Steps:**
```bash
# Check registered redirect URIs
curl -X GET "http://localhost:8444/clients/product-service"

# Ensure redirect URI matches exactly
# http://localhost:3000/callback vs http://localhost:3000/callback/
```

---

## 🗄️ Database Issues

### Issue: H2 Console Not Accessible
```
Connection refused
```

**Solutions:**
```bash
# Check if H2 console is enabled (it should be by default)
# Access: http://localhost:8444/h2-console
# JDBC URL: jdbc:h2:file:./userdb
# Username: sa
# Password: (leave empty)
```

### Issue: Data Not Persisting
```
Data disappears after restart
```

**Solutions:**
```bash
# H2 file database should persist data
# Check file permissions
ls -la userdb*

# If using wrong JDBC URL
# Correct: jdbc:h2:file:./userdb
# Wrong: jdbc:h2:mem:testdb (in-memory only)
```

### Issue: MySQL Connection Issues
```
Communications link failure
```

**Solutions:**
```bash
# Check MySQL service status
brew services list | grep mysql

# Verify connection details in application-dev.properties
spring.datasource.url=jdbc:mysql://localhost:3306/userdb
spring.datasource.username=your_username
spring.datasource.password=your_password

# Test MySQL connection
mysql -u your_username -p -e "SELECT 1"
```

---

## 🔧 API Issues

### Issue: 403 Forbidden on Protected Endpoints
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied"
}
```

**Debug Steps:**
```bash
# Check if JWT token is provided
curl -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8444/users/profile

# Verify token is not expired
# Check user has required permissions
# For admin endpoints, ensure user has ADMIN role
```

### Issue: 400 Bad Request - Validation Errors
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": {
    "password": "Password must be at least 8 characters",
    "email": "Invalid email format"
  }
}
```

**Debug Steps:**
```bash
# Check field requirements
# Username: 3-50 characters
# Password: 8+ characters with special chars
# Email: Valid email format
# Phone: Valid phone number format
```

### Issue: 404 Not Found
```json
{
  "status": 404,
  "error": "Not Found"
}
```

**Debug Steps:**
```bash
# Check endpoint URL
# Verify HTTP method (GET, POST, PUT, DELETE)
# Check if user exists for user-specific endpoints
# Verify path parameters
```

---

## 🔍 Logging and Debugging

### Enable Debug Logging
```properties
# src/main/resources/application.properties
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.security.oauth2=DEBUG
logging.level.com.user.service=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### View Application Logs
```bash
# Real-time logs
./mvnw spring-boot:run

# Or view log file
tail -f logs/user-service.log
```

### Check Application Health
```bash
# Health check
curl http://localhost:8444/actuator/health

# Application info
curl http://localhost:8444/actuator/info

# Metrics
curl http://localhost:8444/actuator/metrics
```

---

## 🐛 Common Development Issues

### Issue: Changes Not Reflected
```
Code changes not taking effect
```

**Solutions:**
```bash
# Clean and rebuild
./mvnw clean compile

# If using DevTools, changes should be auto-reloaded
# Check if DevTools is enabled in pom.xml

# Restart the application
./mvnw spring-boot:run
```

### Issue: Tests Failing
```
Unit tests or integration tests failing
```

**Debug Steps:**
```bash
# Run specific test
./mvnw test -Dtest=UserServiceTest

# Run with verbose output
./mvnw test -DforkCount=1 -DreuseForks=false

# Check test database configuration
# Tests might need @TestPropertySource or @ActiveProfiles
```

### Issue: Maven Dependency Issues
```
Could not resolve dependencies
```

**Solutions:**
```bash
# Clear Maven cache
./mvnw dependency:purge-local-repository

# Force download
./mvnw dependency:resolve

# Check repository access
# Verify proxy settings if behind corporate firewall
```

---

## 🚀 Performance Issues

### Issue: Slow Response Times
```
API responses taking too long
```

**Debug Steps:**
```bash
# Enable SQL logging
logging.level.org.hibernate.SQL=DEBUG

# Check database query performance
# Look for N+1 query problems
# Consider adding indexes

# Check JVM memory usage
# Monitor garbage collection
```

### Issue: High Memory Usage
```
Application consuming too much memory
```

**Solutions:**
```bash
# Set JVM memory limits
java -Xmx512m -Xms256m -jar target/user-service.jar

# Check for memory leaks
# Monitor heap usage
# Use profiling tools like VisualVM
```

---

## 🔒 Security Issues

### Issue: CORS Errors
```
Access to XMLHttpRequest blocked by CORS policy
```

**Solutions:**
```java
// Add to SpringSecurityConfig.java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

### Issue: CSRF Token Issues
```
Invalid CSRF token
```

**Solutions:**
```java
// For API-only applications, disable CSRF
http.csrf().disable();
```

---

## 🐧 Platform-Specific Issues

### macOS Issues
```bash
# Port already in use
lsof -ti:8444 | xargs kill -9

# Java version issues
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

### Windows Issues
```cmd
# Check port usage
netstat -ano | findstr :8444

# Kill process
taskkill /PID <PID> /F

# Set environment variables
set JAVA_HOME=C:\Program Files\Java\jdk-17
```

### Linux Issues
```bash
# Check port usage
netstat -tlnp | grep :8444

# Kill process
kill -9 <PID>

# Set environment variables
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

---

## 📞 Getting Help

### Debug Checklist
- [ ] Is the service running? `curl http://localhost:8444/actuator/health`
- [ ] Are the correct profiles active?
- [ ] Is the database accessible?
- [ ] Are the logs showing any errors?
- [ ] Is the JWT token valid and not expired?
- [ ] Are the request headers correct?
- [ ] Is the request body properly formatted?

### Useful Resources
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security OAuth2](https://docs.spring.io/spring-security/reference/servlet/oauth2/)
- [JWT.io](https://jwt.io/) - JWT debugger
- [H2 Database](http://h2database.com/html/main.html)

### Community Support
- Check existing GitHub issues
- Review Stack Overflow questions
- Join Spring Boot community forums

---

## 🚨 Emergency Fixes

### Complete Reset
```bash
# Stop the service
pkill -f "user-service"

# Clean everything
./mvnw clean
rm -rf target/
rm -f userdb*

# Rebuild and run
./mvnw spring-boot:run
```

### Database Reset
```bash
# Delete H2 database files
rm -f userdb*

# Or reset MySQL database
mysql -u root -p -e "DROP DATABASE userdb; CREATE DATABASE userdb;"
```

---

*This troubleshooting guide covers the most common issues you'll encounter. If you can't find a solution here, check the logs and consider creating an issue with detailed information about your problem.*</content>
<parameter name="filePath">/Users/admin/IdeaProjects/ecommerce/user-service/TROUBLESHOOTING.md
