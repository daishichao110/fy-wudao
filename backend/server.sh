#!/bin/bash

# 劲松金帆舞团后端服务控制脚本
APP_NAME="wudao-backend"
JAR_NAME="wudao-backend-1.0.0.jar"
PID_FILE="app.pid"
LOG_FILE="app.log"
JAVA_OPTS="-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -Xms256m -Xmx512m"

# 获取进程 PID
get_pid() {
    if [ -f "$PID_FILE" ] && ps -p $(cat "$PID_FILE") > /dev/null 2>&1; then
        cat "$PID_FILE"
    else
        ps -ef | grep "$JAR_NAME" | grep -v grep | awk '{print $2}'
    fi
}

# 🚀 启动
start() {
    PID=$(get_pid)
    if [ -n "$PID" ]; then
        echo "⚠️  $APP_NAME 已经在运行中 (PID: $PID)"
    else
        nohup java $JAVA_OPTS -jar $JAR_NAME > $LOG_FILE 2>&1 &
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
    start)   start ;;
    stop)    stop ;;
    restart) stop; sleep 1; start ;;
    status)  status ;;
    logs)    logs ;;
    *)       echo "用法: $0 {start|stop|restart|status|logs}" ;;
esac
