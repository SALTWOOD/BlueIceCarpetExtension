package top.saltwood.brine_carpet_addition.client.mixin;

import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import top.saltwood.brine_carpet_addition.BcaSettings;

@Mixin(AnvilScreen.class)
public class MixinAnvilScreen {
    @ModifyConstant(
            method = "drawForeground",
            constant = @Constant(intValue = 40)
    )
    private int mixinLimitInt(int i) {
        return BcaSettings.avoidAnvilTooExpensive ? Integer.MAX_VALUE : i;
    }
}
