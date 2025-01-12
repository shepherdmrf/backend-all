import pandas as pd
from matplotlib.patches import Circle, Ellipse
import matplotlib.pyplot as plt
import sys
import urllib.parse
import json
import re
import numpy as np
import random

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

kernel1 = parse_to_set(urllib.parse.unquote(sys.argv[1]))
kernel2 = parse_to_set(urllib.parse.unquote(sys.argv[2]))
kernel3 = parse_to_set(urllib.parse.unquote(sys.argv[3]))

kernel4 = kernel1 | kernel2

def create_feature_table(kernel3, kernel4, feature_map):
    data = []
    for key, value in feature_map.items():
        if key in kernel4:
            category, color = "Kernel4 (Intersection)", "#FFA07A"
        elif key in kernel3:
            category, color = "Kernel3 Only", "#87CEEB"
        else:
            continue
        data.append({"Feature ID": key, "Feature Name": value, "Category": category, "Color": color})
    return pd.DataFrame(data)

feature_table = create_feature_table(kernel3, kernel4, feature_map)

fig, axes = plt.subplots(1, 2, figsize=(15, 7), gridspec_kw={'width_ratios': [1.75, 1]})

theta = np.linspace(0, 2 * np.pi, 500)
x = np.cos(theta) * 1.4
y = np.sin(theta) * 1.4
axes[0].fill_between(x, 0, y, where=(y > 0), color="#87CEEB", alpha=0.9)
axes[0].fill_between(x, 0, y, where=(y < 0), color="#87CEEB", alpha=0.9)
ellipse = Ellipse((0, 0), width=2.8, height=0.6*1.4, edgecolor="black", facecolor="#FFA07A", alpha=1)
axes[0].add_patch(ellipse)

kernel4_features = sorted(kernel4)
kernel4_text = "     ".join(map(str, kernel4_features))
axes[0].text(0, 0, kernel4_text, fontsize=18, ha="center", va="center", color="black")

kernel3_only_features = sorted(kernel3 - kernel4)
num_features = len(kernel3_only_features)
num_rows_per_region = 2
col_spacing = 0.4
row_spacing = 0.3

has_extra = num_features % 2 != 0
num_main_features = num_features - 1 if has_extra else num_features
num_cols = (num_main_features // 2 + num_rows_per_region - 1) // num_rows_per_region

upper_features = kernel3_only_features[:num_main_features // 2]
for i, feature in enumerate(upper_features):
    row = i // num_cols
    col = i % num_cols
    total_width = (num_cols - 1) * col_spacing
    x_pos = -total_width / 2 + col * col_spacing
    y_pos = 0.9 - row * row_spacing
    axes[0].text(x_pos, y_pos, str(feature), fontsize=18, ha="center", va="center", color="black")

lower_features = kernel3_only_features[num_main_features // 2:num_main_features]
for i, feature in enumerate(lower_features):
    row = i // num_cols
    col = i % num_cols
    total_width = (num_cols - 1) * col_spacing
    x_pos = -total_width / 2 + col * col_spacing
    y_pos = -0.9 + row * row_spacing
    axes[0].text(x_pos, y_pos, str(feature), fontsize=18, ha="center", va="center", color="black")

if has_extra:
    extra_feature = kernel3_only_features[-1]
    axes[0].text(0, -1.2, str(extra_feature), fontsize=18, ha="center", va="center", color="black")

axes[0].set_aspect('equal', adjustable='box')
axes[0].set_xlim(-1.5, 1.5)
axes[0].set_ylim(-1.5, 1.5)
axes[0].axis("off")

axes[0].set_title(
    "Venn Diagram of the relationship between\n"
    "the combined kernel features and the optimal feature clusters",
    fontsize=14,
    fontweight="bold"
)

axes[1].axis("off")
table_data = feature_table[["Feature ID", "Feature Name"]]
table = axes[1].table(
    cellText=table_data.values,
    colLabels=table_data.columns,
    loc="center",
    cellLoc="center",
    colLoc="center"
)

for i, key in enumerate(table_data.index):
    category = feature_table.loc[key, "Category"]
    color = feature_table.loc[key, "Color"]
    for j in range(len(table_data.columns)):
        table[(i + 1, j)].set_facecolor(color)

table.auto_set_font_size(False)
table.set_fontsize(15)
table.auto_set_column_width(col=list(range(len(table_data.columns))))

for (row, col), cell in table.get_celld().items():
    if row == 0:
        cell.set_fontsize(12)
        cell.set_height(0.05)
        cell.set_text_props(weight='bold')
    else:
        cell.set_fontsize(10)
        cell.set_height(0.04)

plt.subplots_adjust(wspace=0)
plt.savefig("venn_diagram.png", dpi=200, bbox_inches="tight")
