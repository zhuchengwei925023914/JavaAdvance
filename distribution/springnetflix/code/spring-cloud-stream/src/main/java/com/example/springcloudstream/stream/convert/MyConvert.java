package com.example.springcloudstream.stream.convert;

import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.util.MimeType;

public class MyConvert extends AbstractMessageConverter {

    public MyConvert() {
        super(new MimeType("application", "user"));
    }

    @Override
    protected boolean supports(Class<?> aClass) {
        return true;
    }

    @Nullable
    @Override
    protected Object convertFromInternal(Message<?> message, Class<?> targetClass, @Nullable Object conversionHint) {
        System.out.println("转换成对象");
        return super.convertFromInternal(message, targetClass, conversionHint);
    }

    @Nullable
    @Override
    protected Object convertToInternal(Object payload, @Nullable MessageHeaders headers, @Nullable Object conversionHint) {
        System.out.println("转换成消息");
        return super.convertToInternal(payload, headers, conversionHint);
    }
}
