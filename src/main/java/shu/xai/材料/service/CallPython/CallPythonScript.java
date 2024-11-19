package shu.xai.材料.service.CallPython;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CallPythonScript {
    public static void call() {
        // 设置 Python 可执行文件路径和脚本路径
        String pythonPath = "python.exe";
        String scriptPath = "./src/main/resources/python/tool/featureTable_to_sql.py";

        // 创建 ProcessBuilder
        ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath);

        // 获取并修改环境变量
        String systemPath = System.getenv("PATH");
        Map<String, String> env = processBuilder.environment();
        env.put("PATH", systemPath);

        try {
            // 打印调试信息
            System.out.println("Executing command: " + pythonPath + " " + scriptPath);

            // 启动进程
            Process process = processBuilder.start();

            // 读取标准输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // 读取错误输出
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
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
