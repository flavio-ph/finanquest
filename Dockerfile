# Estágio 1: Build da Aplicação com Maven
# Utilizamos uma imagem oficial do Maven com o JDK 21 para compilar o projeto.
# Esta imagem contém todas as ferramentas necessárias para o build.
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Define o diretório de trabalho dentro do container.
WORKDIR /app

# Copia primeiro o pom.xml para aproveitar o cache de dependências do Docker.
# Se as dependências não mudarem, o Docker não as descarregará novamente.
COPY pom.xml .

# Descarrega todas as dependências do projeto.
RUN mvn dependency:go-offline

# Copia o restante do código-fonte da aplicação.
COPY src ./src

# Executa o build do projeto, gerando o ficheiro .jar executável.
# O -DskipTests pula a execução dos testes para acelerar o build da imagem.
RUN mvn clean install -DskipTests


# Estágio 2: Execução da Aplicação
# Utilizamos uma imagem leve, contendo apenas o Java Runtime Environment (JRE),
# para executar a aplicação. Isto resulta numa imagem final muito mais pequena.
FROM eclipse-temurin:21-jre-jammy

# Define o diretório de trabalho.
WORKDIR /app

# Expõe a porta 8080, que é a porta padrão do Spring Boot.
EXPOSE 8080

# Copia o ficheiro .jar gerado no estágio de build para a imagem final.
# O caminho do .jar é encontrado na pasta 'target' do projeto Maven.
COPY --from=build /app/target/finanqust-0.0.1-SNAPSHOT.jar app.jar

# Define o comando que será executado quando o container iniciar.
# Inicia a aplicação Spring Boot.
ENTRYPOINT ["java", "-jar", "app.jar"]
