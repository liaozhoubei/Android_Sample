package com.example.example.retrofit.converter_two;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import retrofit2.Converter;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({METHOD})
@Retention(RUNTIME)
public @interface RequestConverter {
    Class<? extends Converter.Factory> value();
}