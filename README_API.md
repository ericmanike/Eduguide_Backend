# Eduguide API Documentation

This document provides comprehensive documentation for the Eduguide API endpoints and authentication system.

## Base URL

**Development:** `http://localhost:8080/api`
**Production:** `https://your-domain.com/api`

## Authentication

The API uses **JWT (JSON Web Token)** authentication for protected endpoints.

### Security Configuration

- **Public Endpoints:** `/api/users/register`, `/api/users/login`
- **Protected Endpoints:** All other `/api/**` endpoints require authentication
- **Admin-Only Endpoints:** `/api/admin/**` requires ADMIN role
- **Token Expiration:** 24 hours (configurable via `JWT_EXPIRATION` environment variable)

### JWT Token Structure

The JWT token contains the following claims in its payload:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "role": "STUDENT",
  "name": "John Doe",
  "sub": "user@example.com",
  "iat": 1640000000,
  "exp": 1640086400
}
```

### Using Bearer Token

Include the JWT token in the `Authorization` header for all protected requests:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Decoding JWT on Frontend

You don't need the secret key to decode the token (only to verify it on the backend):

```javascript
// Using jwt-decode library
import { jwtDecode } from 'jwt-decode';

const decoded = jwtDecode(token);
console.log(decoded.id);     // User UUID
console.log(decoded.email);  // User email
console.log(decoded.role);   // STUDENT or ADMIN
console.log(decoded.name);   // User name
```

Or manually:

```javascript
function decodeToken(token) {
  const base64Url = token.split('.')[1];
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const jsonPayload = decodeURIComponent(
    atob(base64)
      .split('')
      .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
      .join('')
  );
  return JSON.parse(jsonPayload);
}
```

---

## API Endpoints

### 1. User Management (`/api/users`)

#### Register User
**POST** `/api/users/register` (Public)

Registers a new user account.

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "STUDENT"  // Optional: "STUDENT" (default) or "ADMIN"
}
```

**Success Response (200 OK):**
```json
"User registered successfully!"
```

**Error Responses:**
- `400 Bad Request` - "Error: Email is required!"
- `400 Bad Request` - "Error: Name is required!"
- `400 Bad Request` - "Error: Password is required!"
- `400 Bad Request` - "Error: Email is already taken!"

---

#### Login User
**POST** `/api/users/login` (Public)

Authenticates a user and returns a JWT token.

**Request Body:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "John Doe",
  "email": "john@example.com",
  "role": "STUDENT"
}
```

**Error Response:**
- `401 Unauthorized` - "Error: Invalid email or password!"

---

#### Get All Users
**GET** `/api/users` (Protected)

Returns a list of all registered users.

**Headers:**
```
Authorization: Bearer <token>
```

**Success Response (200 OK):**
```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "john@example.com",
    "name": "John Doe",
    "role": "STUDENT",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
]
```

---

#### Get User by ID
**GET** `/api/users/{id}` (Protected)

Returns a specific user by their UUID.

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (UUID) - User identifier

**Success Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "john@example.com",
  "name": "John Doe",
  "role": "STUDENT",
  "createdAt": "2024-01-15T10:30:00Z",
  "updatedAt": "2024-01-15T10:30:00Z"
}
```

**Error Response:**
- `404 Not Found`

---

#### Update User
**PUT** `/api/users/{id}` (Protected)

Updates user information. All fields are optional.

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (UUID) - User identifier

**Request Body:**
```json
{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "newpassword123",
  "role": "ADMIN"
}
```

**Success Response (200 OK):**
```json
"User updated successfully!"
```

**Error Response:**
- `404 Not Found`

---

#### Delete User
**DELETE** `/api/users/{id}` (Protected)

Deletes a user account.

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `id` (UUID) - User identifier

**Success Response (200 OK):**
```json
"User deleted successfully!"
```

**Error Response:**
- `404 Not Found`

---

### 2. Other Endpoints (Protected)

All endpoints below require authentication via Bearer token.

**Headers Required:**
```
Authorization: Bearer <token>
```

#### User Skills (`/api/user-skills`)
- **GET** `/api/user-skills` - List all user-skill associations
- **GET** `/api/user-skills/user/{userId}` - Get skills for a specific user
- **POST** `/api/user-skills` - Add a skill to a user
- **PUT** `/api/user-skills/{id}` - Update mastery level
- **DELETE** `/api/user-skills/{id}` - Remove a skill from a user

#### User Learning Paths (`/api/user-learning-paths`)
- **GET** `/api/user-learning-paths` - List all user-path associations
- **GET** `/api/user-learning-paths/user/{userId}` - Get paths for a specific user
- **GET** `/api/user-learning-paths/user/{userId}/active` - Get active paths for a user
- **POST** `/api/user-learning-paths` - Assign a path to a user
- **PUT** `/api/user-learning-paths/{id}` - Update progress/match score
- **DELETE** `/api/user-learning-paths/{id}` - Unassign a path

#### User Module Progress (`/api/user-module-progress`)
- **GET** `/api/user-module-progress` - List all progress records
- **GET** `/api/user-module-progress/user/{userId}` - Get progress for a user
- **POST** `/api/user-module-progress` - Create a progress record
- **PUT** `/api/user-module-progress/{id}` - Update progress status
- **DELETE** `/api/user-module-progress/{id}` - Delete progress record

#### Transactions (`/api/transactions`)
- **GET** `/api/transactions` - List all transactions
- **GET** `/api/transactions/{id}` - Get transaction by ID
- **GET** `/api/transactions/user/{userId}` - Get transactions for a user
- **POST** `/api/transactions` - Create a new transaction
- **DELETE** `/api/transactions/{id}` - Delete a transaction

#### Activity Logs (`/api/activity-logs`)
- **GET** `/api/activity-logs` - List all activity logs
- **GET** `/api/activity-logs/user/{userId}` - Get logs for a user
- **POST** `/api/activity-logs` - Create a new log
- **DELETE** `/api/activity-logs/{id}` - Delete a log

#### Learning Paths (`/api/learning-paths`)
- **GET** `/api/learning-paths` - List all learning paths
- **GET** `/api/learning-paths/{id}` - Get path by ID
- **POST** `/api/learning-paths` - Create a new path
- **PUT** `/api/learning-paths/{id}` - Update path details
- **DELETE** `/api/learning-paths/{id}` - Delete a path

#### Modules (`/api/modules`)
- **GET** `/api/modules` - List all modules
- **GET** `/api/modules/{id}` - Get module by ID
- **POST** `/api/modules` - Create a new module
- **PUT** `/api/modules/{id}` - Update module details
- **DELETE** `/api/modules/{id}` - Delete a module

#### Skills (`/api/skills`)
- **GET** `/api/skills` - List all skills
- **GET** `/api/skills/{id}` - Get skill by ID
- **POST** `/api/skills` - Create a new skill
- **PUT** `/api/skills/{id}` - Update skill name
- **DELETE** `/api/skills/{id}` - Delete a skill

#### Path-Module Mapping (`/api/path-modules`)
- **GET** `/api/path-modules` - List all mappings
- **GET** `/api/path-modules/path/{pathId}` - Get modules for a path (ordered)
- **POST** `/api/path-modules` - Map a module to a path
- **PUT** `/api/path-modules/{id}` - Update sequence order
- **DELETE** `/api/path-modules/{id}` - Remove module from path

---

## Data Models

### User
```typescript
{
  id: string;           // UUID
  email: string;
  name: string;
  role: "STUDENT" | "ADMIN";
  createdAt: string;    // ISO 8601 datetime
  updatedAt: string;    // ISO 8601 datetime
}
```

### UserRole Enum
- `STUDENT`
- `ADMIN`

---

## Frontend Integration Example

### React/Next.js API Client

```javascript
const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

// Login and store token
async function login(email, password) {
  const response = await fetch(`${API_URL}/users/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email, password })
  });

  if (!response.ok) {
    const error = await response.text();
    throw new Error(error);
  }

  const data = await response.json();

  // Store token in localStorage or cookies
  localStorage.setItem('token', data.token);
  localStorage.setItem('user', JSON.stringify({
    id: data.id,
    name: data.name,
    email: data.email,
    role: data.role
  }));

  return data;
}

// Register new user
async function register(name, email, password, role = 'STUDENT') {
  const response = await fetch(`${API_URL}/users/register`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name, email, password, role })
  });

  if (!response.ok) {
    const error = await response.text();
    throw new Error(error);
  }

  return await response.text();
}

// Protected API call
async function getUsers() {
  const token = localStorage.getItem('token');

  const response = await fetch(`${API_URL}/users`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });

  if (response.status === 401) {
    // Token expired or invalid
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    throw new Error('Unauthorized - please login again');
  }

  if (!response.ok) {
    throw new Error('Failed to fetch users');
  }

  return await response.json();
}

// Logout
function logout() {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
}

// Get current user from token
function getCurrentUser() {
  const token = localStorage.getItem('token');
  if (!token) return null;

  try {
    const decoded = jwtDecode(token);

    // Check if token is expired
    if (decoded.exp * 1000 < Date.now()) {
      logout();
      return null;
    }

    return {
      id: decoded.id,
      email: decoded.email,
      role: decoded.role,
      name: decoded.name
    };
  } catch (error) {
    logout();
    return null;
  }
}
```

### Axios Interceptor (Alternative)

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api'
});

// Add token to all requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Handle 401 errors globally
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

---

## Environment Variables

Required environment variables for the backend:

```bash
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/eduguide
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password

# JWT (minimum 32 characters for HS256)
JWT_SECRET=your-super-secret-jwt-key-minimum-32-characters-long
JWT_EXPIRATION=86400000

# Server
PORT=8080
```

---

## Error Handling

All error responses follow this pattern:

- **400 Bad Request** - Invalid input or validation error
- **401 Unauthorized** - Missing or invalid authentication token
- **403 Forbidden** - Authenticated but lacks required permissions
- **404 Not Found** - Resource not found
- **500 Internal Server Error** - Server-side error

Error response body is typically a plain string message.

---

## Notes

- All timestamps are in UTC using ISO 8601 format
- UUIDs are used for all entity identifiers
- Passwords are never returned in API responses
- Token expiration is 24 hours (configurable)
- CORS is configured for development (adjust for production)

---

## Swagger UI

For interactive API documentation, visit:

```
http://localhost:8080/swagger-ui.html
```
