package com.megatrex4.mixin;

import com.megatrex4.ConfigManager;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    // Prevent breaking blocks
    @Inject(method = "attackBlock", at = @At("HEAD"), cancellable = true)
    private void preventBreaking(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        ClientPlayerEntity player = net.minecraft.client.MinecraftClient.getInstance().player;
        if (player == null) return;

        ItemStack heldItem = player.getMainHandStack();

        if (ConfigManager.isEnabled() && isNearlyBrokenTool(heldItem)) {
            cir.setReturnValue(ActionResult.FAIL.isAccepted());
            player.sendMessage(
                    net.minecraft.text.Text.translatable("message.safe_tools.weapon_broken")
                            .formatted(Formatting.RED),
                    true
            );
        }
    }

    // Prevent attacking entities (mobs, players, etc.)
    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void preventAttacking(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (player == null) return;

        ItemStack heldItem = player.getMainHandStack();

        if (ConfigManager.isEnabled() && isNearlyBrokenTool(heldItem)) {
            ci.cancel();
            player.sendMessage(
                    net.minecraft.text.Text.translatable("message.safe_tools.weapon_broken")
                            .formatted(Formatting.RED),
                    true
            );
        }

    }

    private boolean isNearlyBrokenTool(ItemStack itemStack) {
        return itemStack.getItem() instanceof MiningToolItem
                || itemStack.getItem() instanceof SwordItem
                && itemStack.getMaxDamage() - itemStack.getDamage() == 1;
    }
}
