package dev.eclipsevisuals.hud;

import dev.eclipsevisuals.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Renders all Eclipse Visuals HUD elements onto the screen.
 * Called from InGameHudMixin after vanilla HUD renders.
 */
public class HudRenderer {

    // Keystroke tracking
    private static int lastLMB = 0;
    private static int lastRMB = 0;

    public static void render(DrawContext ctx, MinecraftClient client, float delta) {
        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();
        var tr = client.textRenderer;
        var cfg = ModConfig.INSTANCE;

        // ── Watermark ──────────────────────────────────────────────────────
        if (cfg.watermark && cfg.watermarkText != null && !cfg.watermarkText.isEmpty()) {
            String wm = cfg.watermarkText;
            int wmColor = parseHexColor(cfg.accentColor, 0xFF7C3AED);
            // Top-right by default
            int wmX = sw - tr.getWidth(wm) - 4;
            int wmY = 4;
            if ("TOP_LEFT".equals(cfg.watermarkPos))    { wmX = 4; wmY = 4; }
            if ("BOTTOM_LEFT".equals(cfg.watermarkPos)) { wmX = 4; wmY = sh - 12; }
            if ("BOTTOM_RIGHT".equals(cfg.watermarkPos)){ wmX = sw - tr.getWidth(wm) - 4; wmY = sh - 12; }
            ctx.drawTextWithShadow(tr, wm, wmX, wmY, wmColor);
        }

        // ── FPS Counter ────────────────────────────────────────────────────
        if (cfg.fpsCounter) {
            int fps = client.getCurrentFps();
            String fpsText = fps + " FPS";
            int fpsColor = fps >= 100 ? 0xFF00FF88 : fps >= 60 ? 0xFFFFFF55 : 0xFFFF5555;
            ctx.drawTextWithShadow(tr, fpsText, 2, 2, fpsColor);
        }

        // ── CPS Counter ────────────────────────────────────────────────────
        if (cfg.cpsCounter) {
            String cpsText = "LMB: " + lastLMB + "  RMB: " + lastRMB;
            ctx.drawTextWithShadow(tr, cpsText, 2, cfg.fpsCounter ? 13 : 2, 0xFFAAAAAA);
        }

        // ── Keystrokes HUD ─────────────────────────────────────────────────
        if (cfg.keystrokes) {
            long handle = client.getWindow().getHandle();
            boolean w = InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_W);
            boolean a = InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_A);
            boolean s = InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_S);
            boolean d = InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_D);
            boolean sp = InputUtil.isKeyPressed(handle, GLFW.GLFW_KEY_SPACE);

            int kx = sw - 62;
            int ky = sh - 70;
            int keySize = 18;
            int gap = 2;
            int accent = parseHexColor(cfg.accentColor, 0xFF7C3AED);

            // W
            drawKey(ctx, tr, "W", kx + keySize + gap, ky, keySize, w, accent);
            // A S D
            drawKey(ctx, tr, "A", kx, ky + keySize + gap, keySize, a, accent);
            drawKey(ctx, tr, "S", kx + keySize + gap, ky + keySize + gap, keySize, s, accent);
            drawKey(ctx, tr, "D", kx + 2 * (keySize + gap), ky + keySize + gap, keySize, d, accent);
            // Space (wider)
            drawKey(ctx, tr, "SPC", kx, ky + 2 * (keySize + gap), keySize * 3 + gap * 2, sp, accent);
        }

        // ── Nick ───────────────────────────────────────────────────────────
        if (cfg.nick && cfg.nickName != null && !cfg.nickName.isEmpty()) {
            // Shown in tab list / name tag via other mixins if implemented
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private static void drawKey(DrawContext ctx, net.minecraft.client.font.TextRenderer tr,
                                 String label, int x, int y, int w, boolean pressed, int accent) {
        int h = 18;
        int bg = pressed ? accent : 0xAA111124;
        int border = pressed ? accent : 0x66FFFFFF;
        int textColor = pressed ? 0xFFFFFFFF : 0xAA888888;

        // Background
        ctx.fill(x, y, x + w, y + h, bg);
        // Border (1px)
        ctx.fill(x, y, x + w, y + 1, border);
        ctx.fill(x, y + h - 1, x + w, y + h, border);
        ctx.fill(x, y, x + 1, y + h, border);
        ctx.fill(x + w - 1, y, x + w, y + h, border);
        // Label
        int lx = x + (w - tr.getWidth(label)) / 2;
        int ly = y + (h - 8) / 2;
        ctx.drawText(tr, label, lx, ly, textColor, false);
    }

    private static int parseHexColor(String hex, int fallback) {
        if (hex == null) return fallback;
        try {
            String clean = hex.startsWith("#") ? hex.substring(1) : hex;
            return (int) (0xFF000000L | Long.parseLong(clean, 16));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
