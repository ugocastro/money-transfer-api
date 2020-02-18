# Money transfer API
This project is a very simple RESTful API for money transfer using SOLID and KISS principles.

It's built on top of:
- Java JDK 11
- Spark Java web server
- Hibernate ORM
- H2 in-memory database
- JUnit 5

### Maven commands
- `$ mvn test`: Run all the test cases (unit, integration, e2e)
- `$ mvn package`: Create a new jar in the target folder (`target/money-transfer-api-1.0-SNAPSHOT.jar`)

### How to execute standalone application
- `$ java -jar money-transfer-api-1.0-SNAPSHOT.jar`
> This will initialize the server on port `4567`

### Important information
The project has basically only two entities: `Account` and `Transaction`.
- **Account**: has `number`, `owner` and `balance` and can perform `deposit` and `withdraw` operations.

- **Transaction**: has `origin`, `destination`, `amount` and `date` and can perform `transfer` operation.

Restrictions applied:
- Account must have a `owner` and initial balance is `0.00`
- Account/Transaction `number` is generated when object is being created
- There is only one default currency and values must have 2 decimal places
- Only positive amounts are allowed for `deposit` and `withdraw`
- The `withdraw` operation is only allowed if account has sufficient `balance`
- Accounts must exist for any of the operations
- It's not possible to transfer to own account

### Endpoints implemented
|Method|Endpoint|Description|Sample|
|---|---|---|---|
|POST|/api/accounts|Create a new account|`curl -X POST localhost:4567/api/accounts -d '{"owner": "John Doe"}'`|
|PUT|/api/accounts/:id/deposit|Deposit money in account|`curl -X PUT localhost:4567/api/accounts/a3718ab3-088a-41c9-9249-846f823e2760/deposit -d '{"amount": 1000.00}'`|
|PUT|/api/accounts/:id/withdraw|Withdraw money from account|`curl -X PUT localhost:4567/api/accounts/a3718ab3-088a-41c9-9249-846f823e2760/withdraw -d '{"amount": 200}'`|
|POST|/api/transfers|Create a new transfer|`curl -X POST localhost:4567/api/transfers -d '{"originAccountNumber": "a3718ab3-088a-41c9-9249-846f823e2760", "destinationAccountNumber": "33ea092b-45c5-4ff0-a095-095fbfa4e48c", "amount": 250.59}'`|