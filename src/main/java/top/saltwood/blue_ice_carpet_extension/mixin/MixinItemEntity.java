package top.saltwood.blue_ice_carpet_extension.mixin;

import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import top.saltwood.blue_ice_carpet_extension.ModSettings;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity {

    @ModifyConstant(
            method = "tick",
            constant = @Constant(intValue = 6000)
    )
    private int modifyDespawnLimit(int original) {
        if (ModSettings.itemDespawnAge == 0) {
            return original;
        }
        if (ModSettings.itemDespawnAge == -1) {
            return Integer.MAX_VALUE;
        }

        return ModSettings.itemDespawnAge;
    }
}

