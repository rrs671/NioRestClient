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
    <version>1.0.0</version>
</dependency>
```

## Usage
Example usage of the library to perform a GET request:

```java

HttpTimeoutParams httpTimeoutParams = HttpTimeoutParams.builder()
        .addConnectionTimeout(3)
        .addReadTimeout(6)
        .build();

NioRestClient nioRestClient = new NioRestClient();

try(RestRequest rest = nioRestClient.rest(httpTimeoutParams)) {
    RequestParams requestParams = RequestParams.builder()
        .addUrl("https://api.example.com/data")
        .addHeaders("Content-Type", "application/json")
        .build();
    
    rest.get(requestParams, String.class)
        .thenAccept(response -> System.out.println("Response: " + response))
        .exceptionally(error -> {
            System.err.println("Request error: " + error.getMessage());
            return null;
        });
}
```

## License
This project is licensed under the **Apache License 2.0**. See the [LICENSE](LICENSE) file for more details.

---

Feel free to contribute or report any issues! 🚀

