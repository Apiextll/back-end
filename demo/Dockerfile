# ==============================================================================
# Dockerfile Multi-Stage para a API Competype (Spring Boot 4 / Java 21)
# ==============================================================================

# Etapa 1: Compilação e empacotamento com Maven Wrapper
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Copia arquivos de definição do Maven e dependências
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Torna o wrapper executável e baixa dependências em cache
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B || true

# Copia o código-fonte da aplicação
COPY src src

# Compila e gera o pacote JAR (pulando os testes no build do Docker)
RUN ./mvnw clean package -DskipTests -B

# Etapa 2: Imagem final leve para execução
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Cria usuário não-root por segurança
RUN groupadd -r competype && useradd -r -g competype competype

# Copia o artefato compilado da etapa anterior
COPY --from=builder /app/target/*.jar app.jar
RUN chown competype:competype app.jar

USER competype

# Porta padrão do Spring Boot
EXPOSE 8080

# Configurações de JVM otimizadas para contêiner
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Duser.timezone=America/Sao_Paulo"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
