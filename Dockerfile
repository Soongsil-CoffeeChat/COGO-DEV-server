# JAR 빌드
FROM gradle:8.2.1-jdk17 AS builder
WORKDIR /app

# wrapper 스크립트, 설정 복사
COPY gradlew .
RUN gradle gradle

# 실행 권한 부여
RUN chmod +x gradlew

# 의존성 캐싱 -> build 파일 복사
COPY build.gradle settings.gradle ./
RUN ./gradlew dependencies --no-daemon

# 나머지 소스 복사 및 빌드
COPY . .
RUN ./gradlew clean bootJar --no-daemon

# Runtime
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# 빌드된 JAR만 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# Spring boot 기본 8080
EXPOSE 8080

# 컨테이너 실행 시 JAR 실행
ENTRYPOINT ["java", "-jar", "app.jar"]