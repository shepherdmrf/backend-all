import warnings
import numpy as np
import sys
import os
import pandas as pd
from scipy.cluster import hierarchy as hie
from sklearn.manifold import TSNE
import matplotlib as mpl
from matplotlib import pyplot as plt
import matplotlib.font_manager
from openpyxl import Workbook
import io
# 解决中文字体问题
mpl.rcParams["font.sans-serif"] = ["Arial"]
mpl.rcParams["font.size"] = 12
mpl.rcParams["axes.unicode_minus"] = False
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

if __name__ == '__main__':
    # 读取 Excel 数据
    data = pd.DataFrame(pd.read_excel('./data_cluster.xlsx', sheet_name='data')).values[1:-1, 3:-2]
    data = data.astype(np.float64)

    print(type(data))  # 确保 data 是 numpy ndarray
    print(data.dtype)  # 确保数据类型是数值类型（float64）

    # 指定聚类特征列
    feature_cluster = [8, 9, 11, 15, 16, 18, 19, 21, 22, 23, 24, 25, 26, 27, 29, 31, 34, 35, 36, 37, 40]
    colors = ['blue', 'orange', 'red', 'purple', 'pink', 'green', 'grey', 'cyan']

    # 指定宋体
    Songti = matplotlib.font_manager.FontProperties(fname='C:\\Windows\\Fonts\\STSONG.TTF')

    # 降维
    low_dim_data = TSNE(n_components=2, random_state=80).fit_transform(data)

    # 计算欧几里得距离矩阵
    disMat = hie.distance.pdist(data, 'euclidean')

    # 进行层次聚类（average 方式）
    Z = hie.linkage(disMat, method='average')

    # 绘制树状图
    fig = plt.figure(figsize=(8, 5))
    P = hie.dendrogram(Z, truncate_mode='lastp', p=8)  # 修正 truncate_mode 参数
    plt.xlabel('样本编号', fontdict={'family': 'Microsoft YaHei'})
    plt.savefig('./Plotting/hierarchical_clustering_Chinese.tif')
    # plt.show()

    # 切割层次聚类树，获得聚类标签
    label = hie.cut_tree(Z, height=1.3)
    label = label.reshape(label.size)

    # 提取树状结构数据
    tree_structure = []
    for i in range(len(Z)):
        parent_node = int(Z[i, 0]) if Z[i, 0] < len(data) else int(Z[i, 0] - len(data))
        child_node_1 = int(Z[i, 1]) if Z[i, 1] < len(data) else int(Z[i, 1] - len(data))
        child_node_2 = int(Z[i, 2]) if Z[i, 2] < len(data) else int(Z[i, 2] - len(data))
        tree_structure.append([parent_node, child_node_1, child_node_2])

    # 创建 Excel 文件并保存树形结构
    wb_path = './clustering_data_tree.xlsx'
    wb = Workbook()
    ws = wb.active
    ws.title = "Hierarchical Clustering Tree"

    # 添加表头
    ws.append(["Node2", "Node1", "distance"])

    # 写入数据
    for row in tree_structure:
        ws.append(row)

    # 保存 Excel
    wb.save(wb_path)
    print(f"树形结构已保存到 {wb_path}")
