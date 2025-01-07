import numpy as np
import pandas as pd
from sklearn.manifold import TSNE
from sklearn.feature_selection import f_classif
import matplotlib as mpl
from matplotlib import pyplot as plt
from sklearn.preprocessing import StandardScaler
import os
import sys

current_dir = os.path.dirname(os.path.abspath(__file__))
parent_dir = os.path.dirname(current_dir)
parent_dir = os.path.dirname(parent_dir)
sys.path.append(parent_dir)

from Printting import create_workbook, save_to_excel_1d
from sklearn.metrics.pairwise import rbf_kernel
from sklearn.cluster import KMeans

mpl.rcParams["font.sans-serif"] = ["Microsoft YaHei"]
mpl.rcParams["font.size"] = 12
mpl.rcParams["axes.unicode_minus"] = False

if __name__ == '__main__':
    data = pd.DataFrame(pd.read_excel('./data.xlsx', sheet_name='data')).values[1:, 3:-1]
    feature_names = pd.DataFrame(pd.read_excel('./data.xlsx', sheet_name='data')).values[0, 3:-1]
    decision_feature_name = 'BVSE'
    decision_feature_values = pd.DataFrame(pd.read_excel('./data.xlsx', sheet_name='data')).values[1:, -1]

    print(feature_names)

    colors = ['blue', 'orange', 'green', 'purple']
    # clusters = ['类簇4', '类簇2', '类簇3', '类簇1']
    clusters = ['Cluster 1', 'Cluster 3', 'Cluster 4', 'Cluster 2']

    scaler = StandardScaler()
    data_pro = scaler.fit_transform(data)
    high_dimensional_data = rbf_kernel(data_pro)

    km = KMeans(n_clusters=4, random_state=100)
    # km.fit(data_pro)
    km.fit(high_dimensional_data)
    labels = km.labels_
    first_ones = []
    for x in range(4):
        first_one = np.where(labels == x)[0][1]
        first_ones.append(first_one)
    first_ones = np.array(first_ones)[list(set(labels))]
    print(first_ones)

    # low_dim_data = TSNE(n_components=2, random_state=1).fit_transform(data_pro)
    low_dim_data = TSNE(n_components=2, random_state=1).fit_transform(high_dimensional_data)
    fig = plt.figure(figsize=(8, 5))
    for i, xy, l in zip(range(np.array(low_dim_data).shape[0]), low_dim_data, labels):
        if i in [13, 15, 18, 20, 51, 68, 72, 79]:
            if i == 79:
                plt.scatter(xy[0], xy[1], color='red', label='Support Vector')
            else:
                plt.scatter(xy[0], xy[1], color='red')
        elif i in first_ones:
            print(colors[l])
            plt.scatter(xy[0], xy[1], color=colors[l], label=clusters[l])
        else:
            plt.scatter(xy[0], xy[1], color=colors[l])
    plt.legend(ncol=2)
    plt.xlabel('PC1')
    plt.ylabel('PC2')
    plt.legend(ncol=1, loc='upper left')
    # plt.savefig('./plotting/low_dimensional_distribution.tif')
    plt.savefig('./plotting/high_dimensional_distribution.png')
#     plt.show()

    low_dim_data = TSNE(n_components=2, random_state=1).fit_transform(data_pro)
#     low_dim_data = TSNE(n_components=2, random_state=1).fit_transform(high_dimensional_data)
    fig = plt.figure(figsize=(8, 5))
    for i, xy, l in zip(range(np.array(low_dim_data).shape[0]), low_dim_data, labels):
        if i in [13, 15, 18, 20, 51, 68, 72, 79]:
            if i == 79:
                plt.scatter(xy[0], xy[1], color='red', label='Support Vector')
            else:
                plt.scatter(xy[0], xy[1], color='red')
        elif i in first_ones:
            print(colors[l])
            plt.scatter(xy[0], xy[1], color=colors[l], label=clusters[l])
        else:
            plt.scatter(xy[0], xy[1], color=colors[l])
    plt.legend(ncol=2)
    plt.xlabel('PC1')
    plt.ylabel('PC2')
    plt.legend(ncol=1, loc='upper left')
    plt.savefig('./plotting/low_dimensional_distribution.png')
#     plt.savefig('./plotting/high_dimensional_distribution.tif')
#
#     path = './data.xlsx'
#     save_to_excel_1d(labels, 'Label', path, 'org_label', 2, 2)
#
#     # Calculate cluster feature means and std
#     cluster_features = {}
#     for cluster_id in range(4):
#         indices = np.where(labels == cluster_id)[0]
#         original_features = data[indices, :]
#         df = pd.DataFrame(original_features, columns=feature_names)
#         cluster_features[clusters[cluster_id]] = df
#
#     # 添加决策特征到类簇数据中
#     cluster_summary = {}
#
#     for cluster_name, feature_df in cluster_features.items():
#         indices = np.where(labels == clusters.index(cluster_name))[0]
#         decision_values = decision_feature_values[indices]  # 决策特征对应的值
#
#         # 将决策特征添加到 DataFrame
#         feature_df[decision_feature_name] = decision_values
#
#         # 计算均值和标准差
#         cluster_mean = feature_df.mean()
#         cluster_std = feature_df.std()
#         cluster_summary[cluster_name] = {"mean": cluster_mean, "std": cluster_std}
#
#     # 输出每个类簇的特征和决策特征
#     with pd.ExcelWriter('./cluster_features.xlsx') as writer:
#         for cluster_name, feature_df in cluster_features.items():
#             feature_df.to_excel(writer, sheet_name=f'{cluster_name}_Data', index=False)
#
#         # 汇总表：均值和标准差
#         summary_data = []
#         for cluster_name, stats in cluster_summary.items():
#             for feature_name in stats["mean"].index:
#                 summary_data.append({
#                     "Cluster": cluster_name,
#                     "Feature": feature_name,
#                     "Mean": stats["mean"][feature_name],
#                     "Std": stats["std"][feature_name]
#                 })
#         summary_df = pd.DataFrame(summary_data)
#         summary_df.to_excel(writer, sheet_name='Summary', index=False)
#
#     print("Cluster feature data and decision feature exported to './cluster_features_with_decision.xlsx'.")
#
#     # Method 2: Normalized feature means heatmap
#     cluster_names = list(cluster_summary.keys())
#     feature_names = cluster_summary[cluster_names[0]]['mean'].index
#     mean_values = np.array([cluster_summary[name]['mean'] for name in cluster_names])
#     normalized_means = (mean_values - mean_values.min(axis=0)) / (mean_values.max(axis=0) - mean_values.min(axis=0))
#
#     fig, ax = plt.subplots(figsize=(12, 6))
#     im = ax.imshow(normalized_means, cmap='coolwarm', aspect='auto')
#     ax.set_xticks(np.arange(len(feature_names)))
#     ax.set_yticks(np.arange(len(cluster_names)))
#     ax.set_xticklabels(feature_names, rotation=45, ha='right')
#     ax.set_yticklabels(cluster_names)
#     plt.colorbar(im, ax=ax)
#     plt.title('Normalized Cluster Feature Means')
#     plt.tight_layout()
#     plt.savefig('./plotting/normalized_feature_means.png')
#
#     # Method 3: Feature importance using ANOVA-like F-test
#     f_values, p_values = f_classif(data, labels)
#     feature_importance = pd.DataFrame({
#         'Feature': feature_names,
#         'F-Value': f_values,
#         'P-Value': p_values
#     }).sort_values(by='F-Value', ascending=False)
#
#     feature_importance.to_excel('./feature_importance.xlsx', index=False)
#
#     # Visualize feature importance
#     fig, ax = plt.subplots(figsize=(10, 6))
#     ax.barh(feature_importance['Feature'], feature_importance['F-Value'], color='skyblue')
#     ax.set_xlabel('F-Value')
#     ax.set_title('Feature Importance (ANOVA F-test)')
#     plt.gca().invert_yaxis()
#     plt.tight_layout()
#     plt.savefig('./plotting/feature_importance.png')
#
#     print("Analysis complete. Results saved.")