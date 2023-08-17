package com.cheney.seckill.validation;

import com.cheney.seckill.utils.ValidatorUtil;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 自定义IsMobile注解的校验器
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {
    private boolean require;
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        require = constraintAnnotation.require();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        //判断require是否符合手机格式
        if (StringUtils.isEmpty(s)){
            if (require)
                //手机号为空。要求必填
                return false;
            else
                //手机号为空，但是我也不要求必填
                return true;
        }else {
            //1手机号不为空
            return ValidatorUtil.isMobile(s);
        }
    }
}
