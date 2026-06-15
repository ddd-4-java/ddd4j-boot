package io.ddd4j.boot.mq.core;

public interface MQEventSerialization {

    <S, T> T deserialize(S src, Class<T> dist) throws RuntimeException;

    <T> T serialize(Object src) throws RuntimeException;

}