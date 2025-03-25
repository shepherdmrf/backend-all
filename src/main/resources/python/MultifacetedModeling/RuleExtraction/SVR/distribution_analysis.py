import numpy as np
import pandas as pd
from sklearn.manifold import TSNE
from sklearn.metrics.pairwise import rbf_kernel
from sklearn.cluster import KMeans
import matplotlib as mpl
from matplotlib import pyplot as plt
from sklearn.preprocessing import StandardScaler
from sklearn.feature_selection import f_classif
import json
import os

# 解决 KMeans 在 Windows 下的内存泄漏问题
os.environ["OMP_NUM_THREADS"] = "1"

mpl.rcParams["font.sans-serif"] = ["Microsoft YaHei"]
mpl.rcParams["font.size"] = 12
mpl.rcParams["axes.unicode_minus"] = False

if __name__ == '__main__':
    data = pd.DataFrame(pd.read_excel('./data.xlsx', sheet_name='data')).values[1:, 3:-1]
    feature_names = pd.DataFrame(pd.read_excel('./data.xlsx', sheet_name='data')).values[0, 3:-1]
    decision_feature_name = 'BVSE'
    decision_feature_values = pd.DataFrame(pd.read_excel('./data.xlsx', sheet_name='data')).values[1:, -1]

    colors = ['blue', 'orange', 'green', 'purple']
    clusters = ['Cluster 1', 'Cluster 3', 'Cluster 4', 'Cluster 2']

    scaler = StandardScaler()
    data_pro = scaler.fit_transform(data)
    high_dimensional_data = rbf_kernel(data_pro)

    km = KMeans(n_clusters=4, random_state=100, n_init=10)
    km.fit(high_dimensional_data)
    labels = km.labels_

    # 获取每个类簇的样本索引
    cluster_samples = {clusters[i]: np.where(labels == i)[0].tolist() for i in range(4)}
    with open('./cluster_samples.json', 'w') as f:
        json.dump(cluster_samples, f)

    first_ones = [int(np.where(labels == x)[0][1]) for x in range(4)]
    first_ones = np.array(first_ones)[list(set(labels))]

    low_dim_data = TSNE(n_components=2, random_state=1).fit_transform(high_dimensional_data)

    # 计算支持向量
    support_vectors = [13, 15, 18, 20, 51, 68, 72, 79]

    # 存储散点数据以便前端使用 ECharts（转换 float32 为 float）
    scatter_data = []
    for i, xy, l in zip(range(np.array(low_dim_data).shape[0]), low_dim_data, labels):
        scatter_data.append({
            "x": float(xy[0]),
            "y": float(xy[1]),
            "label": int(l),
            "is_support_vector": int(i in support_vectors)
        })

    with open('./scatter_data.json', 'w') as f:
        json.dump(scatter_data, f)

    # 保存聚类结果到 Excel
    cluster_results = pd.DataFrame({
        'Sample Index': np.arange(len(labels)),
        'Cluster Label': labels
    })
    cluster_results.to_excel('./clustering_results.xlsx', index=False)

    fig = plt.figure(figsize=(8, 5))
    for i, xy, l in zip(range(np.array(low_dim_data).shape[0]), low_dim_data, labels):
        if i in support_vectors:
            plt.scatter(float(xy[0]), float(xy[1]), color='red', label='Support Vector' if 'Support Vector' not in plt.gca().get_legend_handles_labels()[1] else "")
        elif i in first_ones:
            plt.scatter(float(xy[0]), float(xy[1]), color=colors[l], label=clusters[l])
        else:
            plt.scatter(float(xy[0]), float(xy[1]), color=colors[l])

    plt.legend(ncol=1, loc='upper left')
    plt.xlabel('PC1')
    plt.ylabel('PC2')
    plt.savefig('./plotting/high_dimensional_distribution.png')

    # 计算特征重要性
    f_values, p_values = f_classif(data, labels)
    feature_importance = pd.DataFrame({
        'Feature': feature_names.flatten().tolist(),
        'F-Value': f_values,
        'P-Value': p_values
    }).sort_values(by='F-Value', ascending=False)

    feature_importance.to_excel('./feature_importance.xlsx', index=False)

    # 存储特征重要性数据以便前端使用 ECharts
    feature_importance_json = feature_importance.to_dict(orient='records')
    with open('./feature_importance.json', 'w') as f:
        json.dump(feature_importance_json, f)

    # 可视化特征重要性
    fig, ax = plt.subplots(figsize=(10, 6))
    ax.barh(feature_importance['Feature'], feature_importance['F-Value'], color='skyblue')
    ax.set_xlabel('F-Value')
    ax.set_title('Feature Importance')
    plt.gca().invert_yaxis()
    plt.tight_layout()
    plt.savefig('./plotting/feature_importance.png')

    cluster_features = {}
    for cluster_id in range(4):
        indices = np.where(labels == cluster_id)[0]
        original_features = data[indices, :]
        df = pd.DataFrame(original_features, columns=feature_names)
        cluster_features[clusters[cluster_id]] = df

    # 添加决策特征到类簇数据中
    cluster_summary = {}

    for cluster_name, feature_df in cluster_features.items():
        indices = np.where(labels == clusters.index(cluster_name))[0]
        decision_values = decision_feature_values[indices]  # 决策特征对应的值

        # 将决策特征添加到 DataFrame
        feature_df[decision_feature_name] = decision_values

        # 计算均值和标准差
        cluster_mean = feature_df.mean()
        cluster_std = feature_df.std()
        cluster_summary[cluster_name] = {"mean": cluster_mean, "std": cluster_std}

    # 输出每个类簇的特征和决策特征
    with pd.ExcelWriter('./cluster_features.xlsx') as writer:
        for cluster_name, feature_df in cluster_features.items():
            feature_df.to_excel(writer, sheet_name=f'{cluster_name}_Data', index=False)

        # 汇总表：均值和标准差
        summary_data = []
        for cluster_name, stats in cluster_summary.items():
            for feature_name in stats["mean"].index:
                summary_data.append({
                    "Cluster": cluster_name,
                    "Feature": feature_name,
                    "Mean": stats["mean"][feature_name],
                    "Std": stats["std"][feature_name]
                })
        summary_df = pd.DataFrame(summary_data)
        summary_df.to_excel(writer, sheet_name='Summary', index=False)