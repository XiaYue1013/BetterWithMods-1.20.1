package betterwithmods.api.capabilities;

import betterwithmods.api.tile.IAxle;
import betterwithmods.api.tile.IMechanicalPower;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;

public class CapabilityMechanical {
    public static final Capability<IMechanicalPower> MECHANICAL_POWER =
            CapabilityManager.get(new CapabilityToken<>() {});

    public static final Capability<IAxle> AXLE =
            CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IMechanicalPower.class);
        event.register(IAxle.class);
    }
}
