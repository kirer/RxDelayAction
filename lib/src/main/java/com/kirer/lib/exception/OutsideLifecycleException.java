package com.kirer.lib.exception;

/**
 * @author Kirer
 * @create 2019/12/4
 * @Describe
 */
public class OutsideLifecycleException extends RuntimeException {
    public OutsideLifecycleException(String message) {
        super(message);
    }
}
