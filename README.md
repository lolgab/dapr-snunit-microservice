# Example Service using Scala Native + dapr.io

## Requirements

To run the code you need to:

1. Install NGINX Unit https://unit.nginx.org/installation
2. Install Docker https://docs.docker.com/engine/install/
3. Install dapr https://docs.dapr.io/getting-started/
4. Install Sbt (you probably already have it, if you are reading this) https://www.scala-sbt.org/download.html

## How to run

First you need to initialize the dapr dev environment:

```
dapr init
```

You have to run dapr and let it handle the unitd process:

```
dapr run --app-id app --app-port 8080 --dapr-http-port 3500 -- unitd --no-daemon --log /dev/stdout
```

Now that NGINX Unit is running behind the dapr sidecar you can run Sbt to deploy the Scala Native application:

```
sbt
> deploy
```

If everything goes right, you will see the following message in the Sbt console:
```
{
  "success": "Reconfiguration done."
}
[success] ...
```

This dummy application has a single endpoint `/foo` that can set/get a single JSON object with the following structure:
```json
{"bar": "some text"}
```

```
POST /foo
```

sets the object passed in the data

while

```
GET /foo
```

returns the previously set object.

Trying it out with `curl`:

```bash
curl -v -X POST -d '{"bar": "Some text"}' http://localhost:8080/foo
```

```bash
curl http://localhost:8080/foo
{"bar":"Some text"}
```
