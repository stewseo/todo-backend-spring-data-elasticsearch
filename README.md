# https://todobackend.com/index.html

java 17, spring-data-elasticsearch 5, spring-boot 2, gradle 7.6

version catalog path -- $rootDir/gradle/versions.toml
<br/>

api base url -- https://spring-data-elasticsearch.herokuapp.com:443/todos
<br/>

### Build the project, package the Spring Boot application as a WAR file
``` 
./gradlew clean --rerun-tasks && ./gradlew assemble --rerun-tasks --scan
```

### Deploy the WAR file using the heroku CLI

``` 
heroku war:deploy build/libs/spring-data-elasticsearch-*.war --app spring-data-elasticsearch

``` 

### Example logs
```java
heroku logs --app spring-data-elasticsearch --tail

```
![](../../../Pictures/Screenshots/Screenshot_20230126_060334.png)