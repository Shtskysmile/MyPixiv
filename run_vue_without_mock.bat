@echo off
chcp 65001 >nul
echo ========================================
echo   Starting Vue Dev Server (Real Backend)
echo ========================================
echo.
echo Setting environment variables...
set VUE_APP_USE_MOCK=false
echo Mock mode: DISABLED
echo.
echo Switching to project directory...
cd mypixiv-vue
echo.
echo Starting development server with Real Backend API...
echo.
call npm run serve:dev
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
    call npm run serve:dev
)

