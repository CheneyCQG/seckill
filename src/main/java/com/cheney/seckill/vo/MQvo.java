package com.cheney.seckill.vo;

import com.cheney.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MQvo {
    private User user;
    private Long id;
}
