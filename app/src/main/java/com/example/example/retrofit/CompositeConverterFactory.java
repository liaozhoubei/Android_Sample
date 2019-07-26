package com.example.example.retrofit;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
// https://blog.csdn.net/cheng545/article/details/90550165
public class CompositeConverterFactory extends Converter.Factory {

    private Converter.Factory mFactory;

    public static CompositeConverterFactory create(Converter.Factory factory) {
        if (factory == null) {
            throw new NullPointerException("parameter is null");
        } else {
            return new CompositeConverterFactory(factory);
        }
    }

    private CompositeConverterFactory(Converter.Factory factory) {
        this.mFactory = factory;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        Class<?> factoryClass = null;
        for (Annotation annotation : annotations) {
            if (annotation instanceof ResponseConverter) {
                factoryClass = ((ResponseConverter) annotation).value();
                break;
            }
        }

        Converter.Factory factory = null;
        if (factoryClass != null) {
            try {
                Method createMethod = factoryClass.getMethod("create");
                factory = (Converter.Factory) createMethod.invoke(null);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        if (factory == null && mFactory != null) {
            factory = mFactory;
        }

        if (factory != null)
            return factory.responseBodyConverter(type, annotations, retrofit);

        return super.responseBodyConverter(type, annotations, retrofit);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        Class<?> factoryClass = null;
        for (Annotation paramAnno : methodAnnotations) {
            if (paramAnno instanceof RequestConverter) {
                factoryClass = ((RequestConverter) paramAnno).value();
                break;
            }
        }

        Converter.Factory factory = null;
        if (factoryClass != null) {
            try {
                Method createMethod = factoryClass.getMethod("create");
                factory = (Converter.Factory) createMethod.invoke(null);
                return factory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        if (factory == null && mFactory != null) {
            factory = mFactory;
        }

        if (factory != null)
            return factory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);

        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }
}
