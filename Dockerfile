FROM openjdk:17.0.1-jdk-slim AS java17-to-build
LABEL authors="fryhh"

RUN apt-get update
RUN apt-get install -y maven

# Copy the app
WORKDIR /app/

ADD . .

RUN rm -fr target
RUN mvn package -DskipTests

#### FIM DO BUILD #####

FROM openjdk:17.0.1-jdk-slim

USER root
RUN ln -fs /usr/share/zoneinfo/America/Fortaleza /etc/localtime && ls -l /etc/localtime &&\
 echo "America/Fortaleza" > /etc/timezone && cat /etc/timezone

# Copy the app
WORKDIR /app/
COPY --from=java17-to-build /app/target/*.jar .

## Rodando imagem em um usuário não-root
RUN addgroup spring && adduser --disabled-password --gecos '' --ingroup spring spring
USER spring:spring

## ENV
#ENV SPRING_PROFILES_ACTIVE=prod

## Expõe porta 8080 (padrao de saida do spring)
EXPOSE 8080

## Executa o primeiro arquivo .jar que encontrar
CMD java -Xmx2048m -jar $(ls *.jar | head -n 1) --spring.jpa.show-sql=false --spring.jpa.hibernate.ddl-auto=none
