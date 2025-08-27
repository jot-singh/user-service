# OAuth2 Authorization Server Test Commands

## Server Information
- **Authorization Server URL**: http://localhost:8444
- **Environment**: Local development with H2 database
- **Profile**: local

## OAuth2 Endpoints

### 1. Authorization Server Metadata
```bash
curl -s "http://localhost:8444/.well-known/oauth-authorization-server" | python3 -m json.tool
```

### 2. JWK Set (for JWT signature verification)
```bash
curl -s "http://localhost:8444/oauth2/jwks" | python3 -m json.tool
```

## Client Management

### 1. Bootstrap Product Service Client
```bash
curl -X POST "http://localhost:8444/clients/bootstrap/product-service" \
  -H "Content-Type: application/json" \
  -d '{
    "scopes": ["user.read", "user.validate"]
  }'
```

## OAuth2 Flows

### 1. Client Credentials Flow (Service-to-Service)
```bash
# Get access token
curl -X POST http://localhost:8444/oauth2/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&scope=user.read" \
  --user "product-service:product-service-secret"
```

### 2. Token Introspection
```bash
# Replace TOKEN with actual access token from above
curl -X POST "http://localhost:8444/oauth2/introspect" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "token=TOKEN" \
  --user "product-service:product-service-secret"
```

## Integration with Product Service

The Product Service can now:

1. **Authenticate as a client**:
   - Client ID: `product-service`
   - Client Secret: `product-service-secret`
   - Scopes: `user.read`, `user.validate`

2. **Get access tokens** using client credentials flow

3. **Validate user tokens** by calling the introspection endpoint

4. **Verify JWT signatures** using the JWK Set endpoint

## Next Steps

1. Update Product Service to use OAuth2 client credentials
2. Implement user validation endpoints in User Service
3. Test end-to-end service-to-service communication
4. Configure production OAuth2 clients for different environments

## Security Notes

- Client secrets should be stored securely in production
- Use environment variables for sensitive configuration
- Consider rotating client secrets regularly
- Implement proper scope-based authorization
