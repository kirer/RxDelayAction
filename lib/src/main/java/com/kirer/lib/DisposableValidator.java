package com.kirer.lib;

/**
 * @author Kirer
 * @create 2019/12/4
 * @Describe
 */
public abstract class DisposableValidator implements Validator {

    private boolean isCompleted;

    @Override
    public boolean check() {
        return isCompleted;
    }

    @Override
    public void doValid() {
        if (isCompleted) {
            return;
        }
        doDisposableValid();
    }

    public abstract void doDisposableValid();

    public void postCompleted() {
        this.isCompleted = true;
        RxDelayAction.getInstance().doContinue();
    }
}
