# Stage de build para compilar a aplicação Java
FROM openjdk:17.0.1-jdk-slim AS java17-to-build
LABEL authors="fryhh"

# Instalar dependências de build
RUN apt-get update && apt-get install -y maven

# Copiar a aplicação para dentro do container
WORKDIR /app/
ADD . .

# Limpar pasta de build antiga e compilar o projeto
RUN rm -fr target
RUN mvn package -DskipTests

#### FIM DO BUILD #####

# Stage final para rodar a aplicação
FROM openjdk:17.0.1-jdk-slim

# Configuração de fuso horário
USER root
RUN ln -fs /usr/share/zoneinfo/America/Fortaleza /etc/localtime && ls -l /etc/localtime &&\
    echo "America/Fortaleza" > /etc/timezone && cat /etc/timezone

# Instalar dependências do Chromium e ChromeDriver
RUN apt-get update && apt-get install -y \
    chromium \
    chromium-driver \
    gnupg2 \
    wget \
    less \
    && rm -rf /var/lib/apt/lists/*

RUN apt-get update

RUN mkdir -p /home/chrome-profile && chmod 777 /home/chrome-profile

# Definir variáveis de ambiente
ENV CHROME_BIN="/usr/bin/chromium"

# Expor a porta padrão do Spring Boot
EXPOSE 8080

# Copiar o JAR gerado para o container
WORKDIR /app/
COPY --from=java17-to-build /app/target/*.jar .

# Comando para rodar o JAR com configurações do Spring
CMD java -Xmx2048m -jar $(ls *.jar | head -n 1) --spring.jpa.show-sql=false --spring.jpa.hibernate.ddl-auto=none
