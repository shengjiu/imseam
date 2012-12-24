package com.imseam.cdi.context;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.NormalScope;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@NormalScope(passivating = false)
@Retention(RUNTIME)
@Target({ TYPE, METHOD, FIELD})
@Inherited
public @interface IMConnectionScoped {
}