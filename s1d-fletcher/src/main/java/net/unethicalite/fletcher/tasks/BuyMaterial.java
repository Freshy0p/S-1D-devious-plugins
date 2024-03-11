package net.unethicalite.fletcher.tasks;

import net.unethicalite.fletcher.S1dFletcherPlugin;

public class BuyMaterial extends FletcherTask {
    public BuyMaterial(S1dFletcherPlugin context) {
        super(context);
    }

    @Override
    public boolean validate() {
        return false;
    }

    @Override
    public int execute() {
        return 0;
    }
}

