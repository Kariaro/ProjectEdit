package com.hardcoded.utils;

import java.lang.annotation.*;

/**
 * This annotation is used to tell a developer that the
 * parameter or return value will never be null
 * 
 * @author HardCoded
 */
@Documented
@Target(ElementType.METHOD)
public @interface NotNull {

}
