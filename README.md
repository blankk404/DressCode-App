# DressCode - 智能穿搭应用

<div align="center">
  <h3>开启你的穿搭之旅</h3>
  <p>一款基于MVVM架构的Android穿搭应用</p>
</div>

## 📱 项目简介

DressCode是一款现代化的穿搭管理应用，帮助用户管理衣橱、获取穿搭灵感、虚拟试衣，并与社区分享自己的穿搭心得。

## ✨ 核心特性

### 已实现功能
- ✅ **精美启动页** - 渐变背景 + 品牌展示
- ✅ **用户注册** - 完整的注册流程和数据验证
- ✅ **用户登录** - 支持用户名/手机号登录
- ✅ **MVVM架构** - 清晰的代码结构，易于维护
- ✅ **本地数据库** - Room数据库实现数据持久化
- ✅ **Material Design 3** - 现代化的UI设计

### 规划中功能
- 🔄 衣橱管理 - 管理你的所有衣物
- 🔄 智能搭配 - AI推荐穿搭方案
- 🔄 虚拟试衣 - 预览穿搭效果
- 🔄 穿搭灵感 - 发现更多搭配可能
- 🔄 社区分享 - 与他人分享穿搭心得

## 🏗️ 技术架构

### 架构模式
- **MVVM** (Model-View-ViewModel)
- **Repository Pattern** (仓储模式)
- **LiveData** (响应式数据)

### 技术栈
- **语言**: Java
- **最低SDK**: Android 7.0 (API 24)
- **目标SDK**: Android 14 (API 34)
- **数据库**: Room (SQLite)
- **UI框架**: Material Design 3
- **视图绑定**: ViewBinding

## 📦 项目结构

```
DressCodePractice/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/dresscode/
│   │   │   │   ├── data/              # 数据层
│   │   │   │   │   ├── entity/        # 实体类
│   │   │   │   │   ├── dao/           # 数据访问
│   │   │   │   │   ├── database/      # 数据库
│   │   │   │   │   └── repository/    # 仓储层
│   │   │   │   ├── viewmodel/         # ViewModel
│   │   │   │   ├── fragments/         # 页面片段
│   │   │   │   ├── SplashActivity     # 启动页
│   │   │   │   ├── LoginActivity      # 登录页
│   │   │   │   ├── RegisterActivity   # 注册页
│   │   │   │   └── MainActivity       # 主页
│   │   │   └── res/                   # 资源文件
│   │   │       ├── layout/            # 布局文件
│   │   │       ├── drawable/          # 图片资源
│   │   │       └── values/            # 值资源
│   └── build.gradle                   # 构建配置
├── 开发文档.md                         # 详细开发文档
└── README.md                          # 项目说明
```

## 🚀 快速开始

### 环境要求
- Android Studio Hedgehog | 2023.1.1 或更高版本
- JDK 8 或更高版本
- Android SDK API 34

### 运行步骤

1. **克隆项目**
```bash
git clone [项目地址]
cd DressCodePractice
```

2. **打开项目**
- 使用Android Studio打开项目
- 等待Gradle同步完成

3. **运行应用**
- 连接Android设备或启动模拟器
- 点击Run按钮（或按Shift+F10）

## 📸 应用截图

### 启动页
- 渐变紫粉色背景
- DressCode品牌Logo
- 标语展示
- 登录/注册按钮

### 登录页
- 简洁的登录界面
- 支持用户名/手机号登录
- 密码可见性切换
- 忘记密码入口

### 注册页
- 完整的注册表单
- 实时输入验证
- 性别选择
- 密码确认

## 💾 数据库设计

### 用户表 (users)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | INTEGER | 主键 |
| username | TEXT | 用户名 |
| password | TEXT | 密码 |
| phone | TEXT | 手机号 |
| nickname | TEXT | 昵称 |
| gender | TEXT | 性别 |
| avatar | TEXT | 头像 |
| created_at | INTEGER | 创建时间 |

## 🔐 安全说明

**当前版本为开发版本，以下功能需要在生产环境中加强：**
- 密码需要加密存储（建议使用BCrypt或类似算法）
- 添加Token认证机制
- 实现更严格的输入验证
- 添加防暴力破解机制

## 📝 开发日志

### v0.1.0 (2025-12-19)
- ✅ 初始化项目结构
- ✅ 实现MVVM架构
- ✅ 创建用户实体和数据库
- ✅ 完成启动页UI
- ✅ 完成登录功能
- ✅ 完成注册功能
- ✅ 添加数据验证

## 🤝 贡献指南

欢迎提交Issue和Pull Request！

### 开发规范
1. 遵循MVVM架构模式
2. 使用ViewBinding进行视图绑定
3. 代码注释清晰完整
4. 遵循Material Design设计规范

## 📄 许可证

本项目仅供学习和研究使用。

## 📧 联系方式

如有问题或建议，欢迎联系开发团队。

---

**Made with ❤️ by DressCode Team**

