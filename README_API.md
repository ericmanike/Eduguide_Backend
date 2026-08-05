# Eduguide API Documentation

This document provides an overview of the available API endpoints in the Eduguide application and their current security status.

## Security Status

**Current Protection: None**

All endpoints starting with `/api/` are currently configured to be **publicly accessible** (`.permitAll()`). 

In `SecurityConfig.java`:
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/**").permitAll()
    .anyRequest().authenticated()
);
```

> **Note:** It is highly recommended to secure these endpoints using JWT authentication before deploying to production, except for the `/api/users/register` and `/api/users/login` endpoints.

---

## API Endpoints

### 1. User Management (`/api/users`)

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/api/users` | List all users | N/A |
| GET | `/api/users/{id}` | Get user by ID | N/A |
| POST | `/api/users/register` | Register a new user | `RegisterRequest` |
| POST | `/api/users/login` | Login and get JWT | `LoginRequest` |
| PUT | `/api/users/{id}` | Update user details | `RegisterRequest` |
| DELETE | `/api/users/{id}` | Delete a user | N/A |

### 2. User Skills (`/api/user-skills`)

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/api/user-skills` | List all user-skill associations | N/A |
| GET | `/api/user-skills/user/{userId}` | Get skills for a specific user | N/A |
| POST | `/api/user-skills` | Add a skill to a user | `UserSkillRequest` |
| PUT | `/api/user-skills/{id}` | Update mastery level | `UserSkillRequest` |
| DELETE | `/api/user-skills/{id}` | Remove a skill from a user | N/A |

### 3. User Learning Paths (`/api/user-learning-paths`)

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/api/user-learning-paths` | List all user-path associations | N/A |
| GET | `/api/user-learning-paths/user/{userId}` | Get paths for a specific user | N/A |
| GET | `/api/user-learning-paths/user/{userId}/active` | Get active paths for a user | N/A |
| POST | `/api/user-learning-paths` | Assign a path to a user | `UserLearningPathRequest` |
| PUT | `/api/user-learning-paths/{id}` | Update progress/match score | `UserLearningPathRequest` |
| DELETE | `/api/user-learning-paths/{id}` | Unassign a path | N/A |

### 4. User Module Progress (`/api/user-module-progress`)

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/api/user-module-progress` | List all progress records | N/A |
| GET | `/api/user-module-progress/user/{userId}` | Get progress for a user | N/A |
| POST | `/api/user-module-progress` | Create a progress record | `UserModuleProgressRequest` |
| PUT | `/api/user-module-progress/{id}` | Update progress status | `UserModuleProgressRequest` |
| DELETE | `/api/user-module-progress/{id}` | Delete progress record | N/A |

### 5. Transactions (`/api/transactions`)

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/api/transactions` | List all transactions | N/A |
| GET | `/api/transactions/{id}` | Get transaction by ID | N/A |
| GET | `/api/transactions/user/{userId}` | Get transactions for a user | N/A |
| POST | `/api/transactions` | Create a new transaction | `TransactionRequest` |
| DELETE | `/api/transactions/{id}` | Delete a transaction | N/A |

### 6. Activity Logs (`/api/activity-logs`)

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/api/activity-logs` | List all activity logs | N/A |
| GET | `/api/activity-logs/user/{userId}` | Get logs for a user | N/A |
| POST | `/api/activity-logs` | Create a new log | `ActivityLogRequest` |
| DELETE | `/api/activity-logs/{id}` | Delete a log | N/A |

### 7. Learning Paths (`/api/learning-paths`)

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/api/learning-paths` | List all learning paths | N/A |
| GET | `/api/learning-paths/{id}` | Get path by ID | N/A |
| POST | `/api/learning-paths` | Create a new path | `LearningPath` |
| PUT | `/api/learning-paths/{id}` | Update path details | `LearningPath` |
| DELETE | `/api/learning-paths/{id}` | Delete a path | N/A |

### 8. Modules (`/api/modules`)

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/api/modules` | List all modules | N/A |
| GET | `/api/modules/{id}` | Get module by ID | N/A |
| POST | `/api/modules` | Create a new module | `Module` |
| PUT | `/api/modules/{id}` | Update module details | `Module` |
| DELETE | `/api/modules/{id}` | Delete a module | N/A |

### 9. Skills (`/api/skills`)

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/api/skills` | List all skills | N/A |
| GET | `/api/skills/{id}` | Get skill by ID | N/A |
| POST | `/api/skills` | Create a new skill | `Skill` |
| PUT | `/api/skills/{id}` | Update skill name | `Skill` |
| DELETE | `/api/skills/{id}` | Delete a skill | N/A |

### 10. Path-Module Mapping (`/api/path-modules`)

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/api/path-modules` | List all mappings | N/A |
| GET | `/api/path-modules/path/{pathId}` | Get modules for a path (ordered) | N/A |
| POST | `/api/path-modules` | Map a module to a path | `PathModuleRequest` |
| PUT | `/api/path-modules/{id}` | Update sequence order | `PathModuleRequest` |
| DELETE | `/api/path-modules/{id}` | Remove module from path | N/A |
