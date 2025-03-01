package com.megatrex4;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafeTools implements ClientModInitializer {
	public static final String MOD_ID = "safe_tools";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static KeyBinding toggleKey;

	@Override
	public void onInitializeClient() {
		LOGGER.info("[Safe Tools] Mod initialized!");

		// Register the keybind (Default: V key)
		toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.safe_tools.toggle",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_V,
				"category.safe_tools"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleKey.wasPressed()) {
				ConfigManager.toggle();
				boolean state = ConfigManager.isEnabled();
				MinecraftClient minecraftClient = MinecraftClient.getInstance();
				if (minecraftClient.player != null) {
					// Use translatable text for enabling/disabling
					minecraftClient.player.sendMessage(net.minecraft.text.Text.translatable(
							state ? "message.safe_tools.enabled" : "message.safe_tools.disabled"
					), true);
				}
			}
		});

	}
}