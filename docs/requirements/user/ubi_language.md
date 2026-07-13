# User Service Ubiquitous Language
**Version:** v0.1.1  
**Last Modified:** 2026-07-09

| Term | Definition | Notes |
|------|------------|-------|
| User | A registered person who uses the platform. | Aggregate root in the User Service. |
| Visitor | A person who has not authenticated and can create an account. | |
| Registered user | A user with an account that can authenticate. | |
| Profile | Editable user information shown according to platform visibility rules. | Includes full name, username, birth date, biography, and neighborhood id. |
| Full name | The user's name and surname. | |
| Username | A unique public account handle. | |
| Mail | A unique email address used for account identity and login. | |
| Password hash | A secure stored representation of a password. | Never expose in API responses. |
| Biography | User-written profile description. | Must be meaningful and long enough to read quickly. |
| Neighborhood id | Identifier of the neighborhood the user belongs to. | User references the neighborhood by id only. |
