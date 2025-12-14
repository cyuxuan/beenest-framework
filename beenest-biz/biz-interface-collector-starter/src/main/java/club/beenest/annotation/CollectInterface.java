package club.beenest.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CollectInterface {

    /**
     * 接口描述
     */
    String description() default "";
}
