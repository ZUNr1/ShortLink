# 多阶段构建 - 编译阶段
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# 复制pom.xml并下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源码并打包成WAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# 运行阶段 - 使用 Tomcat 10 (支持 Jakarta EE)
FROM tomcat:10.1-jdk21-temurin
WORKDIR /app

# 创建非root用户
RUN groupadd -r tomcat && useradd -r -g tomcat tomcat

# 删除默认应用
RUN rm -rf /usr/local/tomcat/webapps/*

# 复制WAR包并重命名为ROOT.war（作为根应用）
COPY --from=build /app/target/*.war /usr/local/tomcat/webapps/ROOT.war

# 复制自定义Tomcat配置（可选）
COPY --chown=tomcat:tomcat tomcat-conf/context.xml /usr/local/tomcat/conf/context.xml 2>/dev/null || true

# 设置环境变量
ENV CATALINA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -Dfile.encoding=UTF-8"
ENV JAVA_OPTS="-Duser.timezone=Asia/Shanghai"

# 切换到非root用户
USER tomcat:tomcat

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=45s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

CMD ["catalina.sh", "run"]