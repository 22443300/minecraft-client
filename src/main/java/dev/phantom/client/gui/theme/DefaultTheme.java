package dev.phantom.client.gui.theme;

/**
 * Default implementation of {@link Theme}.
 *
 * All colour constants are inherited from the interface as {@code default int}
 * values; this class exists so callers can instantiate a concrete theme object
 * when needed, and acts as the canonical runtime theme instance.
 */
public class DefaultTheme implements Theme {

    /** Singleton instance. */
    public static final DefaultTheme INSTANCE = new DefaultTheme();

    private DefaultTheme() {}
}
