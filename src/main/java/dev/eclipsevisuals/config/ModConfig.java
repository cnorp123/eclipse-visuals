package dev.eclipsevisuals.config;

/**
 * All toggleable features and settings for Eclipse Visuals.
 * Serialized to JSON via Gson.
 */
public class ModConfig {

    /** Singleton instance. Loaded from disk on client init. */
    public static ModConfig INSTANCE = new ModConfig();

    // ── Visuals ───────────────────────────────────────────────────────────────
    public boolean fullBright      = false;
    public boolean zoom            = true;
    public boolean customCrosshair = false;
    public boolean particles       = true;
    public boolean chinaHit        = false;
    public boolean hitColor        = false;
    public boolean jumpCircles     = false;
    public boolean motionBlur      = false;
    public boolean arrowTrail      = false;
    public boolean entityOutline   = false;
    public boolean blockOverlay    = false;
    public boolean animations      = true;

    // ── HUD ───────────────────────────────────────────────────────────────────
    public boolean fpsCounter  = true;
    public boolean keystrokes  = true;
    public boolean armourHud   = true;
    public boolean comboCounter = false;
    public boolean cpsCounter  = false;
    public boolean watermark   = true;
    public boolean targetHud   = false;
    public boolean scoreboard  = true;
    public boolean pingDisplay = false;
    public boolean packDisplay = false;

    // ── Utilities ─────────────────────────────────────────────────────────────
    public boolean autoSprint    = false;
    public boolean autoGG        = false;
    public boolean quickChat     = false;
    public boolean nick          = false;
    public boolean autoFish      = false;
    public boolean autoReconnect = false;
    public boolean freeLook      = false;

    // ── Feature settings ──────────────────────────────────────────────────────
    public double zoomFov           = 15.0;
    public float  motionBlurStrength = 0.5f;
    public int    crosshairSize      = 5;
    public String crosshairColor     = "#FFFFFF";
    public String crosshairStyle     = "Cross";

    public String watermarkText    = "eclipse.visuals";
    public String watermarkPos     = "TOP_RIGHT";
    public int    watermarkFontSize = 12;

    public String autoGGMessage = "gg";
    public long   autoGGDelay   = 500L;

    public String quickChatZ = "Good game!";
    public String quickChatX = "wp";
    public String quickChatC = "lets go";
    public String quickChatV = "gg ez";

    public String nickName  = "";
    public String nickColor = "#FFFFFF";

    public String particleType = "Crit";
    public int    particleCount = 10;
    public float  particleSpeed = 1.0f;

    // Accent color (hex) for GUI
    public String accentColor = "#7c3aed";
}
