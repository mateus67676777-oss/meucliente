package com.meucliente;

import com.meucliente.gui.MenuScreen;
import com.meucliente.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class MeuClienteMod implements ClientModInitializer {

    public static FabricKeyBinding menuKey;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();

        KeyBindingRegistry.INSTANCE.addCategory("MeuCliente");
        menuKey = FabricKeyBinding.Builder
                .create(new Identifier("meucliente", "menu"),
                        InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F8, "MeuCliente")
                .build();
        KeyBindingRegistry.INSTANCE.register(menuKey);

        ClientTickCallback.EVENT.register(this::onTick);
    }

    private void onTick(MinecraftClient client) {
        while (menuKey.wasPressed()) {
            if (client.currentScreen == null) {
                client.openScreen(new MenuScreen());
            }
        }
    }
}
