package thefool.Annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;


@Target({TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Service {
    String value() default "";
}
