# Bank Management System

A comprehensive Spring Boot application for managing bank operations with user accounts, transactions, and bank staff functionality.

## Features

### User Features
- User registration and login
- Unique User ID generation
- Account balance viewing
- Transaction history
- Loan applications

### Bank Staff Features
- Unique Bank Person ID generation
- View all customer accounts
- Credit/debit money to customer accounts
- View customer transaction history
- Search and filter customers

### Admin Features
- Administrative dashboard
- System management

## Getting Started

### Prerequisites
- Java 17 or higher
- MySQL database
- Maven

### Database Setup
1. Create a MySQL database named `bankdb`
2. Update the database credentials in `src/main/resources/application.properties`

### Running the Application
1. Navigate to the project directory
2. Run: `mvn spring-boot:run`
3. The application will start on port 8081

### Accessing the Application
- Homepage: http://localhost:8081/
- Login: http://localhost:8081/login
- Register: http://localhost:8081/register

## User Roles

### Regular User (USER)
- Can view their account balance
- Can view transaction history
- Can apply for loans
- Gets a unique 8-character User ID

### Bank Person (BANK_PERSON)
- Can view all customer accounts
- Can credit/debit money to any account
- Can view customer transaction history
- Gets a unique 6-character Bank Person ID

### Admin (ADMIN)
- Full administrative access
- System management capabilities

## API Endpoints

### User Endpoints
- `GET /api/user/balance` - Get user balance
- `GET /api/user/user-id` - Get user ID
- `GET /api/user/account-number` - Get account number
- `GET /api/user/transactions` - Get transaction history
- `GET /api/user/loans` - Get loan applications
- `POST /api/user/loan/apply` - Apply for loan

### Bank Person Endpoints
- `GET /api/bank-person/test` - Test endpoint
- `GET /api/bank-person/bank-person-id` - Get bank person ID
- `GET /api/bank-person/users` - Get all users
- `GET /api/bank-person/users/{userId}` - Get user details
- `POST /api/bank-person/users/{userId}/credit` - Credit money
- `POST /api/bank-person/users/{userId}/debit` - Debit money
- `GET /api/bank-person/users/{userId}/transactions` - Get user transactions

## Testing the System

### Create Test Users
1. Register a regular user with role "USER"
2. Register a bank person with role "BANK_PERSON"
3. Login with each account to test functionality

### Test Bank Person Features
1. Login as a bank person
2. Access the bank dashboard
3. View all customer accounts
4. Test credit/debit operations
5. View transaction history

## Troubleshooting

### Common Issues
1. **Database Connection**: Ensure MySQL is running and credentials are correct
2. **Port Conflicts**: Change the port in application.properties if 8081 is in use
3. **Authentication**: Ensure users have the correct roles assigned

### Error Messages
- "Not authorized" - User doesn't have the required role
- "User not found" - Invalid user ID or username
- "Account not found" - User doesn't have an associated account
- "Insufficient balance" - Trying to debit more than available balance

## Project Structure
```
src/main/java/com/yourcompany/bankmanagement/
├── BankManagementApplication.java
├── controller/
│   ├── AdminController.java
│   ├── BankPersonController.java
│   ├── UserController.java
│   └── PageController.java
├── model/
│   ├── Account.java
│   ├── Loan.java
│   ├── Transaction.java
│   └── User.java
├── repository/
│   ├── AccountRepository.java
│   ├── LoanRepository.java
│   ├── TransactionRepository.java
│   └── UserRepository.java
├── security/
│   └── SecurityConfig.java
└── service/
    ├── AccountService.java
    ├── AdminService.java
    ├── LoanService.java
    └── UserService.java
```

## Security Features
- Password encryption using BCrypt
- Role-based access control
- Session management
- CSRF protection disabled for API endpoints

## Database Schema
- **users**: User accounts with roles and unique IDs
- **accounts**: Bank accounts linked to users
- **transactions**: Transaction records
- **loans**: Loan applications and status

## Future Enhancements
- Email notifications
- Advanced reporting
- Mobile app integration
- Enhanced security features
- Audit logging
