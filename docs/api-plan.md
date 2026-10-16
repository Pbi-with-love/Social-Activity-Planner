# Social Activity Planner — API Plan

## 1. Users
- `POST /api/v1/users` — Register
- `GET /api/v1/users/me` — Get current user's profile
- `PATCH /api/v1/users/me` — Update current user's profile

## 2. Groups
- `POST /api/v1/groups` — Create a group
- `GET /api/v1/groups` — Browse groups
- `GET /api/v1/groups/{groupId}` — Get group details
- `PATCH /api/v1/groups/{groupId}` — Update a group
- `DELETE /api/v1/groups/{groupId}` — Delete a group

## 3. Group Members
- `POST /api/v1/groups/{groupId}/members` — Join a group
- `GET /api/v1/groups/{groupId}/members` — List group members
- `DELETE /api/v1/groups/{groupId}/members/me` — Leave a group

## 4. Messages
- `POST /api/v1/groups/{groupId}/messages` — Send a message
- `GET /api/v1/groups/{groupId}/messages` — List messages
- `PATCH /api/v1/groups/{groupId}/messages/{messageId}` — Edit a message
- `DELETE /api/v1/groups/{groupId}/messages/{messageId}` — Delete a message

## 5. Decisions to define
- Authentication and authorization rules
- Request and response DTOs
- Validation rules
- HTTP status codes and error response format
- Pagination and sorting
- Whether group membership is required to read or send messages

Status: Proposed — endpoints are not implemented yet.