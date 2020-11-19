package top.chendaye666.Serialize.impl;
import com.alibaba.fastjson.JSON;
import top.chendaye666.Serialize.Serializer;
import top.chendaye666.Serialize.SerializerAlogrithm;

public class JSONSerializer implements Serializer {

        @Override
        public byte getSerializerAlogrithm() {
            return SerializerAlogrithm.JSON;
        }

        @Override
        public byte[] serialize(Object object) {

            return JSON.toJSONBytes(object);
        }

        @Override
        public <T> T deserialize(Class<T> clazz, byte[] bytes) {

            return JSON.parseObject(bytes, clazz);
        }
    }
