package shu.xai.材料.service.CallPython;

import shu.xai.characteristicClusterConstruction.entity.Feature;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class CallPythonScript {
    public static void call(String scriptPath, String WorkPath, List<String> arg) {
        // 设置 Python 可执行文件路径和脚本路径
        String pythonPath = "python.exe";

        // 创建 ProcessBuilder
        String Parameter=String.valueOf(arg);
        ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath,Parameter);

        // 获取并修改环境变量
        String systemPath = System.getenv("PATH");
        Map<String, String> env = processBuilder.environment();
        env.put("PATH", systemPath);
        processBuilder.directory(new java.io.File(WorkPath));
        try {
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
    public static void call(String scriptPath, String WorkPath) {
        // 设置 Python 可执行文件路径和脚本路径
        String pythonPath = "python.exe";

        // 创建 ProcessBuilder
        ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath);

        // 获取并修改环境变量
        String systemPath = System.getenv("PATH");
        Map<String, String> env = processBuilder.environment();
        env.put("PATH", systemPath);
        processBuilder.directory(new java.io.File(WorkPath));
        try {
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
    public static void CallKnowlegeKernal(String arg) {
        // 设置 Python 可执行文件路径和脚本路径
        String pythonPath = "python.exe";
        String scriptPath = "./ModelSelection.py";

        // 创建 ProcessBuilder
        ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath,arg);

        // 获取并修改环境变量
        String systemPath = System.getenv("PATH");
        Map<String, String> env = processBuilder.environment();
        env.put("PATH", systemPath);
        processBuilder.directory(new java.io.File("./src/main/resources/python/MultifacetedModeling/CodesForValidation"));
        try {
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
    public static void test(String arg) {
        // 设置 Python 可执行文件路径和脚本路径
        String pythonPath = "python.exe";
        String scriptPath = "C:\\Users\\MSI\\PycharmProjects\\pythonProject\\test.py";

        // 创建 ProcessBuilder
        ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath,arg);

        // 获取并修改环境变量
        String systemPath = System.getenv("PATH");
        Map<String, String> env = processBuilder.environment();
        env.put("PATH", systemPath);
        try {
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

    public static void CallKnowPosKernal(String jsonFeatures1, String jsonFeatures2, String model) {
        try {
            // 设置 Python 可执行文件路径和脚本路径
            String pythonPath = "python.exe";
            String scriptPath = "./DKncorFSonAllTrain-KnowPosKernel.py";

            String encodedJsonFeatures1 = URLEncoder.encode(jsonFeatures1, "UTF-8");
            String encodedJsonFeatures2 = URLEncoder.encode(jsonFeatures2, "UTF-8");

            // 构造 Python 命令，传递参数给脚本
            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath, encodedJsonFeatures1, encodedJsonFeatures2, model);

            System.out.println("jsonFeatures1: " + jsonFeatures1);
            System.out.println("jsonFeatures2: " + jsonFeatures2);

            String systemPath = System.getenv("PATH");
            Map<String, String> env = processBuilder.environment();
            env.put("PATH", systemPath);
            processBuilder.directory(new java.io.File("./src/main/resources/python/MultifacetedModeling/CodesForValidation"));

            // 启动进程
            Process process = processBuilder.start();

            // 获取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // 输出 Python 执行的日志
            }

            // 读取错误输出
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
            while ((line = errorReader.readLine()) != null) {
                System.err.println("Error: " + line);
            }

            // 等待脚本执行完毕
            int exitCode = process.waitFor();
            System.out.println("Python script exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void CallFeatureVenn(String features1, String jsonFeatures2, String features3){
        try {
            // 设置 Python 可执行文件路径和脚本路径
            String pythonPath = "python.exe"; // 这里假设 python 已添加到系统环境变量中
            String scriptPath = "./FeatureVenn.py";

            String encodedFeatures1 = URLEncoder.encode(features1, "UTF-8");
            String encodedJsonFeatures2 = URLEncoder.encode(jsonFeatures2, "UTF-8");
            String encodedFeatures3 = URLEncoder.encode(features3, "UTF-8");

            // 构造 Python 命令，传递参数给脚本
            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath, encodedFeatures1, encodedJsonFeatures2, encodedFeatures3);

            System.out.println("features1: " + features1);
            System.out.println("jsonFeatures2: " + jsonFeatures2);
            System.out.println("features3: " + features3);

            String systemPath = System.getenv("PATH");
            Map<String, String> env = processBuilder.environment();
            env.put("PATH", systemPath);
            processBuilder.directory(new java.io.File("./src/main/resources/python/MultifacetedModeling"));

            // 启动进程
            Process process = processBuilder.start();

            // 获取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // 输出 Python 执行的日志
            }

            // 读取错误输出
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
            while ((line = errorReader.readLine()) != null) {
                System.err.println("Error: " + line);
            }

            // 等待脚本执行完毕
            int exitCode = process.waitFor();
            System.out.println("Python script exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void CallSVRTrainning(String features1){
        try {
            // 设置 Python 可执行文件路径和脚本路径
            String pythonPath = "python.exe";
            String scriptPath = "./training.py";

            String encodedFeatures1 = URLEncoder.encode(features1, "UTF-8");

            // 构造 Python 命令，传递参数给脚本
            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath, encodedFeatures1);

            System.out.println("features1: " + features1);

            String systemPath = System.getenv("PATH");
            Map<String, String> env = processBuilder.environment();
            env.put("PATH", systemPath);
            env.put("PYTHONPATH", "./src/main/resources/python/MultifacetedModeling");
//            processBuilder.directory(new java.io.File("./src/main/resources/python/MultifacetedModeling/RuleExtraction/SVR"));
            processBuilder.directory(new java.io.File("./src/main/resources/python/MultifacetedModeling/RuleExtraction/SVR"));

            // 启动进程
            Process process = processBuilder.start();

            // 获取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // 输出 Python 执行的日志
            }

            // 读取错误输出
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
            while ((line = errorReader.readLine()) != null) {
                System.err.println("Error: " + line);
            }

            // 等待脚本执行完毕
            int exitCode = process.waitFor();
            System.out.println("Python script exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void CallSVRAnalysis(){
        try {
            // 设置 Python 可执行文件路径和脚本路径
            String pythonPath = "python.exe";
            String scriptPath = "./distribution_analysis.py";

            // 构造 Python 命令，传递参数给脚本
            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath);

            String systemPath = System.getenv("PATH");
            Map<String, String> env = processBuilder.environment();
            env.put("PATH", systemPath);
//            processBuilder.directory(new java.io.File("./src/main/resources/python/MultifacetedModeling/RuleExtraction/SVR"));
            processBuilder.directory(new java.io.File("./src/main/resources/python/MultifacetedModeling/RuleExtraction/SVR"));

            // 启动进程
            Process process = processBuilder.start();

            // 获取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // 输出 Python 执行的日志
            }

            // 读取错误输出
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
            while ((line = errorReader.readLine()) != null) {
                System.err.println("Error: " + line);
            }

            // 等待脚本执行完毕
            int exitCode = process.waitFor();
            System.out.println("Python script exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void CallViolin(){
        try {
            // 设置 Python 可执行文件路径和脚本路径
            String pythonPath = "python.exe"; // 这里假设 python 已添加到系统环境变量中
            String scriptPath = "./Violin.py";

            // 构造 Python 命令，传递参数给脚本
            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath);

            String systemPath = System.getenv("PATH");
            Map<String, String> env = processBuilder.environment();
            env.put("PATH", systemPath);
            processBuilder.directory(new java.io.File("./src/main/resources/python/MultifacetedModeling"));

            // 启动进程
            Process process = processBuilder.start();

            // 获取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line); // 输出 Python 执行的日志
            }

            // 读取错误输出
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));
            while ((line = errorReader.readLine()) != null) {
                System.err.println("Error: " + line);
            }

            // 等待脚本执行完毕
            int exitCode = process.waitFor();
            System.out.println("Python script exited with code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
