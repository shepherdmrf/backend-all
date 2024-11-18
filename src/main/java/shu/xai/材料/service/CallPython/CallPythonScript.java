package shu.xai.材料.service.CallPython;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class CallPythonScript {
    public static void call() {
        // 使用 ProcessBuilder 设置 Python 解释器和脚本路径
        ProcessBuilder processBuilder = new ProcessBuilder(
                "C:\\Users\\20468\\AppData\\Local\\Programs\\Python\\Python312\\python.exe", // 替换为 Python 的完整路径
                ".\\src\\main\\resources\\python\\tool\\featureTable_to_sql.py"
        );

        try {
            // 启动进程
            Process process = processBuilder.start();

            // 读取标准输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 读取错误输出
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                System.err.println("Error: " + line);
            }

            // 等待进程结束
            int exitCode = process.waitFor();
            System.out.println("Python script exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
