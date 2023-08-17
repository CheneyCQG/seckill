package com.cheney.seckill.vo;

import com.cheney.seckill.validation.IsMobile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginVo {

    @IsMobile(require = true)
    private String mobile;
    @NotNull
    @Length(min = 32,max = 32)
    private String password;
}
