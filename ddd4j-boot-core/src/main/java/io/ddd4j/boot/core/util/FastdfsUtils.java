/**
 * Copyright (C) 2018 Hiwepy (http://hiwepy.io).
 * All Rights Reserved.
 */
package io.ddd4j.boot.core.util;

/**
 * FastDFS 文件存储业务工具。
 *
 * @deprecated 自 3.4.x 起，ddd4j-boot-core 重构为纯 DDD 脚手架，不含业务逻辑。
 *             本类是特定存储方案（FastDFS）的业务工具，不属于框架核心抽象。
 *             <p>
 *             <b>迁移方向</b>：移动到 {@code ddd4j-util-extra} 模块或使用方的文件服务模块。
 *             本类将在 5.0.x 版本移除。
 * @author wandl
 * @since 1.0.x
 */
@Deprecated(since = "3.4.x", forRemoval = true)
public class FastdfsUtils {

    public static String g_charset = "ISO8859-1";

    /**
     * get token for file URL
     *
     * @param file_id    the file id return by FastDFS server
     * @param ts         unix timestamp, unit: second
     * @param secret_key the secret key
     * @return token string
     */
    public static String getToken(String file_id, long ts, String secret_key) throws Exception {
        byte[] bsFileId = file_id.getBytes(g_charset);
        byte[] bsKey = secret_key.getBytes(g_charset);
        byte[] bsTimestamp = Long.valueOf(ts).toString().getBytes(g_charset);

        byte[] buff = new byte[bsFileId.length + bsKey.length + bsTimestamp.length];
        System.arraycopy(bsFileId, 0, buff, 0, bsFileId.length);
        System.arraycopy(bsKey, 0, buff, bsFileId.length, bsKey.length);
        System.arraycopy(bsTimestamp, 0, buff, bsFileId.length + bsKey.length, bsTimestamp.length);

        return md5(buff);
    }

    /**
     * md5 function
     *
     * @param source the input buffer
     * @return md5 string
     */
    public static String md5(byte[] source) throws java.security.NoSuchAlgorithmException {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        md.update(source);
        byte tmp[] = md.digest();
        char str[] = new char[32];
        int k = 0;
        for (int i = 0; i < 16; i++) {
            str[k++] = hexDigits[tmp[i] >>> 4 & 0xf];
            str[k++] = hexDigits[tmp[i] & 0xf];
        }

        return new String(str);
    }

}
