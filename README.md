# todo-backend implemention

java 17, spring-data-elasticsearch 5, spring-boot 3, gradle 7.6


https://todobackend.com/specs/index.html

api base url: https://spring-data-elasticsearch.herokuapp.com:443/todos

version catalog path: $rootDir/gradle/versions.toml

### Clear gradle build cache, Rebuild your gradle project excluding all tests

- ./gradlew clean --rerun-tasks && ./gradlew build --rerun-tasks -x test --scan

### Deploying a WAR file using the heroku CLI

- heroku war:deploy build/libs/spring-data-elasticsearch-*.war --app spring-data-elasticsearch && heroku logs --app spring-data-elasticsearch --tail






