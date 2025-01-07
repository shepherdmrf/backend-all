import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.preprocessing import StandardScaler

# 读取Excel文件，假设文件名为 'data.xlsx'，第一行是特征名
df = pd.read_excel('./DataInput/uploadedFile.xlsx')

# 查看数据结构（可选）
print(df.head())

# 使用StandardScaler进行标准化
scaler = StandardScaler()
df_scaled = pd.DataFrame(scaler.fit_transform(df), columns=df.columns)

# 查看标准化后的数据（可选）
print(df_scaled.head())

# 设定绘图风格
sns.set(style="whitegrid")

# 创建一个画布
plt.figure(figsize=(20, 10))

# 绘制小提琴图，展示每个标准化后的特征的分布
sns.violinplot(data=df_scaled, inner="quart", scale="count")

# 设置标题
plt.title('Violin Plot of Standardized Features', fontsize=22)
plt.xlabel('Features', fontsize=20)
plt.ylabel('Standardized Value', fontsize=20)

# 显示图形
plt.xticks(rotation=90)  # 让特征名称垂直显示，避免重叠
plt.tight_layout()

plt.savefig("violin.png")
