package com.example.example.retrofit.converter_one;

import androidx.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
// https://blog.csdn.net/China_Style/article/details/82084504
public class JsonOrXmlConverterFactory  extends Converter.Factory {

    private final Converter.Factory xmlFactory = SimpleXmlConverterFactory.create();
    private final Converter.Factory jsonFactory = GsonConverterFactory.create();

    public static JsonOrXmlConverterFactory create() {
        return new JsonOrXmlConverterFactory();
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        for (Annotation annotation : annotations) {
            if (!(annotation instanceof ResponseFormat)) {
                continue;
            }
            String value = ((ResponseFormat) annotation).value();
            if (ResponseFormat.JSON.equals(value)) {
                return jsonFactory.responseBodyConverter(type, annotations, retrofit);
            } else if (ResponseFormat.XML.equals(value)) {
                return xmlFactory.responseBodyConverter(type, annotations, retrofit);
            }
        }

        return null;
    }
}
