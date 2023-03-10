# Data CSV API

This project is an API that uses CSV as main storage solution. 

You may execute the project running the following commands:

```shell
mvnw package
docker build -f src/main/docker/Dockerfile -t datacsv-api:latest .
docker run -p8080:8080 datacsv-api:latest 
```

The 4 default methods are available: insert, update, select and delete.

For insertions, you may run something like:

```shell
curl -XPOST -H 'Content-Type: text/csv' http://localhost:8080/insert  -d "0,t-shirt,adidas,10"
```

To delete a line you should call the delete method passing the id as a parameter

```shell
curl -XDELETE -H 'Content-Type: text/csv' http://localhost:8080/delete/1
```

To update a line, you may call the update with the new data and the id:
```shell
curl -XUPDATE -H 'Content-Type: text/csv' http://localhost:8080/update/1  -d "1,t-shirt,adidas,10"
```

To list all the lines you can run the select method with -1:
```shell
curl -XGET -H 'Content-Type: text/csv' http://localhost:8080/select/-1 
```

or you can select a single object passing the id:

```shell
curl -XGET -H 'Content-Type: text/csv' http://localhost:8080/select/1
```



