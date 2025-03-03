# Non-Blocking RestClient

![Java](https://img.shields.io/badge/Java-21+-blue) ![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)

## Overview

This library provides a non-blocking and asynchronous implementation of `RestClient`, enabling applications to perform efficient HTTP calls without blocking the main thread.

## Features
- Fully asynchronous implementation.
- Low latency and enhanced efficiency for highly concurrent applications.
- Based on the `RestClient`.

## Requirements
- **Java 21 or later**

## Installation
Add the dependency to your Maven project:
```xml
<dependency>
    <groupId>com.github.rrs671</groupId>
    <artifactId>nio-rest-client</artifactId>
    <version>2.1.1</version>
</dependency>
```

## Usage
Example usage of the library to perform a GET request:

```java

NioRestClientParams params = NioRestClientParams.builder()
        .addConnectionTimeout(3)
        .addReadTimeout(6)
        .build();

NioRestClient nioRestClient = new NioRestClient();

RestRequest rest = nioRestClient.rest(params);

RequestParams requestParams = RequestParams.builder()
        .addUrl("https://api.example.com/data")
        .addHeaders("Content-Type", "application/json")
        .build();

CompletableFuture<String> future = rest.get(requestParams, String.class);

Response<String> response = ResponseUtils.getResult(future);

if (response.isSuccess()) {
    System.out.println(response.getSucessResult());
} else {
    System.out.println(response.getErrorMessage());
    
    if (response.isHttpResponseError()) {
        System.out.println(response.getErrorStatusCode());
    }
}

```

## License
This project is licensed under the **Apache License 2.0**. See the [LICENSE](LICENSE) file for more details.

---

Feel free to contribute or report any issues! 🚀

