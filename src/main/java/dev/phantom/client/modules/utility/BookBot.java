package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class BookBot extends Module {
    private final ModeSetting mode = register(new ModeSetting("Mode", "Book action mode", "Write", new String[]{"Write", "Sign", "Copy"}));
    private final StringSetting text = register(new StringSetting("Text", "Text to write", "Written by Phantom Client"));
    private final IntSetting pages = register(new IntSetting("Pages", "Number of pages to write", 1, 1, 100));
    private final BooleanSetting sign = register(new BooleanSetting("Sign", "Sign the book after writing", true));
    private final StringSetting title = register(new StringSetting("Title", "Book title", "Phantom Bot"));

    public BookBot() {
        super("BookBot", "Automatically writes and signs books", Category.UTILITY);
    }
}
