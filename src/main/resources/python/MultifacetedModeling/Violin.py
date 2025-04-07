import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.preprocessing import StandardScaler

# 读取Excel文件，假设文件名为 'data.xlsx'，第一行是特征名
df = pd.read_excel('./DataInput/uploadedFile.xlsx')

# 使用StandardScaler进行标准化
scaler = StandardScaler()
df_scaled = pd.DataFrame(scaler.fit_transform(df), columns=df.columns)

# 计算原始数据的统计信息
stats = df.describe()

# 对原始数据的统计信息进行标准化
stats_scaled = (stats - df.mean()) / df.std()

# 设置绘图风格
sns.set(style="whitegrid")

# 创建一个画布
plt.figure(figsize=(20, 10))

# 绘制标准化数据的小提琴图
ax = sns.violinplot(data=df_scaled, inner="quart", density_norm="count")

# 在图上标记原始数据的统计值（标准化后）
for i, feature in enumerate(df.columns):
    # 获取各统计值的位置
    q1 = stats_scaled.loc["25%", feature]  # 第一四分位数
    median = stats_scaled.loc["50%", feature]  # 中位数
    q3 = stats_scaled.loc["75%", feature]  # 第三四分位数
    min_val = stats_scaled.loc["min", feature]  # 最小值
    max_val = stats_scaled.loc["max", feature]  # 最大值

    # 将统计值标注为文字，显示在对应位置
    offset = 0.05  # 调整偏移量

    ax.text(i, q1, f"{stats.loc['25%', feature]:.2f}", color="blue", ha="center", fontsize=10)
    ax.text(i, median, f"{stats.loc['50%', feature]:.2f}", color="green", ha="center", fontsize=10)
    ax.text(i, q3, f"{stats.loc['75%', feature]:.2f}", color="red", ha="center", fontsize=10)


# 设置标题和标签
plt.title('Violin Plot with Original Data Statistics (Standardized)', fontsize=16)
plt.xlabel('Features', fontsize=14)
plt.ylabel('Standardized Value', fontsize=14)

# 显示图形
plt.xticks(rotation=90)
plt.tight_layout()

plt.savefig("violin.png")
