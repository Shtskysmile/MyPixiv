#!/bin/bash
# Linux/Mac脚本 - 启用Mock模式

echo "========================================"
echo "  启用Mock模式"
echo "========================================"
echo ""
echo "正在设置环境变量..."
export VUE_APP_USE_MOCK=true
echo "Mock模式已启用"
echo ""
echo "启动开发服务器..."
npm run serve:mock

