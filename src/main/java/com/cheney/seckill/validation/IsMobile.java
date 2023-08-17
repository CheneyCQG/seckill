package com.cheney.seckill.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 自定义校验：手机格式校验
 */

@Constraint(
        validatedBy = {IsMobileValidator.class}
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsMobile {
    boolean require() default true;
    //错误信息
    String message() default "手机号格式不太对！";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
