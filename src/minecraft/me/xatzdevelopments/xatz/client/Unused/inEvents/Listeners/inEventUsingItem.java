// 
// Decompiled by Procyon v0.5.36
// 

package me.xatzdevelopments.xatz.client.Unused.inEvents.Listeners;

import me.xatzdevelopments.xatz.client.Unused.inEvents.inEvent;

public class inEventUsingItem extends inEvent<inEventUsingItem>
{
    private boolean isCancelled;
    
    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }
    
    @Override
    public void setCancelled(final boolean canceled) {
        this.isCancelled = canceled;
    }
}
