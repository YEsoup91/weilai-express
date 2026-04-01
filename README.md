基于前后端分离架构的物流系统，采用 Spring Boot 3.2.3 + Vue.js 技术栈。后端使用 JDK 21、MyBatis-Plus、MySQL，前端基于 Vue + Axios，通过 RESTful API 进行数据交互，CORS 配置支持跨域访问。
架构设计融合MVC 模式思想，采用经典的 Controller-Service-Mapper 三层结构：Controller 层（如 AuthController.java）负责请求处理，Service 层（如 UserServiceImpl.java）实现业务逻辑与事务管理，Mapper 层（如 UserMapper.java）操作数据库。
Model 层设计细化为 Entity 实体类（对应数据库表）、DTO 数据传输对象（接收前端参数）、VO 视图对象（封装返回数据）、Enum 枚举类（规范状态值），形成完整的数据模型体系。配合统一响应封装 Result.java、全局异常处理 GlobalExceptionHandler.java 以及 Knife4j API 文档、Hutool 工具类
