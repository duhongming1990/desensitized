# desensitized —— 基于自定义注解实现日志字段脱敏



### 项目说明

字段脱敏目前项目核心代码只有4个包：annotation、enums、type、utils

生成PNG水印目前项目核心代码只有1个包：waterprint

```
com.danny.log

             .desensitized
                          .annotation
                                .Desensitized
                          .enums
                                .SensitiveTypeEnum
                                .RoleTypeEnum
                          .type
                                .脱敏类型处理类
                          .utils
                                .DesensitizedUtils
                                .ObjectUtils
                                
             .waterprint
                          .DrawTranslucentPngUtil
```

1、Desensitized是标记敏感信息的注解，用于标记在需要脱敏的字段上，用法建test下的com.danny.log.desensitized.entity.BaseUserInfo类

2、SensitiveTypeEnum是脱敏的字段类型枚举，包括手机号、身份证号、姓名等

3、ObjectUtils用于深克隆对象（找了好多工具都不能在不实现Serializable接口的情况下真正实现深克隆）

4、DesensitizedUtils对字段脱敏的核心逻辑

### 使用方法：

字段脱敏详见test包下的com.danny.log.desensitized.utils.DesensitizedUtilsTest类

生成PNG水印test包下的com.danny.log.waterprint.DrawTranslucentPngUtilTest类

### 博客链接：

数据脱敏——什么是数据脱敏：http://blog.csdn.net/huyuyang6688/article/details/77689459

数据脱敏——基于自定义注解实现日志脱敏 http://blog.csdn.net/huyuyang6688/article/details/77759844