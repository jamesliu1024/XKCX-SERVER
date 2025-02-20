package seig.ljm.xkckserver.common.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
public class Hash {
    /**
     * 对密码进行哈希处理
     * @param password 密码
     * @return 哈希后的密码
     */ 
    public static String hashPassword(String password) {
        try {
            // salt
            // String salt = "xkck-ljm-seig";
            String salt = "";
            StringBuilder sb = new StringBuilder();
            sb.append(salt);
            sb.append(password);
            sb.append(salt);
            String saltedPassword = sb.toString();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(saltedPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256算法不存在", e);
        }
    }
    
}
