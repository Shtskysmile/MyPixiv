@echo off
REM Windows批处理脚本 - 关闭Mock模式
echo ========================================
echo   关闭Mock模式 - 使用真实后端
echo ========================================
echo.
echo 正在设置环境变量...
set VUE_APP_USE_MOCK=false
set VUE_APP_API_BASE_URL=http://10.61.133.80:8080
echo Mock模式已关闭
echo API Base URL: %VUE_APP_API_BASE_URL%
echo.
echo 启动开发服务器...
npm run serve:dev

