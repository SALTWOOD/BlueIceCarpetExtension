package top.saltwood.blue_ice_carpet_extension.util;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public record DeathInfo(long time, int exp, SimpleInventory inventory, @Nullable UUID display) {
    public static @NotNull DeathInfo fromCompound(
            @NotNull NbtCompound nbt,
            RegistryWrapper.WrapperLookup lookup
    ) {
        long time = nbt.getLong("DeathTime");
        int exp = nbt.getInt("Experience");

        SimpleInventory inventory = new SimpleInventory(41);
        NbtList nbtList = nbt.getList("Inventory", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound entry = nbtList.getCompound(i);
            int slot = entry.getInt("Slot");

            if (slot >= 0 && slot < inventory.size()) {
                if (entry.contains("Item", NbtElement.COMPOUND_TYPE)) {
                    inventory.setStack(slot, ItemStack.fromNbt(lookup, entry.getCompound("Item"))
                            .orElse(ItemStack.EMPTY));
                }
            }
        }

        UUID text = nbt.contains("DeathDisplay") ? nbt.getUuid("DeathDisplay") : null;
        return new DeathInfo(time, exp, inventory, text);
    }

    public @NotNull NbtCompound toCompound(RegistryWrapper.WrapperLookup lookup) {
        NbtCompound nbt = new NbtCompound();
        nbt.putLong("DeathTime", this.time);
        nbt.putInt("Experience", this.exp);

        NbtList nbtList = new NbtList();
        for (int i = 0; i < this.inventory.size(); ++i) {
            ItemStack itemStack = this.inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                NbtCompound entry = new NbtCompound();
                entry.putInt("Slot", i);
                entry.put("Item", itemStack.encode(lookup));
                nbtList.add(entry);
            }
        }
        nbt.put("Inventory", nbtList);

        if (this.display != null) nbt.putUuid("DeathDisplay", this.display);
        return nbt;
    }
}
