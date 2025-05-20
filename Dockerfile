# Build Stage
FROM amazoncorretto:21-alpine as build
WORKDIR /build
COPY . .
RUN apk add maven

# Getting version info and building package
RUN mvn package -DskipTests
RUN find ./target -name "*.jar" -not -name "*sources.jar" -not -name "*javadoc.jar" -exec cp {} ./application.jar \;

# Distribution Stage
FROM amazoncorretto:21-alpine as runner
WORKDIR /app
COPY --from=build /build/application.jar ./application.jar
EXPOSE 80
CMD [ "java", "-jar", "application.jar" ]