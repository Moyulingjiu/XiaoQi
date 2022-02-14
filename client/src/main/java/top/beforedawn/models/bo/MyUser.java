package top.beforedawn.models.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyUser {
    private Long id; // id
    private Long qq; // qq号
    private LocalDateTime lastChangePassword; // 上一次修改密码的时间
    private String nickname; // 用户自定义昵称
    private Integer useNickname; // 是否使用自定义昵称称呼
    private String right; // 权限
    private Integer point; // 积分
    private Integer luck; // 运势
    private LocalDateTime lastLuck; // 上一次测运势的时间
    private LocalDateTime modified;
    private LocalDateTime create;
}
