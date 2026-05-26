package dev.eclipsevisuals.gui;

import dev.eclipsevisuals.config.ConfigLoader;
import dev.eclipsevisuals.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Main Eclipse Visuals GUI screen — opened with Right Shift.
 * Shows Visuals / HUD / Utilities tabs with feature toggles.
 */
public class EclipseScreen extends Screen {

    // Layout constants
    private static final int PANEL_W = 460;
    private static final int PANEL_H = 320;
    private static final int ROW_H   = 26;
    private static final int TAB_H   = 24;
    private static final int COL_W   = 210;

    // Tabs
    private static final String[] TABS = {"Visuals", "HUD", "Utilities"};
    private int activeTab = 0;

    // Scroll offset for feature list
    private int scrollOffset = 0;

    // Hover tracking
    private int hoveredToggle = -1;

    /** Simple record for a feature row */
    private record FeatureEntry(String label, String field) {}

    private static final List<FeatureEntry> VISUALS = List.of(
            new FeatureEntry("Full Bright",     "fullBright"),
            new FeatureEntry("Zoom (hold C)",   "zoom"),
            new FeatureEntry("Particles",       "particles"),
            new FeatureEntry("China Hit",       "chinaHit"),
            new FeatureEntry("Hit Color",       "hitColor"),
            new FeatureEntry("Jump Circles",    "jumpCircles"),
            new FeatureEntry("Motion Blur",     "motionBlur"),
            new FeatureEntry("Arrow Trail",     "arrowTrail"),
            new FeatureEntry("Entity Outline",  "entityOutline"),
            new FeatureEntry("Block Overlay",   "blockOverlay"),
            new FeatureEntry("Animations",      "animations"),
            new FeatureEntry("Custom Crosshair","customCrosshair")
    );

    private static final List<FeatureEntry> HUD = List.of(
            new FeatureEntry("FPS Counter",  "fpsCounter"),
            new FeatureEntry("Keystrokes",   "keystrokes"),
            new FeatureEntry("Armour HUD",   "armourHud"),
            new FeatureEntry("Watermark",    "watermark"),
            new FeatureEntry("Target HUD",   "targetHud"),
            new FeatureEntry("Scoreboard",   "scoreboard"),
            new FeatureEntry("Ping Display", "pingDisplay"),
            new FeatureEntry("CPS Counter",  "cpsCounter"),
            new FeatureEntry("Combo Counter","comboCounter"),
            new FeatureEntry("Pack Display", "packDisplay")
    );

    private static final List<FeatureEntry> UTILITIES = List.of(
            new FeatureEntry("Auto Sprint",    "autoSprint"),
            new FeatureEntry("Auto-GG",        "autoGG"),
            new FeatureEntry("Quick Chat",     "quickChat"),
            new FeatureEntry("Nick System",    "nick"),
            new FeatureEntry("Auto Fish",      "autoFish"),
            new FeatureEntry("Auto Reconnect", "autoReconnect"),
            new FeatureEntry("Free Look",      "freeLook")
    );

    // ── Constructor ────────────────────────────────────────────────────────────

    public EclipseScreen() {
        super(Text.literal("Eclipse Visuals"));
    }

    @Override
    public boolean shouldPause() {
        return false; // Don't pause the game
    }

    // ── Rendering ──────────────────────────────────────────────────────────────

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        int px = (this.width  - PANEL_W) / 2;
        int py = (this.height - PANEL_H) / 2;

        // ── Dim background ──
        ctx.fill(0, 0, this.width, this.height, 0x88000000);

        // ── Main panel ──────────────────────────────────────────────────────
        // Outer border
        ctx.fill(px - 1, py - 1, px + PANEL_W + 1, py + PANEL_H + 1, 0xFF1A1030);
        // Panel bg
        ctx.fill(px, py, px + PANEL_W, py + PANEL_H, 0xF0090616);

        // ── Header / title ──────────────────────────────────────────────────
        int accent = parseHexColor(ModConfig.INSTANCE.accentColor, 0xFF7C3AED);
        // Header bar
        ctx.fill(px, py, px + PANEL_W, py + 28, 0xFF0D0A22);
        // Title
        ctx.drawTextWithShadow(textRenderer, "ECLIPSE VISUALS", px + 10, py + 10, accent);
        // Version
        String ver = "v1.0.0";
        ctx.drawTextWithShadow(textRenderer, ver, px + PANEL_W - textRenderer.getWidth(ver) - 8, py + 10, 0xFF555577);

        // ── Tab bar ─────────────────────────────────────────────────────────
        int tabY = py + 28;
        int tabBarH = TAB_H;
        ctx.fill(px, tabY, px + PANEL_W, tabY + tabBarH, 0xFF0C0920);
        for (int i = 0; i < TABS.length; i++) {
            int tabX = px + i * (PANEL_W / TABS.length);
            int tabW = PANEL_W / TABS.length;
            boolean active = i == activeTab;
            if (active) {
                ctx.fill(tabX, tabY + tabBarH - 2, tabX + tabW, tabY + tabBarH, accent);
                ctx.fill(tabX, tabY, tabX + tabW, tabY + tabBarH - 2, 0xFF150F30);
            }
            int tabTextColor = active ? 0xFFFFFFFF : 0xFF556688;
            int tabTextX = tabX + (tabW - textRenderer.getWidth(TABS[i])) / 2;
            ctx.drawTextWithShadow(textRenderer, TABS[i], tabTextX, tabY + 8, tabTextColor);
        }

        // ── Feature list ─────────────────────────────────────────────────────
        List<FeatureEntry> features = getActiveFeatures();
        int listY = tabY + tabBarH + 4;
        int listH = PANEL_H - 28 - tabBarH - 4;

        // Clip region (Minecraft doesn't have scissor built into DrawContext easily, use fill to simulate)
        int half = features.size() / 2 + features.size() % 2;
        for (int i = 0; i < features.size(); i++) {
            int col = i < half ? 0 : 1;
            int row = i < half ? i : i - half;
            int fx = px + col * (COL_W + 20) + 10;
            int fy = listY + row * ROW_H - scrollOffset;
            if (fy < listY || fy + ROW_H > listY + listH) continue;

            FeatureEntry fe = features.get(i);
            boolean enabled = getFieldValue(fe.field());

            // Row hover
            if (mouseX >= fx && mouseX < fx + COL_W && mouseY >= fy && mouseY < fy + ROW_H - 2) {
                ctx.fill(fx, fy, fx + COL_W, fy + ROW_H - 2, 0x22FFFFFF);
            }

            // Label
            int labelColor = enabled ? 0xFFDDDDFF : 0xFF445566;
            ctx.drawTextWithShadow(textRenderer, fe.label(), fx + 4, fy + (ROW_H - 8) / 2, labelColor);

            // Toggle
            drawToggle(ctx, fx + COL_W - 36, fy + (ROW_H - 12) / 2, enabled, accent);
        }

        // Divider between columns
        ctx.fill(px + COL_W + 14, listY, px + COL_W + 15, listY + listH, 0xFF1A1840);

        // ── Bottom bar ───────────────────────────────────────────────────────
        int botY = py + PANEL_H - 22;
        ctx.fill(px, botY, px + PANEL_W, py + PANEL_H, 0xFF0C0920);
        ctx.drawTextWithShadow(textRenderer, "[Right Shift] Close  [Scroll] Scroll", px + 8, botY + 7, 0xFF334455);
        ctx.drawTextWithShadow(textRenderer, "Click a feature to toggle", px + PANEL_W - textRenderer.getWidth("Click a feature to toggle") - 8, botY + 7, 0xFF334455);

        super.render(ctx, mouseX, mouseY, delta);
    }

    private void drawToggle(DrawContext ctx, int x, int y, boolean on, int accent) {
        int trackW = 28;
        int trackH = 12;
        // Track background
        ctx.fill(x, y, x + trackW, y + trackH, on ? accent : 0xFF1A1840);
        // Knob
        int knobX = on ? x + trackW - trackH : x;
        ctx.fill(knobX + 1, y + 1, knobX + trackH - 1, y + trackH - 1, 0xFFFFFFFF);
    }

    // ── Input handling ─────────────────────────────────────────────────────────

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int px = (this.width  - PANEL_W) / 2;
        int py = (this.height - PANEL_H) / 2;

        // Tab click
        int tabY = py + 28;
        if (mouseY >= tabY && mouseY < tabY + TAB_H) {
            int tabIdx = (int)((mouseX - px) / (PANEL_W / TABS.length));
            if (tabIdx >= 0 && tabIdx < TABS.length) {
                activeTab = tabIdx;
                scrollOffset = 0;
                return true;
            }
        }

        // Feature toggle click
        List<FeatureEntry> features = getActiveFeatures();
        int listY = tabY + TAB_H + 4;
        int listH = PANEL_H - 28 - TAB_H - 4;
        int half = features.size() / 2 + features.size() % 2;

        for (int i = 0; i < features.size(); i++) {
            int col = i < half ? 0 : 1;
            int row = i < half ? i : i - half;
            int fx = px + col * (COL_W + 20) + 10;
            int fy = listY + row * ROW_H - scrollOffset;
            if (fy < listY || fy + ROW_H > listY + listH) continue;

            if (mouseX >= fx && mouseX < fx + COL_W && mouseY >= fy && mouseY < fy + ROW_H - 2) {
                toggleField(features.get(i).field());
                ConfigLoader.save();
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        List<FeatureEntry> features = getActiveFeatures();
        int rows = (features.size() + 1) / 2;
        int maxScroll = Math.max(0, rows * ROW_H - (PANEL_H - 28 - TAB_H - 30));
        scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - verticalAmount * ROW_H));
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE
                || keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT) {
            this.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private List<FeatureEntry> getActiveFeatures() {
        return switch (activeTab) {
            case 0  -> VISUALS;
            case 1  -> HUD;
            default -> UTILITIES;
        };
    }

    private boolean getFieldValue(String field) {
        try {
            return (boolean) ModConfig.class.getField(field).get(ModConfig.INSTANCE);
        } catch (Exception e) {
            return false;
        }
    }

    private void toggleField(String field) {
        try {
            var f = ModConfig.class.getField(field);
            f.set(ModConfig.INSTANCE, !(boolean) f.get(ModConfig.INSTANCE));
        } catch (Exception ignored) {}
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
