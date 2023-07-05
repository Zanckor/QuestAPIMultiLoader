package net.zanckor.questapi;

import net.fabricmc.api.ModInitializer;
import net.zanckor.questapi.common.network.NetworkHandler;

public class FabricQuestAPI implements ModInitializer {
    
    @Override
    public void onInitialize() {
        
        // This method is invoked by the Fabric mod loader when it is ready
        // to load your  You can access Fabric and Common code in this
        // project.

        // Use Fabric to bootstrap the Common 
        CommonMain.Constants.LOG.info("Hello Fabric world!");
        CommonMain.init();

        registerNetwork();
    }

    private void registerNetwork(){
        NetworkHandler.registerClientReceiverPacket();
        NetworkHandler.registerServerReceiverPacket();
    }
}