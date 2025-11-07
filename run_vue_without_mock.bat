@echo off
chcp 65001 >nul
echo ========================================
echo   Starting Vue Dev Server (Real Backend)
echo ========================================
echo.
echo Setting environment variables...
set VUE_APP_USE_MOCK=false
REM set VUE_APP_API_BASE_URL=http://10.61.133.80:8080
set VUE_APP_API_BASE_URL=http://localhost:8080
echo Mock mode: DISABLED
echo API Base URL: %VUE_APP_API_BASE_URL%
echo.
echo Switching to project directory...
cd mypixiv-vue
echo.
echo Starting development server with Real Backend API...
echo.
call npm run serve:dev