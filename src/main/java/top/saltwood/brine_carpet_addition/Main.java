package top.saltwood.brine_carpet_addition;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.SettingsManager;
import carpet.utils.Translations;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;
import top.saltwood.brine_carpet_addition.network.BcaProtocol;

import java.util.Map;

public class Main implements ModInitializer, CarpetExtension {
    public static final String MOD_ID = "brine_carpet_addition";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final SettingsManager bcaSettingsManager = new SettingsManager(
            FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(RuntimeException::new).getMetadata().getVersion().getFriendlyString(),
            MOD_ID,
            "BCA Addition");

    @Nullable
    public static MinecraftServer SERVER = null;

    @Override
    public void onInitialize() {
        CarpetServer.manageExtension(this);
    }


    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(BcaSettings.class);

        bcaSettingsManager.registerRuleObserver((serverCommandSource, currentRuleState, originalUserTest) -> {
            switch (currentRuleState.name()) {
                case "bcaProtocolEnabled":
                    if (currentRuleState.value() instanceof Boolean enabled) {
                        if (enabled) {
                            BcaProtocol.enableBcaProtocolGlobal();
                        } else {
                            BcaProtocol.disableBcaProtocolGlobal();
                        }
                    }
                    break;
            }
        });
    }

    @Override
    public SettingsManager extensionSettingsManager() {
        return bcaSettingsManager;
    }

    @Override
    public void onServerLoadedWorlds(MinecraftServer server) {
        BcaProtocol.init();
        Main.SERVER = server;
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        return Translations.getTranslationFromResourcePath("assets/" + MOD_ID + "/lang/%s.json".formatted(lang));
    }

    public static Identifier id(String id) {
        return Identifier.of(MOD_ID, id);
    }
}
