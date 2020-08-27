package com.waterfairy.share;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2020/8/18 17:07
 * @info:
 */
public class MyProvider {
    private static String authority;

    public static void setAuthority(String authorityTemp) {
        authority = authorityTemp;
    }

    public static String getAuthority() {
        return authority;
    }
}
