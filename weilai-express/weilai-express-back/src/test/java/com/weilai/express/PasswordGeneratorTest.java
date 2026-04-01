package com.weilai.express;

import cn.hutool.crypto.digest.BCrypt;
import org.testng.annotations.Test;

public class PasswordGeneratorTest {
    
    @Test
    public void generatePassword() {
        String password = "123456";
        String hash = BCrypt.hashpw(password);
        
        System.out.println("========== 请复制以下哈希值 ==========");
        System.out.println(hash);
        System.out.println("====================================");
        
        // 立即验证
        boolean match = BCrypt.checkpw(password, hash);
        System.out.println("验证结果：" + match);
        
        if (match) {
            System.out.println("\n请在 DataGrip 中执行:");
            System.out.println("UPDATE sys_user SET password = '" + hash + "' WHERE username IN ('user001', 'merchant001', 'rider001', 'admin001');");
        }
    }
}
