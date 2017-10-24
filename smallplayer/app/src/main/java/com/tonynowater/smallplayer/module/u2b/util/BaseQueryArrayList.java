package com.tonynowater.smallplayer.module.u2b.util;

import java.util.ArrayList;

/**
 * Created by tonynowater on 2017/10/24.
 */

public abstract class BaseQueryArrayList extends ArrayList {

    public BaseQueryArrayList(IOnU2BQuery callback) {
        super();
        this.callback = callback;
    }

    protected IOnU2BQuery callback;
    public abstract void query();
}
