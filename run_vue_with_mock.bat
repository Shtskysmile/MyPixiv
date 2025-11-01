@echo off
chcp 65001 >nul
echo ========================================
echo   Starting Vue Dev Server (Mock Mode)
echo ========================================
echo.
echo Setting environment variables...
set VUE_APP_USE_MOCK=true
echo Mock mode: ENABLED
echo.
echo Switching to project directory...
cd mypixiv-vue
echo.
echo Starting development server with Mock data...
echo All API requests will be intercepted by Mock.js
echo.
call npm run serve:mock
if errorlevel 1 (
    echo.
    echo ========================================
    echo   Error: cross-env not found
    echo ========================================
    echo.
    echo Installing cross-env...
    call npm install --save-dev cross-env
    echo.
    echo Retrying...
    call npm run serve:mock
)

