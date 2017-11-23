package ua.feo.md5

import java.security.MessageDigest

static String getMD5(String text) {
    MessageDigest digest = MessageDigest.getInstance("MD5")
    digest.update(text.bytes)
    return new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')
}