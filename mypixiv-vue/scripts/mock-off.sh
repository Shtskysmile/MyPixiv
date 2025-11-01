#!/bin/bash
# Linux/Mac脚本 - 关闭Mock模式

echo "========================================"
echo "  关闭Mock模式 - 使用真实后端"
echo "========================================"
echo ""
echo "正在设置环境变量..."
export VUE_APP_USE_MOCK=false
echo "Mock模式已关闭"
echo ""
echo "启动开发服务器..."
npm run serve:dev

