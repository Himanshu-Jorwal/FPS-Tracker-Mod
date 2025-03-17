package net.deva.trackfps;

import org.lwjgl.glfw.GLFW;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class TrackFPS implements ClientModInitializer {
    private static boolean showFps = true;
    private static KeyBinding toggleFpsKey;

    private static final int RED = 0xFF0000;
    private static final int GREEN = 0x00FF00;
    private static final int CYAN = 0x00FFFF;

    @Override
    public void onInitializeClient() {
        toggleFpsKey = new KeyBinding(
            "key.trackfps.toggle",              // The name of the keybinding
            GLFW.GLFW_KEY_GRAVE_ACCENT,         // The keycode for the tilde (~) key
            "category.trackfps" 
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleFpsKey.isPressed()) {
                showFps = !showFps; 
            }
        });

        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            if (showFps) {
                renderFps(matrixStack);
            }
        });
    }

    private void renderFps(MatrixStack matrices) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options.debugEnabled) return; 

        String fpsText =client.fpsDebugString.split(" ")[0] + " FPS";
        int fps = Integer.parseInt(fpsText.split(" ")[0]);

        // Determine text color based on FPS range
        int textColor;
        if (fps < 60) {
            textColor = RED;
        } else if (fps <= 120) {
            textColor = GREEN;
        } else {
            textColor = CYAN; 
        }

        int x = 10;
        int y = 10;

        TextRenderer textRenderer = client.textRenderer;

        drawTextWithShadow(matrices, textRenderer, fpsText, x + 1, y + 1, 0x000000);

        textRenderer.drawWithShadow(matrices, fpsText, x, y, textColor);
    }

    private void drawTextWithShadow(MatrixStack matrices, TextRenderer textRenderer, String text, int x, int y, int color) {
        textRenderer.draw(matrices, text, x, y, color);
    }
}