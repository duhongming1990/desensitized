package com.danny.log.desensitized.annotation;

import com.danny.log.desensitized.enums.RoleTypeEnum;
import com.danny.log.desensitized.enums.SensitiveTypeEnum;

import java.lang.annotation.*;

/**
 * @Author duhongming
 * @Email 19919902414@189.cn
 * @Date 2018/9/6 14:52
 */
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Desensitized {
    /*脱敏类型(规则)*/
    SensitiveTypeEnum[] type();

    /*判断注解是否生效的方法*/
    String isEffictiveMethod() default "";


    RoleTypeEnum[] role() default RoleTypeEnum.ALL_ROLES;
}