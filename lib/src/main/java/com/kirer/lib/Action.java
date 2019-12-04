package com.kirer.lib;

/**
 * @author Kirer
 * @create 2019/12/4
 * @Describe
 */
public abstract class Action {
    public void onVerified(Validator validator){}
    public abstract void onCompleted();
}