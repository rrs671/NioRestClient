# Non-Blocking RestClient

![Java](https://img.shields.io/badge/Java-21+-blue) ![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)

## Overview

This library provides a non-blocking and asynchronous implementation of `RestClient`, enabling applications to perform efficient HTTP calls without blocking the main thread.

## Features
- Fully asynchronous implementation.
- Low latency and enhanced efficiency for highly concurrent applications.
- Rest Handlers based on HTTP verbs to perform continuous request-response jobs.
- Based on the `RestClient`.

## Requirements
- **Java 21 or later**

## Installation
Add the dependency to your Maven project:
```xml
<dependency>
    <groupId>io.github.rrs671</groupId>
    <artifactId>nio-rest-client</artifactId>
    <version>2.4.1</version>
</dependency>
```

## Usage
Example usage of the library to perform a GET request:

```java

ClientParams clientParams = ClientParams.builder()
        .addConnectionTimeout(3)
        .addReadTimeout(6)
        .build();

NioRestClient nioRestClient = new NioRestClient();

RestRequest restRequest = nioRestClient.rest(clientParams);

RequestParams params = RequestParams.builder()
        .addUrl("https://api.example.com/data")
        .addHeaders("Content-Type", "application/json")
        .build();

AsyncRequest<String> request = restRequest.get(params, String.class);
Response<String> response = request.getResponse();

if (response.isSuccess() && response.getSuccessResult().isPresent()) {
    System.out.println(response.getSuccessResult().get());
} else {
    System.out.println(response.getErrorMessage());
    
    if (response.isHttpResponseError()) {
        System.out.println(response.getErrorStatusCode());
    }
}

```

Example usage of the library to create a job to get responses from a Get Request handle:

``` java

ClientParams clientParams = ClientParams.builder().build();

NioRestClient nioRestClient = new NioRestClient();

ResponseHandler<String, String> responseHandler = new ResponseHandlerImpl<>(1, 4);
GetRestHandler<String, String> getRestHandler = new GetRestHandler<>(nioRestClient, clientParams, responseHandler, String.class);

RequestParams requestParams = RequestParams.builder()
        .addUrl("https://api.example.com/data")
            .addHeaders("Content-Type", "application/json")
                .build();

getRestHandler.doGet("Request-1", requestParams);

while (true) {
    if (responseHandler.hasResponse()) {
        Optional<Tuple<String, String>> consumed = responseHandler.consume();

        Tuple<String, String> tuple = consumed.orElseThrow();
        System.out.println("Response key: " + tuple.getKey());
        System.out.println("Response status: " + tuple.getResponse().isSuccess());

        break;
    }

    try {
        Thread.sleep(1000);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}

```

## License
This project is licensed under the **Apache License 2.0**. See the [LICENSE](LICENSE) file for more details.

---

Feel free to contribute or report any issues! 🚀

