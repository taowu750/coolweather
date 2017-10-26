package com.coolweather.android.exception;

/**
 * 当一个类不可以被创建，而调用了它的构造器去创建这个类的对象时，可以抛出此异常。
 */

public class NotCreatedObjectException extends IllegalStateException {

    public NotCreatedObjectException() {
        super("You can't create objects of this class!");
    }

    public NotCreatedObjectException(String message) {
        super(message);
    }
}
