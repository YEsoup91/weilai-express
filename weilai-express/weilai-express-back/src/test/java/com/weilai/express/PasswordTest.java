package com.weilai.express;

import cn.hutool.crypto.digest.BCrypt;
import org.testng.annotations.Test;

public class PasswordTest {
    
    @Test
    public void testPassword() {
        String password = "123456";
        String hash = BCrypt.hashpw(password);
        
        System.out.println("========== 密码信息 ==========");
        System.out.println("原始密码：" + password);
        System.out.println("BCrypt 哈希：" + hash);
        System.out.println("============================");
        
        // 验证
        boolean match = BCrypt.checkpw(password, hash);
        System.out.println("验证结果：" + match);
        
        // 现在用这个哈希值更新数据库
        System.out.println("\n请执行以下 SQL:");
        System.out.println("UPDATE sys_user SET password = '" + hash + "' WHERE username IN ('user001', 'merchant001', 'rider001', 'admin001');");
    }
}
