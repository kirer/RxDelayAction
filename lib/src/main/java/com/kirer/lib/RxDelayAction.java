package com.kirer.lib;

/**
 * @author Kirer
 * @create 2019/12/4
 * @Describe
 */
public class RxDelayAction {

    private DelayAction mDelayAction;

    public static RxDelayAction getInstance() {
        return SingletonHolder.mInstance;
    }

    private static class SingletonHolder {
        private static RxDelayAction mInstance = new RxDelayAction();
    }

    public synchronized DelayAction create() {
        if(null != mDelayAction){
            mDelayAction.dispose();
            mDelayAction = null;
        }
        mDelayAction = new DelayAction();
        return mDelayAction;
    }

    public synchronized void doContinue() {
        if(null == mDelayAction){
            return;
        }
        mDelayAction.doContinue();
    }

}
