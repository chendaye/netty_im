package top.chendaye666.Serialize.test;

import lombok.Data;

/**
 * 以上是通信过程中 Java 对象的抽象类，可以看到，我们定义了一个版本号（默认值为 1 ）以及一个获取指令的抽象方法，
 * 所有的指令数据包都必须实现这个方法，这样我们就可以知道某种指令的含义。
 *
 * @Data 注解由 lombok 提供，它会自动帮我们生产 getter/setter 方法，减少大量重复代码，推荐使用。
 */
@Data
public abstract class Packet {

    // 协议版本
    private Byte version = 1;

    // 指令
    public abstract Byte getCommand();

    public abstract int getVersion();
}
