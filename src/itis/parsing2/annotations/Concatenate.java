package itis.parsing2.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//значение нужно соединять из значений других полей учитывая разделитель (delimiter).
//Имена полей "источников значений" передается в параметр аннотации.
// Если какое либо из полей найти не удалось, должна генерироваться соответствующая ошибка для поля, над которым стоит аннотация
//Например если @Concatenate(fieldNames = ["field1, field2"], delimiter = "**"), field1=aa, field2=bb,
//то в поле необходимо положить aa**bb
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Concatenate {

    String[] fieldNames();
    String delimiter();
}
