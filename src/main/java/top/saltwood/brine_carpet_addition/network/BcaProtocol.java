package top.saltwood.brine_carpet_addition.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import top.saltwood.brine_carpet_addition.BcaSettings;
import top.saltwood.brine_carpet_addition.Main;
import top.saltwood.brine_carpet_addition.network.packets.BcaProtocolDisablePayload;
import top.saltwood.brine_carpet_addition.network.packets.BcaProtocolEnablePayload;
import top.saltwood.brine_carpet_addition.network.packets.UpdateBlockEntityPayload;
import top.saltwood.brine_carpet_addition.network.packets.UpdateEntityPayload;

import java.util.concurrent.locks.ReentrantLock;

public class BcaProtocol {

    public static final ReentrantLock lock = new ReentrantLock(true);

    public static void enable(@NotNull ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new BcaProtocolEnablePayload());
        lock.lock();
        lock.unlock();
    }

    public static void disable(@NotNull ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new BcaProtocolDisablePayload());
    }

    public static void updateEntity(@NotNull ServerPlayerEntity player, @NotNull Entity entity) {
        ServerPlayNetworking.send(player, UpdateEntityPayload.of(player, entity));
    }

    public static void updateBlockEntity(@NotNull ServerPlayerEntity player, @NotNull BlockEntity entity) {
        if (!entity.hasWorld()) return;
        if (Main.SERVER == null) return;
        ServerPlayNetworking.send(player, UpdateBlockEntityPayload.of(player, entity, Main.SERVER.getRegistryManager()));
    }

    public static void init() {
        ServerPlayConnectionEvents.JOIN.register(BcaProtocol::onJoin);
    }

    private static void onJoin(ServerPlayNetworkHandler serverPlayNetworkHandler, PacketSender packetSender, MinecraftServer minecraftServer) {
        if (BcaSettings.bcaProtocolEnabled) enable(serverPlayNetworkHandler.player);
    }

    public static void disableBcaProtocolGlobal() {
        if (Main.SERVER == null) return;
        lock.lock();
        lock.unlock();
        for (ServerPlayerEntity player : Main.SERVER.getPlayerManager().getPlayerList()) {
            disable(player);
        }
    }

    public static void enableBcaProtocolGlobal() {
        if (Main.SERVER == null) return;
        for (ServerPlayerEntity player : Main.SERVER.getPlayerManager().getPlayerList()) {
            enable(player);
        }
    }
}