package top.beforedawn.service.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户类
 *
 * @author 墨羽翎玖
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id; // id
    private Long qq; // qq号
    private String password; // 密码
    private LocalDateTime lastChangePassword; // 上一次修改密码的时间
    private String nickname; // 用户自定义昵称
    private Integer useNickname; // 是否使用自定义昵称称呼
    private UserRight right; // 权限
    private Integer point; // 积分
    private Integer luck; // 运势
    private LocalDateTime lastLuck; // 上一次测运势的时间
    private LocalDateTime modified;
    private Long modifiedId;
    private LocalDateTime create;
    private Long createId;

    public User(Long qq) {
        this.qq = qq;
    }
}
