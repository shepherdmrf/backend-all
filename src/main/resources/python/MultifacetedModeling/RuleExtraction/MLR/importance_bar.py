import numpy as np
import pandas as pd
import matplotlib as mpl
from matplotlib import pyplot as plt
import matplotlib.font_manager

# 绘制特征重要度柱状图

mpl.rcParams["font.sans-serif"] = ['SemHei']
mpl.rcParams["font.size"] = 12
mpl.rcParams["axes.unicode_minus"] = False

if __name__ == '__main__':
    data = pd.DataFrame(pd.read_excel('./training_result.xlsx', sheet_name='all'))
    # data = pd.DataFrame(pd.read_excel('./training_result.xlsx', sheet_name='all_pos'))
    coef = data['Importance']
    pcc = data['PCC']
    name = data['Name']

    Songti = matplotlib.font_manager.FontProperties(fname='C:\Windows\Fonts\STSONG.TTF')

    plt.figure(figsize=(14, 7))
    plt.barh(range(len(name)), coef, 0.3, color='#0780cf', label='LC')
    plt.barh(np.arange(len(name))+0.3, pcc, 0.3, color='#f47a75', label='PCC')
    for i, c in enumerate(coef):
        if c > 0:
            plt.text(c + 0.015, i - 0.17, '%.3f' % c, fontsize=10)
        else:
            plt.text(c - 0.1, i - 0.17, '%.3f' % c, fontsize=10)
            # plt.text(c - 0.07, i - 0.17, '%.3f' % c, fontsize=10)
    for i, p in enumerate(pcc):
        if p > 0:
            plt.text(p + 0.015, i + 0.15, '%.3f' % p, fontsize=10)
            # plt.text(p + 0.01, i + 0.15, '%.3f' % p, fontsize=10)
        else:
            plt.text(p - 0.1, i + 0.15, '%.3f' % p, fontsize=10)
            # plt.text(p - 0.075, i + 0.15, '%.3f' % p, fontsize=10)

    plt.plot([-1.1, 1.3], [11.65, 11.65], color='grey', linestyle='--')
    plt.plot([-1.1, 1.3], [8.65, 8.65], color='grey', linestyle='--')
    plt.plot([-1.1, 1.3], [6.65, 6.65], color='grey', linestyle='--')
    plt.plot([-1.1, 1.3], [5.65, 5.65], color='grey', linestyle='--')
    plt.plot([-1.1, 1.3], [0.65, 0.65], color='grey', linestyle='--')

    # plt.plot([-1.07, 0.65], [11.65, 11.65], color='grey', linestyle='--')
    # plt.plot([-1.07, 0.65], [8.65, 8.65], color='grey', linestyle='--')
    # plt.plot([-1.07, 0.65], [6.65, 6.65], color='grey', linestyle='--')
    # plt.plot([-1.07, 0.65], [5.65, 5.65], color='grey', linestyle='--')
    # plt.plot([-1.07, 0.65], [0.65, 0.65], color='grey', linestyle='--')

    plt.xlim(-1.1, 1.3)
    # plt.xlim(-1.07, 0.65)
    plt.ylim(-1, len(coef))
    # plt.ylim(-1, len(coef)+0.8)
    plt.yticks(range(0, len(coef)), list(name), fontsize=10)
    plt.xticks(fontsize=10)
    # plt.xlabel('Coef')
    # plt.ylabel('Descriptor')
    plt.xlabel('取值', fontdict={'family': 'Microsoft YaHei'})
    plt.ylabel('描述符', fontdict={'family': 'Microsoft YaHei'})
    plt.legend(ncol=1, loc='upper right')
    # plt.legend(ncol=2, loc='upper center')
    plt.savefig('./Plotting/all_importance_Chinese.tif')
    # plt.savefig('./Plotting/consistent_importance_Chinese.tif')
    plt.show()