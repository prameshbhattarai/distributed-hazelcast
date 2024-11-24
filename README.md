# Distributed Hazelcast Caching with REST API

This project implements a **distributed caching system** using **Hazelcast** across **multiple servers**.
The cache is managed by the Hazelcast distributed map, and the servers use REST API endpoints to interact with the
cache.
The application is designed to be run across **3 servers**, each handling cache operations via the REST API.

## Project Structure

- **ServerAResource.java**: The main resource class exposing REST API endpoints.
    - Implements `ping` to check server status.
    - Implements `addToCache` to add data to the distributed cache.
    - Implements `getFromCache` to retrieve data from the distributed cache.
- **CacheConfig.java**: Contains the Hazelcast configuration setup.
- **AsyncCompletionHandler.java**: A utility class to handle asynchronous responses.
- **ServerResponse.java**: A DTO for standardized server responses.

## Prerequisites

To run this application, ensure that you have the following:

- **Java 21**.
- **Maven** to manage dependencies and build the project.
- **3 servers** configured to run the application on different ports.

## Running the Application

You can start the application in **3 different server instances**. Each server will run on a different port, for
example:

- Server 1: `http://localhost:8081`
- Server 2: `http://localhost:8082`
- Server 3: `http://localhost:8083`

## Verify Hazelcast Cluster

After starting all the servers, they should join the same Hazelcast cluster.

## API Endpoints

### 1. Ping Server

**Endpoint**: `/api/ping`

**Method**: `GET`

**Description**: A simple health check endpoint that returns a "pong" response from the server.

#### Example Request:

```bash
curl -X GET 'http://localhost:8081/api/ping' \
      -H 'Accept: application/json' \
      -H 'Connection: keep-alive' \
      --compressed
```

#### Example Response:

```json
{
  "message": "pong from server A !!!"
}
```

### 2. Add Data to Cache

**Endpoint**: `/api/cache/{key}/value/{value}`

**Method**: `PUT`

**Description**: Adds a key-value pair to the distributed cache.

- **{key}**: The key to store in the cache.
- **{value}**: The value to associate with the given key.

#### Example Request:

```bash
curl -X PUT 'http://localhost:8081/api/cache/abc/value/123' \
      -H 'Accept: application/json' \
      -H 'Connection: keep-alive' \
      --compressed
```

#### Example Response:

```json
{
  "message": "Value 123 store for key abc"
}
```

If the key already exists, it will return a message stating that the value is already present.

#### Example Response for existing key:

```json
{
  "message": "Value 123 already exist for key abc"
}
```

### 3. Get Data from Cache

**Endpoint**: `/api/cache/{key}`

**Method**: `GET`

**Description**: Retrieves the value associated with the given key from the distributed cache by sending request to different server.

- **{key}**: The key to retrieve from the cache.

#### Example Request:

```bash
curl -X GET 'http://localhost:8081/api/cache/abc' \
      -H 'Accept: application/json' \
      -H 'Connection: keep-alive' \
      --compressed
```

#### Example Response:

```json
{
  "message": "Value 123 store for key abc"
}
```

If no value exists for the given key, the response will indicate that no value was found.

#### Example Response when key is not found:

```json
{
  "message": "No value found for key abc"
}
```
