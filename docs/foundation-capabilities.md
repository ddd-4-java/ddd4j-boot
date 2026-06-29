# 基础能力实战示例

## 分页与排序

- Service：`ddd4j-boot-core/src/main/java/io/ddd4j/boot/core/service/BaseServiceImpl.java:92-105`
- Mapper：`ddd4j-boot-core/src/main/java/io/ddd4j/boot/core/mybatis/mapper/BaseMapper.java:34-41`
- 用法：组装 `PaginationEntity` 与 `OrderItem`，调用 `getPagedList`

## 通用统计接口

- 唯一值/编码/名称/父级统计：`BaseServiceImpl.java:121-139` 对应 `BaseMapper.java:48-74`

## 幂等控制

- 注解：`ddd4j-boot-core/src/main/java/io/ddd4j/boot/core/annotation/ApiIdempotent.java`
- Key 生成逻辑：`ddd4j-boot-core/src/main/java/io/ddd4j/boot/core/utils/IdempotentUtils.java`
- 建议：对重要写操作开启幂等，设置合理过期与重试策略

## 国际化与主题

- 国际化消息：`BaseController.java:61-67`、`BaseServiceImpl.java:71-78`
- 主题与本地化解析：`DefaultWebMvcConfiguration.java:81-115`

## 统一响应与异常映射

- 响应：`ApiRestResponse.java:118-166`
- MVC 异常：`webmvc/GlobalExceptionHandler.java`
- WebFlux 异常：`webflux/handler/GlobalExceptionHandler.java`

## MDC 链路日志

- 拦截器：`DefaultWebMvcConfiguration.java:117-120`
- 建议：在关键业务入口记录 TraceId/SpanId，便于问题排查

## MyBatis JSON 类型处理

- `ddd4j-boot-core/src/main/java/io/ddd4j/boot/core/mybatis/handler/JSONObjectTypeHandler.java`

