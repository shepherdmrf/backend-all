import pandas as pd
from matplotlib.patches import Ellipse
from matplotlib_venn import venn2
import matplotlib.pyplot as plt
import sys
import urllib.parse
import json
import re

# 定义编号与内容的映射（0-37）
feature_map = {
    0: "Occu_6b", 1: "Occu_18e", 2: "Occu_36f", 3: "C_Na", 4: "Occu_M1", 5: "Occu_M2",
    6: "EN_M1", 7: "EN_M2", 8: "avg_EN_M", 9: "Radius_M1", 10: "Radius_M2",
    11: "avg_Radius_M", 12: "Valence_M1", 13: "Valence_M2", 14: "avg_Valence_M",
    15: "Occu_X1", 16: "Occu_X2", 17: "EN_X1", 18: "EN_X2", 19: "avg_EN_X",
    20: "Radius_X1", 21: "Radius_X2", 22: "avg_Radius_X", 23: "Valence_X1",
    24: "Valence_X2", 25: "avg_Valence_X", 26: "a", 27: "c", 28: "V",
    29: "V_MO6", 30: "V_XO4", 31: "V_Na(1)O6", 32: "V_Na(2)O8", 33: "V_Na(3)O5",
    34: "BT2", 35: "BT1", 36: "Min_BT", 37: "RT", 38: "Entropy_6b", 39: "Entropy_18e",
    40: "Entropy_36f", 41: "Entropy_Na", 42: "Entropy_M", 43: "Entropy_X", 44: "T"
}

# 接收参数
kernel1 = sys.argv[1]
kernel2 = sys.argv[2]
kernel3 = sys.argv[3]

# 解码 URL 参数
kernel1 = urllib.parse.unquote(kernel1)
kernel2 = urllib.parse.unquote(kernel2)
kernel3 = urllib.parse.unquote(kernel3)

# 将字符串或 JSON 转换为集合
def parse_to_set(data):
    if isinstance(data, str):
        try:
            json_data = json.loads(data)
            if isinstance(json_data, list) and 'features' in json_data[0]:
                feature_str = json_data[0]['features']
                numbers = re.findall(r'\d+', feature_str)
            else:
                numbers = re.findall(r'\d+', data)
        except json.JSONDecodeError:
            numbers = re.findall(r'\d+', data)
    elif isinstance(data, list):
        flat_list = ",".join(map(str, data))
        numbers = re.findall(r'\d+', flat_list)
    else:
        raise ValueError("Unsupported data format")
    return set(map(int, numbers))

kernel1 = parse_to_set(kernel1)
kernel2 = parse_to_set(kernel2)
kernel3 = parse_to_set(kernel3)

# 计算交集
kernel4 = kernel1 | kernel2

# 创建特征表格
def create_feature_table(kernel3, kernel4, feature_map):
    data = []
    for key, value in feature_map.items():
        if key in kernel3 and key in kernel4:
            category, color = "Intersection", "#FFA07A"  # 黄色
        elif key in kernel3:
            category, color = "Kernel3 Only", "#FFDAB9"  # 绿色
        elif key in kernel4:
            category, color = "Kernel4 Only", "#FA8072"  # 蓝色
        else:
            continue
        data.append({"Feature ID": key, "Feature Name": value, "Category": category, "Color": color})
    return pd.DataFrame(data)

feature_table = create_feature_table(kernel3, kernel4, feature_map)

fig, axes = plt.subplots(
    1, 2, figsize=(15, 7),
    gridspec_kw={'width_ratios': [1.75, 1]}
)

venn = venn2([kernel3, kernel4], set_labels=("", ""), ax=axes[0])

colors = {
    '10': "#FFA07A",
    '01': "#FFDAB9",
    '11': "#FA8072",
}

for subset_label in venn.subset_labels:
    if subset_label and subset_label.get_text() == "0":
        subset_label.set_visible(False)

# 替换圆形为椭圆
max_width = 2.0  # 设置椭圆的最大宽度
max_height = 1.0  # 设置椭圆的最大高度

for subset, color in colors.items():
    patch = venn.get_patch_by_id(subset)
    if patch:
        path = patch.get_path()
        vertices = path.vertices
        center = vertices.mean(axis=0)  # 中心点
        center[0] -= 0.175
        radius_x = (vertices[:, 0].max() - vertices[:, 0].min()) / 2
        radius_y = (vertices[:, 1].max() - vertices[:, 1].min()) / 2

        # 限制椭圆的宽度和高度
        adjusted_width = min(1.5 * radius_x * 1.5, max_width)
        adjusted_height = min(1.75 * radius_y * 0.8, max_height)

        patch.remove()  # 移除圆形
        ellipse = Ellipse(
            xy=center,
            width=adjusted_width,  # 使用限制后的宽度
            height=adjusted_height,  # 使用限制后的高度
            color=color,
            alpha=0.7
        )
        axes[0].add_patch(ellipse)


axes[0].set_title(
    "Venn Diagram of the relationship between\n"
    "the combined kernel features and the optimal feature clusters",
    fontsize=14,
    fontweight="bold",
    pad=0  # 适当调整标题与图之间的距离
)

axes[0].title.set_y(1)

for subset_label in venn.subset_labels:
    if subset_label:  # 确保标签存在
        subset_label.set_fontsize(14)  # 设置字体大小
        subset_label.set_fontweight('bold')  # 可选：加粗数字

for subset, subset_label in zip(('10', '01', '11'), venn.subset_labels):
    if subset_label:
        if subset == '10':
            subset_label.set_x(subset_label.get_position()[0] - 0.1)
            subset_label.set_y(subset_label.get_position()[1] )
        elif subset == '01':
            subset_label.set_x(subset_label.get_position()[0] + 0.2)
            subset_label.set_y(subset_label.get_position()[1] - 0.1)
        elif subset == '11':
            subset_label.set_x(subset_label.get_position()[0] - 0.15)
            subset_label.set_y(subset_label.get_position()[1])


# 绘制表格
axes[1].axis("off")
table_data = feature_table[["Feature ID", "Feature Name"]]
table = axes[1].table(
    cellText=table_data.values,
    colLabels=table_data.columns,
    loc="center",
    cellLoc="center",
    colLoc="center"
)

# 根据分类为行着色
for i, key in enumerate(table_data.index):
    category = feature_table.loc[key, "Category"]
    color = feature_table.loc[key, "Color"]
    for j in range(len(table_data.columns)):
        table[(i + 1, j)].set_facecolor(color)

# 调整表格样式
table.auto_set_font_size(False)
table.set_fontsize(15)
table.auto_set_column_width(col=list(range(len(table_data.columns))))

for (row, col), cell in table.get_celld().items():
    if row == 0:  # 表头单元格
        cell.set_fontsize(12)  # 调整表头字体大小
        cell.set_height(0.05)  # 设置表头单元格高度
        cell.set_text_props(weight='bold')  # 加粗表头
    else:  # 数据单元格
        cell.set_fontsize(10)  # 调整数据单元格字体大小
        cell.set_height(0.04)  # 设置数据单元格高度

plt.subplots_adjust(wspace=0)  # 调整子图间距

# 保存图片
plt.savefig("venn_diagram.png", dpi=200, bbox_inches="tight")
