package top.saltwood.brine_carpet_addition.mixin;

import net.minecraft.screen.AnvilScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import top.saltwood.brine_carpet_addition.BcaSettings;

@Mixin(AnvilScreenHandler.class)
public class MixinAnvilScreenHandler {
    @ModifyConstant(
            method = "updateResult",
            constant = @Constant(intValue = 40, ordinal = 2),
            require = 1
    )
    private int mixinLimitInt(int i) {
        return BcaSettings.avoidAnvilTooExpensive ? Integer.MAX_VALUE : 40;
    }
}