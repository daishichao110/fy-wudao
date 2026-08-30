#!/bin/bash

# 劲松金帆舞团后端服务控制脚本 (支持外部生产环境配置文件覆盖)
APP_NAME="wudao-backend"
JAR_NAME="wudao-backend-1.0.0.jar"
PID_FILE="app.pid"
LOG_FILE="app.log"
JAVA_OPTS="-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -Xms256m -Xmx512m"

# 🔍 自动检测同级目录下的外部生产环境配置文件 (application-prod.yml 或 application.yml)
CONFIG_OPTS=""
if [ -f "./application-prod.yml" ]; then
    CONFIG_OPTS="--spring.config.location=optional:classpath:/application.yml,file:./application-prod.yml"
    echo "检测到外部生产环境配置文件: ./application-prod.yml"
elif [ -f "./application.yml" ]; then
    CONFIG_OPTS="--spring.config.location=optional:classpath:/application.yml,file:./application.yml"
    echo "检测到外部配置文件: ./application.yml"
else
    echo "使用内嵌默认配置文件: classpath:/application.yml"
fi

# 获取进程 PID
get_pid() {
    if [ -f "$PID_FILE" ] && ps -p $(cat "$PID_FILE") > /dev/null 2>&1; then
        cat "$PID_FILE"
    else
        ps -ef | grep "$JAR_NAME" | grep -v grep | awk '{print $2}'
    fi
}

# 📦 源码打包
build() {
    echo "📦 开始在服务器根据源码编译打包 $APP_NAME ..."
    MVN_CMD="mvn"
    if ! command -v mvn >/dev/null 2>&1; then
        if [ -f "./mvnw" ]; then
            MVN_CMD="./mvnw"
        elif [ -f "/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn" ]; then
            MVN_CMD="/Applications/IntelliJ IDEA.app/Contents/plugins/maven/lib/maven3/bin/mvn"
        else
            echo "❌ 未检测到 mvn 命令，请确保已安装 Maven 或配置环境变量"
            return 1
        fi
    fi

    $MVN_CMD clean package -DskipTests
    if [ $? -eq 0 ]; then
        if [ -f "target/$JAR_NAME" ]; then
            cp "target/$JAR_NAME" "./$JAR_NAME"
            echo "🎉 $APP_NAME 源码打包成功！JAR 产物已生成并就绪: ./$JAR_NAME"
        else
            echo "🎉 $APP_NAME Maven 构建成功！"
        fi
    else
        echo "❌ $APP_NAME 源码打包失败，请检查编译日志"
        return 1
    fi
}

# 🚀 启动
start() {
    PID=$(get_pid)
    if [ -n "$PID" ]; then
        echo "⚠️  $APP_NAME 已经在运行中 (PID: $PID)"
    else
        if [ ! -f "$JAR_NAME" ]; then
            if [ -f "target/$JAR_NAME" ]; then
                cp "target/$JAR_NAME" "./$JAR_NAME"
            else
                echo "⚠️ 未找到 $JAR_NAME，正在自动触发源码打包..."
                build || exit 1
            fi
        fi
        nohup java $JAVA_OPTS -jar $JAR_NAME $CONFIG_OPTS > $LOG_FILE 2>&1 &
        echo $! > $PID_FILE
        echo "🚀 $APP_NAME 启动成功！(PID: $!, 日志查看: tail -f $LOG_FILE)"
    fi
}

# 🛑 停止
stop() {
    PID=$(get_pid)
    if [ -z "$PID" ]; then
        echo "ℹ️  $APP_NAME 当前未运行"
    else
        kill $PID
        rm -f $PID_FILE
        echo "🛑 $APP_NAME 已停止 (PID: $PID)"
    fi
}

# 🔄 部署 (打包 -> 停止旧服务 -> 启动新服务)
deploy() {
    build || exit 1
    stop
    sleep 1
    start
    status
}

# 📊 状态
status() {
    PID=$(get_pid)
    if [ -n "$PID" ]; then
        echo "🟢 $APP_NAME 正在运行 (PID: $PID)"
    else
        echo "🔴 $APP_NAME 当前未运行"
    fi
}

# 📝 日志
logs() {
    if [ -f "$LOG_FILE" ]; then
        tail -f -n 100 $LOG_FILE
    else
        echo "❌ 未找到日志文件: $LOG_FILE"
    fi
}

# 入口判断
case "$1" in
    start)         start ;;
    stop)          stop ;;
    restart)       stop; sleep 1; start ;;
    status)        status ;;
    logs)          logs ;;
    build|package) build ;;
    deploy)        deploy ;;
    *)             echo "用法: $0 {start|stop|restart|status|logs|build|package|deploy}" ;;
esac
