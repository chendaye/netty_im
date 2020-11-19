package top.chendaye666.Serialize.test;

import lombok.Data;

import static top.chendaye666.Serialize.test.Command.LOGIN_REQUEST;

/**
 * 登录请求数据包继承自 Packet，然后定义了三个字段，分别是用户 ID，用户名，密码，
 * 这里最为重要的就是覆盖了父类的 getCommand() 方法，值为常量 LOGIN_RE
 */
@Data
public class LoginRequestPacket extends Packet {
    private Integer userId;

    private String username;

    private String password;

    @Override
    public Byte getCommand() {

        return LOGIN_REQUEST;
    }
}