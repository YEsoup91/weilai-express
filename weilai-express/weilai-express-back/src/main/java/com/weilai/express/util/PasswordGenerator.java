package com.weilai.express.util;

import cn.hutool.crypto.digest.BCrypt;

public class PasswordGenerator {
    public static void main(String[] args) {
        String password = "123456";
        String hash = BCrypt.hashpw(password);
        
        System.out.println("========== 密码信息 ==========");
        System.out.println("原始密码：" + password);
        System.out.println("BCrypt 哈希：" + hash);
        System.out.println("============================");
        
        // 验证
        boolean match = BCrypt.checkpw(password, hash);
        System.out.println("验证结果：" + match);
    }
}
