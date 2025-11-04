# 在线博客系统部署文档

**项目名称：** 在线博客系统 (blog_system)
**版本：** 1.0
**日期：** 2025年11月4日

---

## 1. 环境要求

在部署本系统之前，请确保您的服务器或开发环境满足以下要求：

*   **操作系统：** Windows, macOS, Linux (支持Java运行环境即可)
*   **Java Development Kit (JDK)：** 1.8 或更高版本
    *   可以通过 `java -version` 命令验证。
*   **Apache Maven：** 3.6.0 或更高版本
    *   可以通过 `mvn -v` 命令验证。
*   **Apache Tomcat：** 9.x 系列
    *   确保已下载并解压，并正确配置 `JAVA_HOME` 环境变量。
*   **MySQL Server：** 5.7 或 8.0 系列
    *   确保MySQL服务器已安装并运行。
*   **Web浏览器：** 任意现代浏览器 (Chrome, Firefox, Edge, Safari等)

---

## 2. 数据库配置与初始化

### 2.1 安装MySQL Server

请确保MySQL服务器已安装并正在运行。

### 2.2 创建数据库和表

1.  打开MySQL客户端（例如 MySQL Workbench、命令行客户端或Navicat）。
2.  执行以下SQL语句创建数据库 `blog_system`：

    ```sql
    CREATE DATABASE IF NOT EXISTS blog_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    USE blog_system;
    ```
    *(注：`CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci` 推荐用于支持更广泛的字符集，如表情符号。)*

3.  执行以下SQL语句创建数据表：

    ```sql
    -- 用户表
    CREATE TABLE users (
        id INT AUTO_INCREMENT PRIMARY KEY,
        username VARCHAR(50) UNIQUE NOT NULL,
        password VARCHAR(100) NOT NULL,
        email VARCHAR(100),
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

    -- 文章表
    CREATE TABLE articles (
        id INT AUTO_INCREMENT PRIMARY KEY,
        user_id INT NOT NULL,
        title VARCHAR(255) NOT NULL,
        content LONGTEXT,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

    -- 评论表
    CREATE TABLE comments (
        id INT AUTO_INCREMENT PRIMARY KEY,
        article_id INT NOT NULL,
        user_id INT NOT NULL,
        content TEXT NOT NULL,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE,
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
    ```

4.  **插入测试数据：**

    ```sql
    INSERT INTO users (username, password, email) VALUES ('admin', '$2a$10$v0t1Q/Z0m3X4X5X6X7X8X9X0X1X2X3X4X5X6X7X8X9X0X1X2X3', 'admin@example.com'); -- 密码为 '123456' 的BCrypt哈希值
    INSERT INTO users (username, password, email) VALUES ('testuser', '$2a$10$v0t1Q/Z0m3X4X5X6X7X8X9X0X1X2X3X4X5X6X7X8X9X0X1X2X3', 'test@example.com'); -- 密码为 '123456' 的BCrypt哈希值
    
    INSERT INTO articles (user_id, title, content) VALUES (1, '我的第一篇博客', '这是我用这个博客系统发布的第一篇文章，非常激动！');
    INSERT INTO articles (user_id, title, content) VALUES (1, '关于Web开发的思考', 'Web开发是一个不断进化的领域，学习永无止境。');
    INSERT INTO articles (user_id, title, content) VALUES (2, '编程学习心得', '分享一些我最近学习编程的心得体会，希望对大家有帮助。');
    
    INSERT INTO comments (article_id, user_id, content) VALUES (1, 2, '写得真好！');
    INSERT INTO comments (article_id, user_id, content) VALUES (1, 1, '谢谢你的支持！');
    ```
    **注意：** 上述 `admin` 和 `testuser` 的密码哈希值 `$2a$10$v0t1Q/Z0m3X4X5X6X7X8X9X0X1X2X3X4X5X6X7X8X9X0X1X2X3` 是一个示例，实际部署时建议通过注册功能生成，或者使用在线工具生成 `123456` 的BCrypt哈希值替换。

### 2.3 配置数据库连接

1.  找到项目目录下的数据库配置文件：`src/main/resources/db.properties`。
2.  使用文本编辑器打开此文件，并修改其中的数据库连接信息，特别是 `db.password`。
    ```properties
    db.url=jdbc:mysql://localhost:3306/blog_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    db.username=root
    db.password=你的MySQL密码 # <-- 将此替换为你的MySQL用户密码
    ```
    *   如果MySQL服务器不在本地，请修改 `db.url` 中的 `localhost` 和端口号。
    *   `useSSL=false` 在开发环境通常可以接受，生产环境建议配置SSL。
    *   `serverTimezone=UTC` 确保时间区域设置正确。
    *   `allowPublicKeyRetrieval=true` 用于兼容MySQL 8.x 的某些认证机制，通常推荐加上。

---

## 3. 项目构建

1.  **获取项目代码：**
    将 `blog_system` 项目文件夹复制到您的开发机器上。

2.  **安装Maven依赖并打包：**
    打开命令行或终端，进入 `blog_system` 项目的根目录（即 `pom.xml` 文件所在的目录）。
    执行以下Maven命令来下载所有依赖并构建项目为WAR包：
    ```bash
    mvn clean package
    ```
    *   `clean` 命令用于清理上次构建的残留文件。
    *   `package` 命令会编译源代码、运行测试、并生成 `blog_system.war` 文件。
    *   构建成功后，您将在项目的 `target/` 目录下找到 `blog_system.war` 文件。

---

## 4. 部署到Tomcat

1.  **安装Tomcat：**
    确保您已经下载并解压了 Apache Tomcat 9.x 版本。请勿直接运行此步骤前先进行安装。

2.  **停止Tomcat (如果正在运行)：**
    在部署新的WAR包之前，建议先停止Tomcat服务器。
    进入Tomcat安装目录的 `bin` 文件夹，执行：
    *   **Windows:** `shutdown.bat`
    *   **Linux/macOS:** `./shutdown.sh`

3.  **部署WAR包：**
    将上一步生成的 `blog_system.war` 文件复制到Tomcat安装目录下的 `webapps/` 文件夹中。

4.  **启动Tomcat：**
    进入Tomcat安装目录的 `bin` 文件夹，执行：
    *   **Windows:** `startup.bat`
    *   **Linux/macOS:** `./startup.sh`
    *   (可选) 您也可以使用 `./catalina.sh run` 在前台启动Tomcat，这样可以更方便地查看启动日志和潜在错误。

---

## 5. 访问网站

1.  **打开Web浏览器。**
2.  **访问URL：**
    在浏览器地址栏中输入以下URL：
    ```
    http://localhost:8080/blog_system/
    ```
    *   **`localhost`：** 如果您在本地部署，使用 `localhost`。如果部署在远程服务器上，请替换为服务器的IP地址或域名。
    *   **`8080`：** 这是Tomcat的默认HTTP端口。如果您的Tomcat配置了不同的端口，请替换为实际端口号。
    *   **`/blog_system/`：** 这是您部署的WAR包的上下文路径。默认情况下，Tomcat会使用WAR包的文件名（不带 `.war` 后缀）作为上下文路径。如果您修改了 `finalName` 配置，请使用您配置的名称。

---