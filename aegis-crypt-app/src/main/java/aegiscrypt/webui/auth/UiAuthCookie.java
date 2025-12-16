package aegiscrypt.webui.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public final class UiAuthCookie {
    public static final String NAME = "AegisJwt";

    private UiAuthCookie() {}

    public static void set(HttpServletResponse resp, String jwt) {
        Cookie c = new Cookie(NAME, jwt);
        c.setHttpOnly(true);
        c.setPath("/");
        // для локальной разработки без https:
        c.setSecure(false);
        // 1 час
        c.setMaxAge(3600);
        resp.addCookie(c);
    }

    public static void clear(HttpServletResponse resp) {
        Cookie c = new Cookie(NAME, "");
        c.setHttpOnly(true);
        c.setPath("/");
        c.setMaxAge(0);
        resp.addCookie(c);
    }

    public static String get(HttpServletRequest req) {
        if (req.getCookies() == null) return null;
        for (Cookie c : req.getCookies()) {
            if (NAME.equals(c.getName())) return c.getValue();
        }
        return null;
    }
}
