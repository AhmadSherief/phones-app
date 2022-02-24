# Phones App

An application for listing and filtering phone numbers stored in an sqlite database.

## Table of contents

- [Quick Start](#quick-start)
- [Project Description](#file-description)
- [Summary](#summary)
- [Author](#author)
- [Copyright and License](#copyright-and-license)

## Quick Start

The fastest way to get started is to:

1- Make sure you have [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/) installed.

2- Clone the repo: `git clone https://github.com/AhmadSherief/phones-app.git`

3- Open the command-line and navigate to the folder where you cloned the repo.

4- Run the command `docker-compose up -d`

5- Open [localhost:4300](http://localhost:4300) and you should see the application running.

## Project Description

This project uses angular as the frontend framework, spring-boot as the backend framework, and sqlite for the database.

The frontend displays a list of phones that are fetched from the backend service after specifying some filters in the API request query parameters.

The backend receives the API request and generates a dynamic query that fetches the required records from the sqlite database by applying some regex operations and pagination, and also gets the total count of matching records to provide server-side pagination.

The project backend includes unit-tests, exception-handling, and logging.

## Summary

This project demonstrates the best practices which should be followed when building an application.

It represents a way to overcome the limitation of having a database which does not provide an implementation for a certain function, in this case, SQLite does not provide implementation for the regular expresion comparision, so in order to use this functionality, the solution is to implement a user-defined function and pass it to the database to be called whenever a regex operation is required.

It can be seen from the unit-tests that, evey unit test is completely independent from the others, and whenever dependency-injection is required, mocking the dependency is done, which results in faster and less repeated tests.

Exception handling is implemented to provide readable error messages to the frontend while maintaining the normal, desired flow of the application even when unexpected events occur.

Logging is also important to track the flow of the application specially in the event of error tracking, this helps identify the source of error. For simplicity, logs are not stored in a specific location or file, but idealy it should.

Generating dynamic, native queries is performed to overcome the limitation of unsupported regular expressions implementation, and server-side pagination is implemented to imitate loading large amount of data in real applications.

## Author

### Ahmad Sherief

- [linkedin.com/in/ahmadsherief/](https://www.linkedin.com/in/ahmadsherief/)
- [github.com/AhmadSherief](https://github.com/AhmadSherief)

## Copyright and License

Code is released under the [MIT License](https://github.com/AhmadSherief/phones-app/blob/main/LICENSE).
