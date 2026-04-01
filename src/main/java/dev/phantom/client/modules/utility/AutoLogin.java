package dev.phantom.client.modules.utility;

import dev.phantom.client.core.module.Category;
import dev.phantom.client.core.module.Module;
import dev.phantom.client.core.module.setting.*;

public class AutoLogin extends Module {
    private final StringSetting password = register(new StringSetting("Password", "Your server password", ""));
    private final StringSetting command = register(new StringSetting("Command", "Login command", "/login"));
    private final IntSetting delay = register(new IntSetting("Delay", "Ticks to wait before sending login command", 20, 5, 100));
    private final BooleanSetting autoRegister = register(new BooleanSetting("Auto Register", "Automatically register if not registered", false));
    private final StringSetting registerCommand = register(new StringSetting("Register Command", "Register command", "/register"));

    public AutoLogin() {
        super("AutoLogin", "Automatically logs into servers with a password command", Category.UTILITY);
    }
}
