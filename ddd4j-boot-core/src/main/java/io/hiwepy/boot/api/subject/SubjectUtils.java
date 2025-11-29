package io.hiwepy.boot.api.subject;

import org.apache.commons.collections.MapUtils;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class SubjectUtils {

    public static volatile SubjectProvider subjectProvider = null;

    public static final Function<Object, Long> TO_LONG = member -> {
        if (Objects.isNull(member)) {
            return null;
        }
        return member instanceof Long ? (Long) member : new BigDecimal(member.toString()).longValue();
    };

    public static Subject getSubject() {
        Subject subject = ThreadContext.getSubject();
        if (subject == null) {
            ThreadContext.bind(subject);
        }
        return subject;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getPrincipal(Class<T> clazz) {
        Object principal = getSubject().getPrincipal();
        if (clazz.isAssignableFrom(principal.getClass())) {
            return (T) principal;
        }
        return null;
    }

    public static Object getPrincipal() {
        return getSubject().getPrincipal();
    }

    public static String getUserId() {
        if (isAuthenticated()) {
            AuthPrincipal principal = SubjectUtils.getPrincipal(AuthPrincipal.class);
            return Objects.requireNonNull(principal).getUserId();
        }
        return null;
    }

    public static Long getUserIdLong() {
        if (isAuthenticated()) {
            AuthPrincipal principal = SubjectUtils.getPrincipal(AuthPrincipal.class);
            return TO_LONG.apply(Objects.requireNonNull(principal).getUserId());
        }
        return null;
    }

    public static String getProfileString(String key) {
        return getProfileString(key, EMPTY);
    }

    public static String getProfileString(String key, String defaultValue) {
        if (isAuthenticated()) {
            AuthPrincipal principal = SubjectUtils.getPrincipal(AuthPrincipal.class);
            return MapUtils.getString(Objects.requireNonNull(principal).getProfile(), key, defaultValue);
        }
        return null;
    }


    public static Integer getProfileInteger(String key) {
        return getProfileInteger(key, null);
    }

    public static Integer getProfileInteger(String key, Integer defaultValue) {
        if (isAuthenticated()) {
            AuthPrincipal principal = SubjectUtils.getPrincipal(AuthPrincipal.class);
            return MapUtils.getInteger(Objects.requireNonNull(principal).getProfile(), key, defaultValue);
        }
        return null;
    }

    public static Long getProfileLong(String key) {
        return getProfileLong(key, null);
    }

    public static Long getProfileLong(String key, Long defaultValue) {
        if (isAuthenticated()) {
            AuthPrincipal principal = SubjectUtils.getPrincipal(AuthPrincipal.class);
            return MapUtils.getLong(Objects.requireNonNull(principal).getProfile(), key, defaultValue);
        }
        return null;
    }

    public static Double getProfileDouble(String key) {
        return getProfileDouble(key, null);
    }

    public static Double getProfileDouble(String key, Double defaultValue) {
        if (isAuthenticated()) {
            AuthPrincipal principal = SubjectUtils.getPrincipal(AuthPrincipal.class);
            return MapUtils.getDouble(Objects.requireNonNull(principal).getProfile(), key, defaultValue);
        }
        return null;
    }

    public static boolean isAuthenticated() {
        return getSubject().isAuthenticated();
    }

}
