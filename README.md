## DressCodePractice（DressCode）— Android 穿搭管理应用

DressCodePractice 是一个以 **衣橱管理 + 穿搭拼图 + 灵感收藏 + 天气/AI 建议** 为核心的 Android 应用示例项目，采用 **Java + MVVM + Room** 实现本地数据管理与 UI 分层，适合作为移动端工程化与功能迭代的练习项目。

### 文档导航

- **使用指南**：`使用指南.md`
- **开发文档**：`开发文档.md`
- **功能更新说明**：`功能更新说明.md`

### 功能概览

- **账号与个人资料**
  - 启动页、注册、登录（用户名/手机号）
  - 登录态保存（SharedPreferences）
  - 个人中心展示用户信息与加入天数
  - 编辑资料（昵称/性别/头像选择框架）
  - 退出登录（清理本地状态并回到启动页）
- **身材档案（Body Profile）**
  - 身高/体重等数据录入与持久化
  - BMI 实时计算与状态展示
- **衣橱（Closet）**
  - 衣物新增/编辑/查看（图片、类别、季节等）
  - 类别与季节筛选
  - 基础数据自动种子化（类别/风格）
- **搭配（Outfit / 拼图）**
  - 从衣橱选取单品进行拼图
  - 画布上拖拽、缩放、旋转（多点触控）
  - 保存为本地拼图图片并支持再次编辑
  - 搭配列表支持按场景/季节筛选；支持删除
  - 上身照上传（以 `content uri` 形式保存）
- **灵感（Inspiration）**
  - 灵感标签创建/重命名/删除
  - 多选导入图片、长按删除
  - 基于关键词触发 AI 配图推荐入口
- **天气与 AI 穿搭建议**
  - 使用定位或城市搜索获取天气（Open‑Meteo，免 Key）
  - 基于天气信息生成简短穿搭建议（SiliconFlow 大模型）
- **AI 推荐图片**
  - 基于关键词拉取 10 张推荐图片 URL（SiliconFlow 大模型）

> 说明：本项目以“本地数据 + 交互体验”为主，社区/虚拟试衣等能力仍在持续完善中。

### 技术栈

- **语言**：Java
- **架构**：MVVM + Repository Pattern + LiveData
- **UI**：Material Components（Material 3 风格）、ViewBinding、Fragment + BottomNavigation
- **数据**：Room（SQLite）
- **网络**：OkHttp
- **图片**：Glide（部分模块）+ SAF（`ACTION_OPEN_DOCUMENT` 持久化 Uri 权限）
- **定位**：Android LocationManager + Geocoder

### 环境要求

- Android Studio（建议 2023.1+）
- JDK 8+
- Android SDK 34（compileSdk/targetSdk = 34，minSdk = 24）

### 快速开始

1. 克隆并打开项目

```bash
git clone <your-repo-url>
cd DressCodePractice
```

2. 使用 Android Studio 打开并等待 Gradle Sync 完成
3. 连接设备或启动模拟器，运行 `app`

可选：命令行构建（Windows）

```bash
.\gradlew.bat assembleDebug
```

### 配置项（SiliconFlow）

本项目通过 `BuildConfig` 注入 SiliconFlow 配置，优先级为：

1) **Gradle Property** → 2) **local.properties** → 3) **环境变量**

你需要提供 `SILICONFLOW_API_KEY`（密钥请勿写死到代码/仓库文件）。

**方式 A：local.properties（推荐）**

在项目根目录 `local.properties` 追加：

```properties
SILICONFLOW_API_KEY=你的密钥
```

**方式 B：Gradle 全局属性**

在用户目录的 `~/.gradle/gradle.properties` 添加：

```properties
SILICONFLOW_API_KEY=你的密钥
```

**方式 C：环境变量（PowerShell 示例）**

```powershell
$env:SILICONFLOW_API_KEY="你的密钥"
```

相关参数在 `app/build.gradle` 中通过 `buildConfigField` 配置（如 API Base、模型名、max_tokens、temperature）。

### 权限说明

应用会在运行时按需申请以下权限：

- **网络**：`android.permission.INTERNET`（天气、AI 请求）
- **定位**：`android.permission.ACCESS_FINE_LOCATION` / `ACCESS_COARSE_LOCATION`（首页天气定位；拒绝后会使用缓存或默认城市）
- **图片读取**：Android 13+ 使用 `READ_MEDIA_IMAGES`，更低版本使用 `READ_EXTERNAL_STORAGE`（衣物图片/灵感图片/上身照选择）

### 数据与迁移策略

- 数据存储在本地 Room 数据库 `dresscode_database`
- 当前启用了 `fallbackToDestructiveMigration()`：数据库版本升级会 **清空并重建** 数据库（适合开发迭代，不适合生产）
- 首次启动会自动写入基础种子数据（类别/风格），用于衣物录入与筛选

### 目录结构（简化）

```
app/src/main/java/com/dresscode
├── ai/                 # SiliconFlow 客户端与穿搭建议
├── data/
│   ├── dao/            # Room DAO
│   ├── database/       # AppDatabase
│   ├── entity/         # Room Entity
│   ├── model/          # 业务模型（如 WeatherInfo）
│   └── repository/     # Repository
├── viewmodel/          # ViewModel
├── fragments/          # 首页/衣橱/灵感/搭配等 Fragment
├── adapter/            # RecyclerView Adapter
└── utils/              # 图片、Bitmap、触控变换等工具
```

### 架构说明（MVVM）

- **View（Activity/Fragment）**：只负责 UI 渲染与用户交互
- **ViewModel**：维护 UI 状态（LiveData），封装异步调用
- **Repository**：聚合数据来源（Room/网络），提供业务接口
- **Room（DAO/Entity）**：本地持久化

### 常见问题（FAQ）

- **AI 功能提示 `SILICONFLOW_API_KEY 未配置`**
  - 请按“配置项（SiliconFlow）”设置密钥，并重新 Sync/Run
- **首次升级版本后数据丢失**
  - 当前迁移策略为 destructive migration（开发期策略），升级会清库
- **图片选择后重启/切页图片丢失**
  - 项目使用 SAF 并尝试持久化 Uri 权限；若设备/系统限制导致失败，可在设置中重新授权或重新选择图片
- **首页天气不准确/定位失败**
  - 可点击天气卡片切换城市；拒绝定位权限时会使用缓存或默认城市

### Roadmap

- 虚拟试衣页面完善（人体/服饰叠加、更多交互）
- 社区内容与分享流程完善
- 密码加密与更完善的鉴权机制
- 数据备份/导出与更安全的迁移方案

### 许可证

本项目用于学习与研究目的；如需用于商业场景，请补充并明确许可证及第三方资源授权。


