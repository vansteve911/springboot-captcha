package com.vansteve911.spring.captcha.common;

/**
 * Created by vansteve911
 */
public interface FactoryRegistry<S, F> {

    FactoryRegistry<S, F> registerFactory(S strategy, F factory);

    F getFactory(S strategy);
}
