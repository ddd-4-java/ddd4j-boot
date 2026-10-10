# 项目级智能体约束

本文件适用于 `ddd4j-boot` 仓库及其全部子目录。

## Maven POM 内嵌 Shell 命令

- `ddd4j-boot-parent/pom.xml` 中 `exec-maven-plugin` 的 `<argument>` 会被原样传给 `sh -c`。格式化 XML 时，不得把一条 Shell
  命令的参数拆到没有续行符的下一行。
- 尤其禁止把 `--format`、`--driver-opt`、`--password-stdin` 等选项单独放在新行；`sh` 会把它们当成独立命令并报
  `command not found`。
- 修改 Maven 内嵌 Shell 前后，必须运行 `python3 -m unittest scripts/test_embedded_shell_commands.py`。
- 涉及 Docker 构建流程时，不能只用 `-Ddocker.skip=true` 的构建作为完成证据；还必须验证 `docker.skip=false`
  时对应脚本被执行，并确认日志中不存在 `command not found`。
- 格式化、重排或“仅文档/注释”提交也必须保留 Shell 命令语义。不得因为提交主题不是构建修复而跳过上述回归测试。
- `*-SNAPSHOT` 是可变坐标。重新发布父 POM 后，必须使用全新的 Maven 本地仓库验证下游实际解析到的新时间戳构件，不能只凭当前
  `~/.m2` 缓存判定修复已生效。

## Java 类型统一规范（所有分支）

- 本框架所有版本分支严禁声明 Java `record` 类型，范围包含生产源码、测试、示例、局部类型与嵌套类型。
- 值对象和 DTO 使用普通类；需要不可变语义时使用 `final` 类与 `private final` 字段，保持各 JDK 分支的类型布局一致。
- 从 record 迁移时保留构造器校验、组件访问方法、值相等性、哈希值、字符串表示、泛型、接口及序列化契约；嵌套类型显式保留 `static` 语义。
- JSON 类型显式声明属性及构造器映射；字段校验和接口文档注解按适用位置保留。禁止依赖 Record 反射或 record pattern。
- 提交前扫描当前分支所有受 Git 管理的 Java 文件并验证不存在 record 声明，执行受影响类型及 JSON/校验链路测试；报告构建和测试的实际限制。

执行门禁：`python3 scripts/check_no_records.py`；扫描器自测：`python3 -m unittest discover -s scripts -p test_check_no_records.py`。
