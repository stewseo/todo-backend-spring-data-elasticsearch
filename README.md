# https://todobackend.com/index.html

- Java 17.0.4 Adoptium Temurin 
- Spring Data Elasticsearch 5.0.1
- Spring Boot 2.7.4
- Gradle 7.6
- Heroku CLI 7.53.0

api base url -- https://spring-data-elasticsearch.herokuapp.com:443/todos
<br/>

### [Clear Caches, Build the Spring Boot Application, and Package as WAR](https://docs.gradle.org/current/userguide/war_plugin.html)
``` 
./gradlew clean --rerun-tasks && ./gradlew assemble --rerun-tasks --scan
```
<br/>

### [Deploy the local WAR file to Heroku and run it with Tomcat Webapp-Runner](https://devcenter.heroku.com/articles/war-deployment#deployment-with-the-heroku-cli)

``` 
heroku war:deploy libs/spring-data-elasticsearch-*.war --app spring-data-elasticsearch

``` 

<br/>

### [Real-time tail logs from a successful request](https://devcenter.heroku.com/articles/logging)
```java
heroku logs --app spring-data-elasticsearch --tail

```

![Screenshot_20230126_060334](https://user-images.githubusercontent.com/54422342/214996250-84108ab7-0266-4f05-a715-debb580b7ebd.png)
