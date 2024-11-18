import numpy as np
import pandas as pd
from sklearn.manifold import TSNE
import matplotlib as mpl
from matplotlib import pyplot as plt
from sklearn.preprocessing import StandardScaler
from Printting import create_workbook, save_to_excel_1d
from sklearn.metrics.pairwise import rbf_kernel
from sklearn.cluster import KMeans

mpl.rcParams["font.sans-serif"] = ["Microsoft YaHei"]
mpl.rcParams["font.size"] = 12
mpl.rcParams["axes.unicode_minus"] = False

if __name__ == '__main__':
    data = pd.DataFrame(pd.read_excel('./data.xlsx', sheet_name='data')).values[1:, 3:-1]
    colors = ['blue', 'orange', 'green', 'purple']
    # clusters = ['类簇4', '类簇2', '类簇3', '类簇1']
    clusters = ['类簇1', '类簇3', '类簇4', '类簇2']

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
                plt.scatter(xy[0], xy[1], color='red', label='支持向量')
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
    plt.savefig('./plotting/high_dimensional_distribution.tif')
    plt.show()

    path = './data.xlsx'
    save_to_excel_1d(labels, 'Label', path, 'org_label', 2, 2)
