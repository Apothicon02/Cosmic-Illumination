package com.apothicon.hydrocosmical.mixins;

import com.apothicon.hydrocosmical.worldgen.generators.HydrocosmicalGenerator;
import finalforeach.cosmicreach.worldgen.ZoneGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.apothicon.hydrocosmical.Hydrocosmical.isHydrocosmicalRegistered;

@Mixin(ZoneGenerator.class)
public class ZoneGeneratorMixin {

    @Inject(method = "registerZoneGenerators", at = @At("TAIL"))
    private static void injected(CallbackInfo ci) {
        if (!isHydrocosmicalRegistered) {
            isHydrocosmicalRegistered = true;
            ZoneGenerator.registerZoneGenerator(new HydrocosmicalGenerator());
        }
    }
}
