import pandas as pd
from scipy.cluster import hierarchy as hie
from sklearn.manifold import TSNE
import matplotlib as mpl
from matplotlib import pyplot as plt
from Printting import create_workbook, save_to_excel_1d
import matplotlib.font_manager

# 绘制层次聚类树

mpl.rcParams["font.sans-serif"] = ["Arial"]
mpl.rcParams["font.size"] = 12
mpl.rcParams["axes.unicode_minus"] = False

if __name__ == '__main__':
    # data = pd.DataFrame(pd.read_excel('./data_cluster.xlsx', sheet_name='data')).values[1:-1, 3:-2]
    data = pd.DataFrame(pd.read_excel('./347_with_label.xlsx')).values[:, 1:-5]
    # feature_cluster = [8, 9, 11, 15, 16, 18, 19, 21, 22, 23, 24, 25, 26, 27, 29, 31, 34, 35, 36, 37, 40]
    # feature_cluster = [11, 26, 27, 34, 35]
    colors = ['blue', 'orange', 'red', 'purple', 'pink', 'green', 'grey', 'cyan']

    Songti = matplotlib.font_manager.FontProperties(fname='C:\Windows\Fonts\STSONG.TTF')

    low_dim_data = TSNE(n_components=2, random_state=80).fit_transform(data)

    disMat = hie.distance.pdist(data, 'euclidean')
    Z = hie.linkage(disMat, method='average')

    fig = plt.figure(figsize=(8, 5))
    P = hie.dendrogram(Z, truncate_mode='mtica', p=8)
    # plt.xlabel('Number of points in node (or index of point if no parenthesis)')
    plt.xlabel('样本编号', fontdict={'family': 'Microsoft YaHei'})
    # plt.savefig('./hierarchical_clustering.tif')
    plt.savefig('./Plotting/hierarchical_clustering_Chinese.tif')
    plt.show()

    label = hie.cut_tree(Z, height=1.3)
    label = label.reshape(label.size)
    print(label)
    # wb = './clustering_data.xlsx'
    # create_workbook(wb)
    # save_to_excel_1d(label, 'd_class_layer3-1', wb, 'cluster', 5, 2)
