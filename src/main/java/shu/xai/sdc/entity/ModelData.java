package shu.xai.sdc.entity;

public class ModelData {
    private Integer id; // 模型的唯一标识符
    private String name; // 模型名称
    private String modelPath; // 模型存储路径
    private String introductionFile; // 模型介绍文件的名称
    private String architectureFile; // 模型架构文件的名称

    // Getter 和 Setter 方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public String getIntroductionFile() {
        return introductionFile;
    }

    public void setIntroductionFile(String introductionFile) {
        this.introductionFile = introductionFile;
    }

    public String getArchitectureFile() {
        return architectureFile;
    }

    public void setArchitectureFile(String architectureFile) {
        this.architectureFile = architectureFile;
    }
}
