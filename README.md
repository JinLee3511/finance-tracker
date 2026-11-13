# Finance Tracker

A personal project to track income and expenses.

Built with Spring Boot and MySQL. Transactions (income or expense) are recorded and
grouped into categories, so spending can be reviewed over time.

## Run

1. Create a MySQL database named `finance_tracker`.
2. Add a `.env` file in the project root with `MYSQL_PASSWORD=your_password`.
3. `./gradlew bootRun`
