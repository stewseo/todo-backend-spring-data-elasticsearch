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

### [deploy the local WAR file to Heroku and run it with Tomcat Webapp-Runner](https://devcenter.heroku.com/articles/war-deployment#deployment-with-the-heroku-cli)

``` 
heroku war:deploy build/libs/spring-data-elasticsearch-*.war --app spring-data-elasticsearch

``` 

### Example logs
```java
heroku logs --app spring-data-elasticsearch --tail

```

![Screenshot_20230126_060334](https://user-images.githubusercontent.com/54422342/214996250-84108ab7-0266-4f05-a715-debb580b7ebd.png)
