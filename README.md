# SWIFT Codes API

## Project Description

This application serves to manage and expose SWIFT (BIC) codes for banks and their branches. It allows for processing, storing, and providing access to SWIFT code information through a REST API.

## Features

- Parsing SWIFT code data from a file
- Storing data in a database
- Providing REST API with the following endpoints:
  - `GET /v1/swift-codes/{swift-code}` - retrieve details of a single SWIFT code
  - `GET /v1/swift-codes/country/{countryISO2code}` - retrieve all SWIFT codes for a specific country
  - `POST /v1/swift-codes` - add a new SWIFT code
  - `DELETE /v1/swift-codes/{swift-code}` - delete a SWIFT code

## Technologies

- Java
- Spring Boot
- Maven
- Docker
- Docker Compose
- PostgreSQL

## System Requirements

- Docker
- Docker Compose

## Installation and Launch

### Step 1: Clone the repository

```bash
git clone https://github.com/EmilKulka/remitly-internship-home-exercise
cd remitly-internship-home-exercise
```

### Step 2: Configure environment variables

Create a `.env` file based on the available `.env.example` template:

```bash
cp .env.example .env
```

Open the `.env` file and adjust the variable values as needed:

```
# Example variables - adjust to your project
SPRING_DATASOURCE_URL=jdbc:postgresql://swift-code-db:5432/swift-code-db
POSTGRES_USER=root
POSTGRES_PASSWORD=foobar
POSTGRES_DB=swift-code-db
```

### Step 3: Launch the application

Run the application using Docker Compose:

```bash
docker compose -f docker/docker-compose.yaml --env-file .env up --build
```

The application will be available at `http://localhost:8080`.

## Testing

The project includes unit and integration tests that are run in the CI pipeline. Running tests locally is not required, but they can be executed using:

```bash
mvn test
```
