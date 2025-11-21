package com.labelai.security;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckOwnership {
    OwnershipType value() default OwnershipType.ITEM;
}
