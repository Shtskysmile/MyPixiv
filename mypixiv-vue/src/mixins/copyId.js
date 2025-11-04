/**
 * 复制ID到剪贴板的 Mixin
 * 提供通用的复制功能和样式
 */
export default {
  methods: {
    /**
     * 显示轻量级提示框（Toast）
     * @param {string} message - 提示消息
     * @param {string} type - 类型：success, error, warning
     */
    showToast(message, type = 'success') {
      // 创建提示框元素
      const toast = document.createElement('div');
      toast.className = `copy-toast copy-toast-${type}`;
      
      // 根据类型设置图标
      const icons = {
        success: '✓',
        error: '✗',
        warning: '⚠'
      };
      
      toast.innerHTML = `<span style="margin-right: 8px; font-size: 16px;">${icons[type] || icons.success}</span>${message}`;
      
      // 设置样式
      Object.assign(toast.style, {
        position: 'fixed',
        top: '80px',
        left: '50%',
        transform: 'translateX(-50%) translateY(-20px)',
        background: type === 'success' ? 'rgba(0, 0, 0, 0.85)' : 
                    type === 'error' ? 'rgba(220, 38, 38, 0.9)' : 
                    'rgba(234, 179, 8, 0.9)',
        color: '#fff',
        padding: '12px 24px',
        borderRadius: '8px',
        fontSize: '14px',
        fontWeight: '500',
        boxShadow: '0 4px 16px rgba(0, 0, 0, 0.2)',
        zIndex: '99999',
        opacity: '0',
        transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
        pointerEvents: 'none',
        whiteSpace: 'nowrap',
        maxWidth: '80vw',
        display: 'flex',
        alignItems: 'center',
        backdropFilter: 'blur(8px)'
      });
      
      // 添加到页面
      document.body.appendChild(toast);
      
      // 触发进入动画
      requestAnimationFrame(() => {
        requestAnimationFrame(() => {
          toast.style.opacity = '1';
          toast.style.transform = 'translateX(-50%) translateY(0)';
        });
      });
      
      // 2秒后淡出并移除
      setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(-50%) translateY(-20px)';
        setTimeout(() => {
          if (toast.parentNode) {
            document.body.removeChild(toast);
          }
        }, 300);
      }, 2000);
    },

    /**
     * 复制ID到剪贴板
     * @param {string} id - 要复制的ID
     * @param {string} type - ID类型（用于提示消息），默认为 '用户ID'
     */
    async copyIdToClipboard(id, type = '用户ID') {
      if (!id) {
        this.showToast('ID为空，无法复制', 'warning');
        return;
      }

      try {
        // 使用现代 Clipboard API
        if (navigator.clipboard && window.isSecureContext) {
          await navigator.clipboard.writeText(id);
          this.showToast(`${type} 已复制到剪贴板`, 'success');
        } else {
          // 降级方案：使用传统的 execCommand 方法
          const textArea = document.createElement('textarea');
          textArea.value = id;
          textArea.style.position = 'fixed';
          textArea.style.left = '-999999px';
          textArea.style.top = '-999999px';
          document.body.appendChild(textArea);
          textArea.focus();
          textArea.select();
          
          try {
            const successful = document.execCommand('copy');
            if (successful) {
              this.showToast(`${type} 已复制到剪贴板`, 'success');
            } else {
              throw new Error('execCommand failed');
            }
          } catch (err) {
            console.error('复制失败:', err);
            this.showToast('复制失败，请手动复制', 'error');
          } finally {
            document.body.removeChild(textArea);
          }
        }
      } catch (error) {
        console.error('复制到剪贴板失败:', error);
        this.showToast('复制失败，请手动复制', 'error');
      }
    },

    /**
     * 格式化长ID（超过指定长度时截断）
     * @param {string} id - 要格式化的ID
     * @param {number} maxLength - 最大长度，默认12
     * @returns {string} 格式化后的ID
     */
    formatUserId(id, maxLength = 12) {
      if (!id) return '未知';
      return id.length > maxLength ? id.substring(0, maxLength) + '...' : id;
    }
  }
};
