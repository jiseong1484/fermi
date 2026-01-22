# 1단계: Gradle로 애플리케이션 빌드
FROM gradle:8.8.0-jdk17 AS build
WORKDIR /home/gradle/src

# Gradle 래퍼 및 빌드 스크립트 복사
COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

# 의존성 미리 다운로드하여 Docker 레이어 캐시 활용
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사 후 테스트를 제외하고 빌드
COPY src ./src
RUN ./gradlew build --no-daemon -x test

# 2단계: 경량 런타임 이미지 생성
FROM eclipse-temurin:17-jre
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일만 복사
COPY --from=build /home/gradle/src/build/libs/demo-0.0.1-SNAPSHOT.jar app.jar

# 애플리케이션 포트 노출
EXPOSE 8080

# 컨테이너 시작 시 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
