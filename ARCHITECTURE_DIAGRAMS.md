# User Service Architecture Diagrams 🏗️

## System Architecture Overview

```mermaid
graph TB
    subgraph "Client Applications"
        SPA[Single Page App]
        Mobile[Mobile App]
        ProductSvc[Product Service]
    end

    subgraph "API Gateway / Load Balancer"
        Gateway[API Gateway]
    end

    subgraph "User Service"
        subgraph "Spring Boot Application"
            Controllers[REST Controllers]
            Services[Business Services]
            Security[Security Layer]
            OAuth2[OAuth2 Server]
        end

        subgraph "Data Layer"
            JPA[JPA/Hibernate]
            H2[(H2 Database)]
            MySQL[(MySQL Database)]
        end
    end

    subgraph "External Services"
        EmailSvc[Email Service]
        SMSSvc[SMS Service]
    end

    SPA --> Gateway
    Mobile --> Gateway
    ProductSvc --> Gateway

    Gateway --> Controllers
    Controllers --> Services
    Services --> Security
    Services --> OAuth2
    Services --> JPA

    JPA --> H2
    JPA --> MySQL

    Services --> EmailSvc
    Services --> SMSSvc

    style UserService fill:#e1f5fe
    style Controllers fill:#c8e6c9
    style Services fill:#fff3e0
    style Security fill:#ffcdd2
    style OAuth2 fill:#f3e5f5
```

## Component Interaction Flow

```mermaid
sequenceDiagram
    participant Client
    participant Gateway
    participant AuthCtrl as AuthController
    participant AuthSvc as AuthService
    participant UserRepo as UserRepository
    participant JWTSvc as JWTService
    participant DB as Database

    Client->>Gateway: POST /auth/login
    Gateway->>AuthCtrl: Forward request
    AuthCtrl->>AuthSvc: login(request)
    AuthSvc->>UserRepo: findByUsername()
    UserRepo->>DB: SELECT * FROM users
    DB-->>UserRepo: User data
    UserRepo-->>AuthSvc: User entity
    AuthSvc->>JWTSvc: generateToken(user)
    JWTSvc-->>AuthSvc: JWT token
    AuthSvc-->>AuthCtrl: AuthResponse
    AuthCtrl-->>Gateway: Response
    Gateway-->>Client: JWT Token
```

## OAuth2 Authorization Code Flow

```mermaid
sequenceDiagram
    participant User
    participant Client
    participant AuthServer as User Service
    participant ResourceServer as Product Service

    User->>Client: Click "Login"
    Client->>AuthServer: GET /oauth2/authorize?response_type=code&client_id=...
    AuthServer->>User: Redirect to login page
    User->>AuthServer: Enter credentials
    AuthServer->>AuthServer: Validate user
    AuthServer->>User: Show consent screen
    User->>AuthServer: Grant permission
    AuthServer->>Client: Redirect with auth code
    Client->>AuthServer: POST /oauth2/token (code + client_secret)
    AuthServer->>AuthServer: Validate code
    AuthServer-->>Client: Access token + Refresh token
    Client->>ResourceServer: API call with token
    ResourceServer->>AuthServer: POST /oauth2/introspect (validate token)
    AuthServer-->>ResourceServer: Token validation result
    ResourceServer-->>Client: Protected resource
```

## Database Schema Diagram

```mermaid
erDiagram
    USER ||--o{ ADDRESS : has
    USER ||--o{ SESSION : manages
    USER ||--o{ VERIFICATION_TOKEN : receives
    USER }o--|| ROLE : belongs_to

    USER {
        bigint id PK
        varchar username UK
        varchar password
        varchar email UK
        varchar first_name
        varchar last_name
        varchar phone
        enum role
        boolean email_verified
        boolean account_locked
        int failed_login_attempts
        datetime created_at
        datetime updated_at
        datetime last_login
    }

    ADDRESS {
        bigint id PK
        bigint user_id FK
        varchar street
        varchar city
        varchar state
        varchar zip_code
        varchar country
        enum type
    }

    SESSION {
        bigint id PK
        bigint user_id FK
        varchar session_id
        datetime expires_at
        varchar ip_address
        varchar user_agent
    }

    VERIFICATION_TOKEN {
        bigint id PK
        bigint user_id FK
        varchar token
        datetime expires_at
        enum type
    }

    ROLE {
        enum name PK "CUSTOMER,MERCHANT,ADMIN"
        varchar description
    }
```

## Security Architecture

```mermaid
graph TB
    subgraph "Security Layers"
        subgraph "Network Layer"
            HTTPS[HTTPS/TLS]
            CORS[CORS Policy]
            RateLimit[Rate Limiting]
        end

        subgraph "Application Layer"
            AuthN[Authentication]
            AuthZ[Authorization]
            InputVal[Input Validation]
            XSSProt[XSS Protection]
        end

        subgraph "Data Layer"
            Enc[Password Encryption]
            SQLInj[SQL Injection Prevention]
            Audit[Audit Logging]
        end
    end

    subgraph "Security Components"
        JWT[JWT Tokens]
        OAuth2[OAuth2 Server]
        BCrypt[BCrypt Passwords]
        CSRF[CSRF Protection]
        Session[Session Management]
    end

    HTTPS --> AuthN
    CORS --> AuthN
    RateLimit --> AuthN

    AuthN --> AuthZ
    AuthZ --> InputVal
    InputVal --> XSSProt

    XSSProt --> Enc
    Enc --> SQLInj
    SQLInj --> Audit

    JWT --> AuthN
    OAuth2 --> AuthN
    BCrypt --> Enc
    CSRF --> AuthZ
    Session --> AuthN

    style SecurityLayers fill:#ffebee
    style SecurityComponents fill:#e8f5e8
```

## Deployment Architecture

```mermaid
graph TB
    subgraph "Production Environment"
        subgraph "Load Balancer"
            LB[NGINX/HAProxy]
        end

        subgraph "Application Servers"
            App1[User Service #1]
            App2[User Service #2]
            App3[User Service #3]
        end

        subgraph "Databases"
            Master[(MySQL Master)]
            Slave1[(MySQL Slave #1)]
            Slave2[(MySQL Slave #2)]
        end

        subgraph "Cache & Session Store"
            Redis[(Redis Cluster)]
        end

        subgraph "Monitoring"
            Prometheus[Prometheus]
            Grafana[Grafana]
            ELK[ELK Stack]
        end
    end

    LB --> App1
    LB --> App2
    LB --> App3

    App1 --> Master
    App2 --> Master
    App3 --> Master

    Master --> Slave1
    Master --> Slave2

    App1 --> Redis
    App2 --> Redis
    App3 --> Redis

    App1 --> Prometheus
    App2 --> Prometheus
    App3 --> Prometheus

    Prometheus --> Grafana
    App1 --> ELK
    App2 --> ELK
    App3 --> ELK

    style ProductionEnvironment fill:#f3e5f5
    style LoadBalancer fill:#e8f5e8
    style ApplicationServers fill:#fff3e0
    style Databases fill:#e1f5fe
```

## API Request Flow

```mermaid
graph LR
    A[Client Request] --> B{Authentication Required?}
    B -->|Yes| C[JWT Token Present?]
    B -->|No| D[Process Request]

    C -->|Yes| E[Validate JWT Token]
    C -->|No| F[Return 401 Unauthorized]

    E -->|Valid| G[Extract User Context]
    E -->|Invalid/Expired| F

    G --> H{Authorization Required?}
    H -->|Yes| I[Check User Permissions]
    H -->|No| D

    I -->|Authorized| D
    I -->|Not Authorized| J[Return 403 Forbidden]

    D --> K[Input Validation]
    K -->|Valid| L[Business Logic]
    K -->|Invalid| M[Return 400 Bad Request]

    L --> N[Database Operation]
    N --> O[Return Response]

    style A fill:#e8f5e8
    style F fill:#ffebee
    style J fill:#ffebee
    style M fill:#ffebee
    style O fill:#c8e6c9
```

## Error Handling Flow

```mermaid
graph TD
    A[Request] --> B{Exception Occurs}
    B -->|Validation Error| C[ValidationException]
    B -->|Authentication Error| D[AuthenticationException]
    B -->|Authorization Error| E[AccessDeniedException]
    B -->|Business Logic Error| F[BusinessException]
    B -->|System Error| G[RuntimeException]

    C --> H[Global Exception Handler]
    D --> H
    E --> H
    F --> H
    G --> H

    H --> I{Exception Type}
    I -->|Validation| J[Return 400 + Field Errors]
    I -->|Authentication| K[Return 401 + Error Message]
    I -->|Authorization| L[Return 403 + Error Message]
    I -->|Business| M[Return 400/404/409 + Business Message]
    I -->|System| N[Return 500 + Generic Message]

    J --> O[Log Error Details]
    K --> O
    L --> O
    M --> O
    N --> O

    O --> P[Return Standardized Error Response]

    style A fill:#e8f5e8
    style P fill:#c8e6c9
    style N fill:#ffebee
```

## Performance Monitoring

```mermaid
graph TB
    subgraph "Application Metrics"
        HTTP[HTTP Request/Response]
        DB[Database Queries]
        Cache[Cache Hit/Miss]
        Memory[JVM Memory Usage]
        CPU[CPU Usage]
        Threads[Thread Pool]
    end

    subgraph "Business Metrics"
        Auth[Authentication Success/Failure]
        UserReg[User Registrations]
        TokenGen[Token Generations]
        APIUsage[API Usage by Endpoint]
    end

    subgraph "Infrastructure Metrics"
        Disk[Disk I/O]
        Network[Network I/O]
        LoadAvg[System Load]
        Uptime[Service Uptime]
    end

    subgraph "Monitoring Stack"
        Prometheus[(Prometheus)]
        Grafana[Grafana Dashboard]
        AlertManager[Alert Manager]
        ELK[ELK Stack]
    end

    HTTP --> Prometheus
    DB --> Prometheus
    Cache --> Prometheus
    Memory --> Prometheus
    CPU --> Prometheus
    Threads --> Prometheus

    Auth --> Prometheus
    UserReg --> Prometheus
    TokenGen --> Prometheus
    APIUsage --> Prometheus

    Disk --> Prometheus
    Network --> Prometheus
    LoadAvg --> Prometheus
    Uptime --> Prometheus

    Prometheus --> Grafana
    Prometheus --> AlertManager
    Prometheus --> ELK

    style ApplicationMetrics fill:#e1f5fe
    style BusinessMetrics fill:#fff3e0
    style InfrastructureMetrics fill:#f3e5f5
    style MonitoringStack fill:#e8f5e8
```

## Development Workflow

```mermaid
graph LR
    A[Feature Request] --> B[Create Issue]
    B --> C[Design API]
    C --> D[Write Tests]
    D --> E[Implement Code]
    E --> F[Code Review]
    F --> G[Merge to Main]
    G --> H[Deploy to Staging]
    H --> I[Integration Tests]
    I --> J[Deploy to Production]
    J --> K[Monitor & Alert]

    style A fill:#e8f5e8
    style K fill:#c8e6c9
```

---

*These diagrams provide visual representations of the User Service architecture, flows, and processes. Use them as references for understanding the system design and implementation.*</content>
<parameter name="filePath">/Users/admin/IdeaProjects/ecommerce/user-service/ARCHITECTURE_DIAGRAMS.md
