# WSL 开发环境与启动手册（RuoYi-Cloud）

> 账号：
>
> 登录账号：admin  admin123
>
> admin控制台：ruoyi 123456
>
> nacos：nacos nacos
>
> Sentinel：sentinel sentinel

## 1. 结论

你指定的 WSL 发行版可访问：Ubuntu-24.04-Second。
本手册基于以下工作区：
- /mnt/f/code/project_web/RuoYi-Cloud
- /mnt/f/code/project_web/RuoYi-Cloud-Vue3

## 2. 已完成的环境配置与验证

以下项目已在 WSL 中验证通过：

1. Java：17.0.18
2. Maven：3.8.7
3. Docker：27.5.1
4. Docker Compose：2.33.0
5. nvm：0.40.3
6. Node.js：20.20.2（nvm default -> 20）
7. npm：10.8.2
8. yarn：1.22.22
9. 前端依赖安装：可用 yarn 安装
10. 后端编译验证：mvn -pl ruoyi-gateway -am -DskipTests compile 为 BUILD SUCCESS

版本要求依据：
- [RuoYi-Cloud/pom.xml](RuoYi-Cloud/pom.xml)
- [RuoYi-Cloud-Vue3/package.json](RuoYi-Cloud-Vue3/package.json)
- [RuoYi-Cloud-Vue3/README.md](RuoYi-Cloud-Vue3/README.md)

关键端口与代理依据：
- 网关端口 8080：[RuoYi-Cloud/ruoyi-gateway/src/main/resources/bootstrap.yml](RuoYi-Cloud/ruoyi-gateway/src/main/resources/bootstrap.yml)
- 认证端口 9200：[RuoYi-Cloud/ruoyi-auth/src/main/resources/bootstrap.yml](RuoYi-Cloud/ruoyi-auth/src/main/resources/bootstrap.yml)
- 系统端口 9201：[RuoYi-Cloud/ruoyi-modules/ruoyi-system/src/main/resources/bootstrap.yml](RuoYi-Cloud/ruoyi-modules/ruoyi-system/src/main/resources/bootstrap.yml)
- 代码生成端口 9202：[RuoYi-Cloud/ruoyi-modules/ruoyi-gen/src/main/resources/bootstrap.yml](RuoYi-Cloud/ruoyi-modules/ruoyi-gen/src/main/resources/bootstrap.yml)
- 定时任务端口 9203：[RuoYi-Cloud/ruoyi-modules/ruoyi-job/src/main/resources/bootstrap.yml](RuoYi-Cloud/ruoyi-modules/ruoyi-job/src/main/resources/bootstrap.yml)
- 文件服务端口 9300：[RuoYi-Cloud/ruoyi-modules/ruoyi-file/src/main/resources/bootstrap.yml](RuoYi-Cloud/ruoyi-modules/ruoyi-file/src/main/resources/bootstrap.yml)
- 监控中心端口 9100：[RuoYi-Cloud/ruoyi-visual/ruoyi-monitor/src/main/resources/bootstrap.yml](RuoYi-Cloud/ruoyi-visual/ruoyi-monitor/src/main/resources/bootstrap.yml)
- 前端 dev 端口 80，代理到 8080：[RuoYi-Cloud-Vue3/vite.config.js](RuoYi-Cloud-Vue3/vite.config.js)
- 中间件与容器端口：[RuoYi-Cloud/docker/docker-compose.yml](RuoYi-Cloud/docker/docker-compose.yml)

## 3. 新 Ubuntu 系统环境配置命令（从零开始）

说明：以下命令以普通用户为例，需使用 sudo；如果你当前就是 root 用户，可去掉 sudo。

### 3.1 系统基础包

~~~bash
sudo apt update
sudo apt upgrade -y
sudo apt install -y curl git unzip zip ca-certificates gnupg lsb-release build-essential
~~~

### 3.2 Java 17 + Maven + Docker + Compose

~~~bash
sudo apt install -y openjdk-17-jdk maven docker.io docker-compose-v2

# 非 root 用户建议加入 docker 组
sudo usermod -aG docker $USER

# WSL 下通常需要手动启动 docker 服务（若失败可忽略，使用 Docker Desktop 也可）
sudo service docker start || true
~~~

### 3.3 安装 nvm + Node 20 + yarn

~~~bash
curl -fsSL https://raw.githubusercontent.com/nvm-sh/nvm/v0.40.3/install.sh | bash

export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && . "$NVM_DIR/nvm.sh"

nvm install 20
nvm alias default 20
nvm use 20

# 更新npm
npm install -g npm@latest

# 前端 README 推荐 yarn，安装 yarn classic
npm install -g yarn@1.22.22
或者
corepack prepare yarn@1.22.22 --activate
~~~

### 3.4 环境验证

~~~bash
java -version
mvn -v
docker --version
docker compose version
node -v
npm -v
yarn -v
~~~

## 4. 一次性项目初始化（按前端 README）

在 Windows PowerShell 执行：

~~~powershell
wsl.exe -d Ubuntu-24.04-Second
~~~

进入 WSL 后执行：

~~~bash
cd /mnt/f/code/project_web

# 确保 Node 20
source ~/.nvm/nvm.sh
nvm use 20

# 前端依赖安装（与 README 保持一致）
cd /mnt/f/code/project_web/RuoYi-Cloud-Vue3
yarn --registry=https://registry.npmmirror.com

# 后端编译校验（建议首次执行）
cd /mnt/f/code/project_web/RuoYi-Cloud
mvn -pl ruoyi-gateway -am -DskipTests compile
~~~

## 5. 日常启动顺序（开发模式）

建议按下面顺序启动。

### 5.1 启动基础中间件（MySQL/Redis/Nacos/Sentinel）

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud/docker
docker compose up -d ruoyi-mysql ruoyi-redis ruoyi-nacos
docker compose ps
~~~

如需同时拉起 Sentinel 控制台：

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud/docker
docker compose up -d ruoyi-mysql ruoyi-redis ruoyi-nacos ruoyi-sentinel
docker compose ps ruoyi-sentinel
~~~

说明：
- MySQL 已配置自动导入项目 [RuoYi-Cloud/sql](RuoYi-Cloud/sql) 下的 .sql 文件（挂载到 /docker-entrypoint-initdb.d）。
- 仅在 MySQL 数据卷首次初始化时自动执行。

如果你需要强制重新自动导入（会清空现有库数据）：

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud/docker
docker compose down
docker volume rm docker_ruoyi-mysql-data
docker compose up -d ruoyi-mysql
~~~

### 5.2 启动后端微服务（全量命令，Maven 方式）

每个服务建议单独开一个终端，网关最后启动。

注意：不要在根工程 `RuoYi-Cloud/pom.xml` 上直接执行 `mvn spring-boot:run`，应指定子模块 `pom.xml`。

~~~bash
mvn -f /mnt/f/code/project_web/RuoYi-Cloud/ruoyi-auth/pom.xml spring-boot:run
~~~

~~~bash
mvn -f /mnt/f/code/project_web/RuoYi-Cloud/ruoyi-modules/ruoyi-system/pom.xml spring-boot:run
~~~

~~~bash
mvn -f /mnt/f/code/project_web/RuoYi-Cloud/ruoyi-modules/ruoyi-gen/pom.xml spring-boot:run
~~~

~~~bash
mvn -f /mnt/f/code/project_web/RuoYi-Cloud/ruoyi-modules/ruoyi-job/pom.xml spring-boot:run
~~~

~~~bash
mvn -f /mnt/f/code/project_web/RuoYi-Cloud/ruoyi-modules/ruoyi-file/pom.xml spring-boot:run
~~~

~~~bash
mvn -f /mnt/f/code/project_web/RuoYi-Cloud/ruoyi-visual/ruoyi-monitor/pom.xml spring-boot:run
~~~

~~~bash
mvn -f /mnt/f/code/project_web/RuoYi-Cloud/ruoyi-gateway/pom.xml spring-boot:run
~~~

### 5.3 启动后端微服务（全量命令，Jar 方式）

对应 [RuoYi-Cloud/bin](RuoYi-Cloud/bin) 下脚本，先统一打包再逐个启动。

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud
mvn clean package -DskipTests
~~~

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud/ruoyi-auth/target
java -Dfile.encoding=utf-8 -jar ruoyi-auth.jar
~~~

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud/ruoyi-modules/ruoyi-system/target
java -Dfile.encoding=utf-8 -jar ruoyi-modules-system.jar
~~~

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud/ruoyi-modules/ruoyi-gen/target
java -Dfile.encoding=utf-8 -jar ruoyi-modules-gen.jar
~~~

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud/ruoyi-modules/ruoyi-job/target
java -Dfile.encoding=utf-8 -jar ruoyi-modules-job.jar
~~~

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud/ruoyi-modules/ruoyi-file/target
java -Dfile.encoding=utf-8 -jar ruoyi-modules-file.jar
~~~

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud/ruoyi-visual/ruoyi-monitor/target
java -Dfile.encoding=utf-8 -jar ruoyi-visual-monitor.jar
~~~

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud/ruoyi-gateway/target
java -Dfile.encoding=utf-8 -jar ruoyi-gateway.jar
~~~

### 5.4 启动前端（Vue3）

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud-Vue3
yarn dev
~~~

访问：
- 前端开发地址：http://localhost:80

## 6. 修改代码后如何重新运行

### 6.1 前端代码变更

1. 普通页面/组件修改：Vite 会热更新，通常不需要重启。
2. 若改了构建配置（如 vite.config.js）或出现异常缓存：

~~~bash
# 在前端终端里按 Ctrl+C 后重新执行
yarn dev
~~~

### 6.2 后端代码变更

1. 当前服务终端按 Ctrl+C。
2. 重新执行对应服务命令（示例网关）：

~~~bash
mvn -f /mnt/f/code/project_web/RuoYi-Cloud/ruoyi-gateway/pom.xml spring-boot:run
~~~

3. 如果改动了公共模块（ruoyi-common 或 ruoyi-api），建议重启所有受影响服务。

### 6.3 数据库或配置改动

1. Nacos 配置改动后若未自动生效，重启对应服务。
2. MySQL/Redis/Nacos 容器异常时可执行：

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud/docker
docker compose restart ruoyi-mysql ruoyi-redis ruoyi-nacos
~~~

## 7. 下次继续开发时的快速启动清单

每次开始开发建议按以下清单执行：

1. 进入 WSL：

~~~powershell
wsl.exe -d Ubuntu-24.04-Second
~~~

2. 在 WSL 校验 Node 版本（需要是 20）：

~~~bash
node -v
~~~

若不是 20：

~~~bash
source ~/.nvm/nvm.sh
nvm use 20
~~~

如果你希望直接在 PowerShell 执行一条 WSL 命令，推荐固定使用 bash -lc：

~~~powershell
C:\WINDOWS\System32\wsl.exe -d Ubuntu-24.04-Second bash -lc "source ~/.nvm/nvm.sh; nvm use 20; node -v"
~~~

注意：
- 不要用 sh -lc 执行包含 nvm 的命令，sh 环境里通常没有 source 与 nvm 函数。
- 本项目 bin 下脚本使用了 bash 语法（如 BASH_SOURCE、[[ ]] 等），应在 bash 下执行。

3. 启动中间件：

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud/docker
docker compose up -d ruoyi-mysql ruoyi-redis ruoyi-nacos
~~~

如需联调 Sentinel 菜单：

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud/docker
docker compose up -d ruoyi-sentinel
~~~

4. 启动后端服务（最少 auth/system/gateway，完整联调用 5.2 全量命令）。
5. 启动前端：

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud-Vue3
yarn dev
~~~

6. 打开 http://localhost:80 联调。

## 8. 常见问题

1. 前端提示 Node 版本不满足（例如要求 >=18）：

~~~bash
source ~/.nvm/nvm.sh
nvm use 20
node -v
~~~

2. docker 命令连接失败：
- 确认 Docker Desktop 已启动且 WSL 集成开启。

3. 端口冲突：
- 80、8080、9100、9200、9201、9202、9203、9300、3306、6379、8848 被占用时，先释放端口再启动。

4. yarn 安装慢或偶发失败：
- 重试 yarn --registry=https://registry.npmmirror.com。

5. MySQL 没有自动导入 SQL：
- 先确认 [RuoYi-Cloud/sql](RuoYi-Cloud/sql) 目录下有 .sql 文件。
- 再确认是否是第一次初始化 MySQL（命名卷已有数据时不会再次自动导入）。
- 需要重导入时按 5.1 章节的删除 MySQL 命名卷步骤执行。

6. 系统监控 -> Sentinel 打不开：
- 执行 docker compose ps ruoyi-sentinel，确认容器处于 Up。
- 访问 http://localhost:8718/#/login 验证控制台可达。

## 9. 一键封装脚本

已提供以下脚本：
- [RuoYi-Cloud/bin/start-all-dev.sh](RuoYi-Cloud/bin/start-all-dev.sh)
- [RuoYi-Cloud/bin/stop-all-dev.sh](RuoYi-Cloud/bin/stop-all-dev.sh)
- [RuoYi-Cloud/bin/status-all-dev.sh](RuoYi-Cloud/bin/status-all-dev.sh)

当前可用性说明：
- 已在 Ubuntu-24.04-Second 中实测通过执行 ./bin/status-all-dev.sh。
- 三个脚本当前均具备可执行权限，可继续使用。
- 若从 PowerShell 直接调用，建议：

~~~powershell
C:\WINDOWS\System32\wsl.exe -d Ubuntu-24.04-Second bash -lc "cd /mnt/f/code/project_web/RuoYi-Cloud && ./bin/status-all-dev.sh"
~~~

启动全部（中间件 + 后端全模块 + 前端）：

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud
./bin/start-all-dev.sh
~~~

说明：
- `start-all-dev.sh` 默认会先执行一次 Maven 预构建（install）来解决 `com.ruoyi:*` 本地依赖缺失问题。
- 如果你刚刚已经完整构建过，可加 `--skip-build` 跳过预构建以加速启动。
- `start-all-dev.sh` 默认会自动尝试修复 Nacos/Sentinel 菜单入口（可通过 `--no-menu-fix` 关闭）。
- `start-all-dev.sh` 默认会一并启动 ruoyi-sentinel 容器。

只启动后端（不拉起中间件和前端）：

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud
./bin/start-all-dev.sh --no-middlewares --no-frontend
~~~

仅跳过预构建：

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud
./bin/start-all-dev.sh --skip-build
~~~

查看当前状态：

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud
./bin/status-all-dev.sh
~~~

停止全部（默认也会停止中间件）：

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud
./bin/stop-all-dev.sh
~~~

只停应用进程（保留中间件）：

~~~bash
cd /mnt/f/code/project_web/RuoYi-Cloud
./bin/stop-all-dev.sh --no-middlewares
~~~

日志目录：
- 后端和前端日志输出到 [RuoYi-Cloud/.run-logs](RuoYi-Cloud/.run-logs)
- PID 文件保存在 [RuoYi-Cloud/.run](RuoYi-Cloud/.run)
