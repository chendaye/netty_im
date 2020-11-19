package top.chendaye666.Serialize;

import top.chendaye666.Serialize.impl.JSONSerializer;

/**
 * Java 对象定义完成之后，接下来我们就需要定义一种规则，如何把一个 Java 对象转换成二进制数据，这个规则叫做 Java 对象的序列化。
 *
 * 序列化接口有三个方法，getSerializerAlgorithm() 获取具体的序列化算法标识，serialize() 将 Java 对象转换成字节数组，
 * deserialize() 将字节数组转换成某种类型的 Java 对象，在本小册中，我们使用最简单的 json 序列化方式，使用阿里巴巴的 fastjson 作为序列化框架
 */
public interface Serializer {
    Serializer DEFAULT = new JSONSerializer();

    /**
     * 序列化算法
     * @return
     */
    byte getSerializerAlogrithm();

    /**
     * java 对象转换成二进制
     */
    byte[] serialize(Object object);

    /**
     * 二进制转换成 java 对象
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);
}
