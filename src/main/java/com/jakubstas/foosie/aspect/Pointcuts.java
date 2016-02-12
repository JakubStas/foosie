package com.jakubstas.foosie.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class Pointcuts {

    @Pointcut("execution(public * *(..))")
    public void anyPublicOperation() {
    }

    @Pointcut("execution(public * com.jakubstas.foosie.rest.GameController.*(..))")
    public void inGameController() {
    }

    @Pointcut("anyPublicOperation() && inGameController() && args(responseUrl,..)")
    public void inPublicMethodOfGameController(final String responseUrl) {
    }
}
