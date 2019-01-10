# API Client

The API Client is a simple tool for interacting with the [OverOps public API](https://doc.overops.com/reference) in Java.

## Table of Contents

[Background](#background)  
[Getting Started](#getting-started)  
&nbsp;&nbsp;&nbsp;&nbsp; [Installing](#installing)  
&nbsp;&nbsp;&nbsp;&nbsp; [Constructors](#constructors)  
&nbsp;&nbsp;&nbsp;&nbsp; [REST Operations](#rest-operations)  
&nbsp;&nbsp;&nbsp;&nbsp; [Generics](#generics)  
[Examples](#examples)  
&nbsp;&nbsp;&nbsp;&nbsp; [Get Event Metadata](#get-event-metadata)  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; [with CURL](#with-curl)  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; [with API Client](#with-api-client)  
&nbsp;&nbsp;&nbsp;&nbsp; [Get Event Volume](#get-event-volume)  
&nbsp;&nbsp;&nbsp;&nbsp; [Transaction Graphs](#transaction-graphs)  
&nbsp;&nbsp;&nbsp;&nbsp; [List Views](#list-views)  
[UDFs](#udfs)  
&nbsp;&nbsp;&nbsp;&nbsp; [OverOps Functions UDF library](#overops-functions)  
&nbsp;&nbsp;&nbsp;&nbsp; [Custom UDFs](#custom-udfs)  

## Background

The API Client is divided into two projects: the [API Client itself](/api-client), and a [set of utility functions](/api-client-util).

The API Client provides methods for `GET`, `PUT`, `POST`, and `DELETE` REST operations, as well as plain old Java objects (POJOs) that represent request and result objects for each operation available through the [OverOps public API](https://doc.overops.com/reference).

Utility functions wrap commonly used API operation sets into a single function. For example, the [`LabelUtil.createLabelsIfNotExists`](api-client-util/src/main/java/com/takipi/api/client/util/label/LabelUtil.java#L35) method first makes an API call to list all labels for a given environment, then compares that list with a list of new labels, calling the create label API for each label that does not already exist. Several individual API calls are wrapped into a single convenience method.

The OverOps API Client and utility functions make it easy to access data, extend the functionality of OverOps, and integrate OverOps data with other platforms without having to manually make and parse HTTP requests.

## Getting Started

### Installing

The [API Client](https://mvnrepository.com/artifact/com.takipi/api-client) and [utility functions](https://mvnrepository.com/artifact/com.takipi/api-client-util) are both published to the Maven central repository. Simply add one or both to your dependencies to use them in your code.

Maven:

```xml
<dependency>
    <groupId>com.takipi</groupId>
    <artifactId>api-client</artifactId>
    <version>2.11.0</version>
</dependency>
<dependency>
    <groupId>com.takipi</groupId>
    <artifactId>api-client-util</artifactId>
    <version>2.11.0</version>
</dependency>
```

Gradle:

```gradle
dependencies {
  compile (
    "com.takipi:api-client:2.11.0",
    "com.takipi:api-client-util:2.11.0",
  )
}
```

```java
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.util.*;
```

### Constructors

In order to ensure backwards and forwards compatibility with the API, API Client constructors are purposefully not public. Instead, we use Builders. This enables the underlying implementation to be changed as needed, and additional functionality to be added in the future, without breaking code that depends on the API Client.

```java
// create a new Builder
ApiClient client = ApiClient.newBuilder()
    .setHostname("http://localhost:8080") // for SaaS, use https://api.overops.com/
    .setApiKey("xxxxx") // find API token in Account Settings
    .build();
```

### REST operations

The API Client makes create, read, update, and delete operations available for endpoints that support these operations. Refer to the [API documentation](https://doc.overops.com/reference) to see which operations are supported for each endpoint.

```java
// create
apiClient.post(request);

// read
apiClient.get(request);

// update
apiClient.put(request);

// delete
apiClient.delete(request);
```

### Generics

The API Client leverages generics for ease of use.

```java
Response<T> response = apiClient.get(ApiGetRequest<T> request);
```

For example, `LabelsRequest` yields a `LabelsResult`, while `EventsSlimVolumeRequest` yields a `EventsSlimVolumeResult`.

```java
LabelsRequest labelsRequest = LabelsRequest.newBuilder()
  .setServiceId(serviceId)
  .build();

Response<LabelsResult> labelsResult = apiClient.get(labelsRequest);


EventsSlimVolumeRequest eventsSlimRequest = EventsSlimVolumeRequest.newBuilder()
  .setFrom(from)
  .setTo(to)
  .setServiceId(serviceId)
  .setViewId(viewId)
  .setVolumeType(VolumeType.all)
  .build();

Response<EventsSlimVolumeResult> eventsSlimResult = apiClient.get(eventsSlimRequest);
```

Some API calls do not return data, and instead return in an empty response.

```java
CreateLabelRequest createRequest = CreateLabelRequest.newBuilder()
  .setServiceId(serviceId)
  .setName(name)
  .build();

Response<EmptyResult> createResult = apiClient.post(createRequest);
```

## Examples

### Get Event Metadata

*API documentation: [Fetch event data](https://doc.overops.com/reference#get_services-env-id-events-event-id)*

Here we will retrieve details about an event given an event ID. For this request, two parameters are required: environment ID and event ID. The result will contain all event metadata (see [EventResult.java](/blob/master/api-client/src/main/java/com/takipi/api/client/result/event/EventResult.java)).

For this example, we'll use both CURL and the API Client to illustrate how to translate between the two.

#### with CURL

API request:

```console
curl -H "x-api-key: xxxxx" --request GET --url https://api.overops.com/api/v1/services/Sxxxxx/events/119
```

*Remember to replace x-api-key: xxxxx with your API key, Sxxxxx with your environment ID, and 119 with your event ID.*

API response:

```json
{
    "id": "119",
    "summary": "SAXParseException in XmlParseService.fireEvent",
    "type": "Swallowed Exception",
    "first_seen": "2019-01-07T22:34:30.963Z",
    "error_location": {
        "prettified_name": "XmlParseService.fireEvent",
        "class_name": "com.overops.examples.service.XmlParseService",
        "method_name": "fireEvent",
        "method_desc": "(Z)V",
        "method_position": 63
    },
    "entry_point": {
        "prettified_name": "Controller.route",
        "class_name": "com.overops.examples.controller.Controller",
        "method_name": "route",
        "method_desc": "(JLcom/overops/examples/domain/User;)Z"
    },
    "introduced_by": "v2.1.0",
    "labels": [
        "Java-Parser.infra",
        "Inbox"
    ],
    "similar_event_ids": [
        "121",
        "123"
    ],
    "error_origin": {
        "prettified_name": "SAXParser.parse",
        "class_name": "javax.xml.parsers.SAXParser",
        "method_name": "parse",
        "method_desc": "(Ljava/io/InputStream;Lorg/xml/sax/helpers/DefaultHandler;)V"
    },
    "name": "SAXParseException",
    "message": "XML document structures must start and end within the same entity.",
    "is_rethrow": false,
    "class_group": "K225",
    "call_stack_group": "K226"
}
```

#### with API Client

```java
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.request.event.EventRequest;
import com.takipi.api.client.result.event.EventResult;
import com.takipi.api.core.url.UrlClient.Response;

public class Example {

  public static void main(String[] args) {

    // construct an event request
    EventRequest eventRequest = EventRequest.newBuilder()
      .setServiceId("Sxxxxx") // environment ID
      .setEventId("119") // event ID
      .build();

    // construct an API client
    ApiClient apiClient = ApiClient.newBuilder()
      .setHostname("https://api.overops.com")
      .setApiKey("xxxxx")
      .build();

    // GET event data
    Response<EventResult> eventResponse = apiClient.get(eventRequest);

    // check for a bad API response (HTTP status code >= 300)
    if (eventResponse.isBadResponse())
      throw new IllegalStateException("Failed getting events.");

    // EventResult is a POJO representation of the JSON object above
    EventResult eventResult = eventResponse.data;

    System.out.println("ID: " + eventResult.id);
    System.out.println("Introduced by: " + eventResult.introduced_by);
    System.out.println("Name: " + eventResult.name);
    System.out.println("Message: " + eventResult.message);
  }
}
```

Output:

```console
ID: 119
Introduced by: v2.1.0
Name: SAXParseException
Message: XML document structures must start and end within the same entity.
```

### Get Event Volume

*API documentation: [Fetch events details](https://doc.overops.com/reference#get_services-env-id-views-view-id-events)*

Here we will retrieve a list of events and event details for a given time frame and view.

For this request, four parameters are required: environment ID, view ID, from, and to. The result will contain a list of events (see [EventsVolumeResult.java](/blob/master/api-client/src/main/java/com/takipi/api/client/result/event/EventsVolumeResult.java)).

```java
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.view.SummarizedView;
import com.takipi.api.client.request.event.EventsVolumeRequest;
import com.takipi.api.client.result.event.EventsVolumeResult;
import com.takipi.api.client.util.validation.ValidationUtil.VolumeType;
import com.takipi.api.client.util.view.ViewUtil;
import com.takipi.api.core.url.UrlClient.Response;

public class Example {

  public static void main(String[] args) {

    String serviceId = "Sxxxxx";

    ApiClient apiClient = ApiClient.newBuilder().setHostname("https://api.overops.com")
        .setApiKey("xxxxx").build();

    // get "All Events" view
    SummarizedView view = ViewUtil.getServiceViewByName(apiClient, serviceId, "All Events");

    // get events that have occurred in the last 5 minutes
    DateTime to = DateTime.now();
    DateTime from = to.minusMinutes(5);

    // date parameter must be properly formatted
    DateTimeFormatter fmt = ISODateTimeFormat.dateTime().withZoneUTC();

    // get all events within the date range
    EventsVolumeRequest eventsVolumeRequest = EventsVolumeRequest.newBuilder().setServiceId(serviceId)
        .setFrom(from.toString(fmt)).setTo(to.toString(fmt)).setViewId(view.id).setVolumeType(VolumeType.all)
        .build();

    // GET event data
    Response<EventsVolumeResult> eventsVolumeResponse = apiClient.get(eventsVolumeRequest);

    // check for a bad API response (HTTP status code >= 300)
    if (eventsVolumeResponse.isBadResponse())
      throw new IllegalStateException("Failed getting events.");

    EventsVolumeResult eventsVolumeResult = eventsVolumeResponse.data;

    System.out.println("Found " + eventsVolumeResult.events.size() + " events");
    System.out.println(eventsVolumeResult.events);

  }
}
```

Output:

```console
Found 9 events
[Logged Warning in LoggedWarnService.fireEvent(114), ExampleSwallowedException in CatchAndIgnoreService.fireEvent(113), Logged Error in LoggedErrorService.fireEvent(112), ExampleCaughtException in CatchAndProcessService.fireEvent(111), Logged Error in XmlParseService.fireEvent(124), SAXParseException in XmlParseService.fireEvent(123), HTTP Error: 500 in RestEndpoint.throwError(117), Custom OverOps Event in CustomEventService.fireEvent(116), ExampleUncaughtException in UncaughtExceptionService.lambda$fireEvent$1(115)]
```

### Transaction Graphs

*API documentation: [Fetch event metrics split by entrypoint](https://doc.overops.com/reference#get_services-env-id-views-view-id-metrics-entrypoint-graph)*

Here we will retrieve detailed statistics for events in a given view and time frame.

For this request, five parameters are required: environment ID, view ID, from, to, and number of points.

Rather than using [`TransactionsGraphRequest`](/blob/master/api-client/src/main/java/com/takipi/api/client/request/transaction/TransactionsGraphRequest.java) directly, we'll use [`TransactionUtil.getTransactionGraphs`](/blob/master/api-client-util/src/main/java/com/takipi/api/client/util/transaction/TransactionUtil.java), which returns `Map<String, TransactionGraph>`. [`TransactionGraph`](/blob/master/api-client/src/main/java/com/takipi/api/client/data/transaction/TransactionGraph.java) contains a list of `GraphPoint`, each of which contains a timestamp and [`Stats`](/blob/master/api-client/src/main/java/com/takipi/api/client/data/transaction/Stats.java).

```java
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.transaction.Stats;
import com.takipi.api.client.data.transaction.TransactionGraph;
import com.takipi.api.client.data.transaction.TransactionGraph.GraphPoint;
import com.takipi.api.client.data.view.SummarizedView;
import com.takipi.api.client.util.transaction.TransactionUtil;
import com.takipi.api.client.util.view.ViewUtil;

public class Example {

  public static void main(String[] args) {

    String serviceId = "Sxxxxx";

    ApiClient apiClient = ApiClient.newBuilder().setHostname("https://api.overops.com")
        .setApiKey("xxxxx").build();

    // get "All Events" view
    SummarizedView view = ViewUtil.getServiceViewByName(apiClient, serviceId, "All Events");

    // get events that have occurred in the last 5 minutes
    DateTime to = DateTime.now();
    DateTime from = to.minusHours(5);

    // get transaction graphs with TransactionUtil
    Map<String, TransactionGraph> transactionGraphs = TransactionUtil.getTransactionGraphs(apiClient, serviceId, view.id, from, to, 10);

    Set<String> keySet = transactionGraphs.keySet();

    // for illustration, print a subset of the data
    for(String k : keySet) {
      TransactionGraph graph = transactionGraphs.get(k);
      System.out.println("class name: " + graph.class_name);
      System.out.println("method name: " + graph.method_name);
      System.out.println();

      GraphPoint point = graph.points.get(0);
      System.out.println("point 0 timestamp: " + point.time);

      Stats stats = point.stats;
      System.out.println("point 0 total time: " + stats.total_time);
      System.out.println("point 0 average time: " + stats.avg_time);
      System.out.println("point 0 average time standard deviation: " + stats.avg_time_std_deviation);
      System.out.println("point 0 invocations: " + stats.invocations);
      System.out.println();
    }

  }
}
```

Output:

```console
class name: com/overops/examples/service/UncaughtExceptionService
method name: lambda$fireEvent$1

point 0 timestamp: 2019-01-09T16:40:00.000Z
point 0 total time: 12.42
point 0 average time: 1.12
point 0 average time standard deviation: 0.0
point 0 invocations: 11

class name: com/overops/examples/controller/Controller
method name: route
point 0 timestamp: 2019-01-09T16:40:00.000Z
point 0 total time: 66202.32
point 0 average time: 141.15
point 0 avgerage time standard deviation: 0.0
point 0 invocations: 469
```

### List Views

*API documentation: [List views](https://doc.overops.com/reference#get_services-env-id-views)*

Here we will retrieve a list of all views for a given environment.

For this request, one parameter is required: environment ID. The [result](/blob/master/api-client/src/main/java/com/takipi/api/client/result/view/ViewsResult.java) will contain a list of [`SummarizedView`](/blob/master/api-client/src/main/java/com/takipi/api/client/data/view/SummarizedView.java).

```java
import com.takipi.api.client.ApiClient;
import com.takipi.api.client.data.view.SummarizedView;
import com.takipi.api.client.request.view.ViewsRequest;
import com.takipi.api.client.result.view.ViewsResult;
import com.takipi.api.core.url.UrlClient.Response;

public class Example {

  public static void main(String[] args) {

    String serviceId = "Sxxxxx";

    ApiClient apiClient = ApiClient.newBuilder().setHostname("https://api.overops.com")
        .setApiKey("xxxxx").build();

    // get all views
    ViewsRequest viewsRequest = ViewsRequest.newBuilder().setServiceId(serviceId).build();

    // GET view data
    Response<ViewsResult> viewsResponse = apiClient.get(viewsRequest);

    // check for a bad API response (HTTP status code >= 300)
    if (viewsResponse.isBadResponse())
      throw new IllegalStateException("Failed getting views.");

    ViewsResult viewsResult = viewsResponse.data;

    // print all views
    for(SummarizedView view : viewsResult.views) {
      System.out.println(view.name + "(" + view.id + ")");
    }

  }
}
```

Output:

```console
Hibernate(P7156)
Tier Routing(P7092)
Network Errors(P292)
New This Week(P286)
Tiers Routing(P7153)
Logged Warnings(P289)
Log4j(P7155)
Framework Metrics(P294)
My Timers(P5438)
New Today(P285)
Custom Event(P9075)
HTTP Errors(P6344)
DB Errors(P291)
Apache-http(P9283)
Deployment Routing(P7227)
ForceSnapshot(P9287)
EventGenerator(P9018)
Uncaught Exceptions(P290)
Last Hour(P284)
New in v2.1.3(P9291)
Logged Errors(P288)
Resolved Events(P6479)
Java-Parser(P9284)
App Routing(P7226)
All Exceptions(P6230)
Hidden Events(P6478)
Resurfaced(P6480)
Last Day(P283)
Java-lang(P7154)
All Events(P293)
JVM Errors(P287)
Swallowed Exceptions(P6139)
```

## UDFs

User Defined Functions make extensive use of the API Client and utility functions.

Within a UDF, the API Client is available from [ContextArgs](https://github.com/takipi/overops-functions/blob/master/overops-functions/src/main/java/com/takipi/udf/ContextArgs.java), which sets hostname and API key from the context.

```java
// from ContextArgs in a UDF
ApiClient apiClient = contextArgs.apiClient();
```

### OverOps Functions

Explore the [OverOps UDF library](https://github.com/takipi/overops-functions/) for more sophisticated examples on how to use the API Client. These functions are available by default for all users in OverOps.

### Custom UDFs

To write your own UDFs leveraging the API Client, fork the [User Defined Functions](https://github.com/takipi-field/udf) repository.
