package net.zanckor.questapi;

import net.minecraftforge.fml.common.Mod;

@Mod(CommonMain.Constants.MOD_ID)
public class QuestAPI {
    
    public QuestAPI() {
    
        // This method is invoked by the Forge mod loader when it is ready
        // to load your  You can access Forge and Common code in this
        // project.
    
        // Use Forge to bootstrap the Common 
        CommonMain.Constants.LOG.info("Hello Forge world!");
        CommonMain.init();
        
    }
}