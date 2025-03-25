import pandas as pd
import numpy as np
from matplotlib import pyplot as plt
import sys
import os

current_dir = os.path.dirname(os.path.abspath(__file__))
parent_dir = os.path.dirname(current_dir)  # 上一层目录
sys.path.append(parent_dir)

from sklearn import preprocessing
# from Printting import create_workbook, save_to_excel_1d
# from ModelsCV import predictors
import warnings
warnings.filterwarnings('ignore')


def load_data(filename, sheet_name='data'):
    data_frame = pd.DataFrame(pd.read_excel(filename, sheet_name=sheet_name))
    data_set = data_frame.values
    return data_set

plt.rcParams["font.family"] = ["Cambria"]
plt.rcParams["font.size"] = 12
plt.rcParams["axes.unicode_minus"] = False
# 利用最佳特征团训练模型，以获得支持向量
if __name__ == '__main__':
    load_data_wb = 'pred.xlsx'
    data = load_data(load_data_wb, sheet_name='Sheet1')[:, [0, 1, 2]]
    train_y = np.array(data[:, -3])
    predict_y = np.array(data[:, -2])
    std = np.array(data[:, -1])
    # 可视化结果
    plt.figure(figsize=(8, 6))
    x = np.arange(0, len(predict_y))
    plt.plot(train_y, train_y, color='r', ls='--', lw=3, label='Measured Energy Barrier(eV)')
    plt.plot(train_y, predict_y, 'o', color='#00A0FF', alpha=0.5, markersize=8, label='Predicted Energy Barrier(eV)')
    # plt.plot(train_y, y_pred, 'b-', label='Predicted Data')
    plt.fill_between(train_y, predict_y - 2 * std, predict_y + 2 * std, color='pink', alpha=0.6, label='Uncertainty of the Samples')
    plt.xlabel('Measured Energy Barrier(eV)', fontweight='bold', fontsize=12)
    plt.ylabel('Predicted Energy Barrier(eV)', fontweight='bold', fontsize=12)
    plt.xlim(0.6, 1.7)
    plt.ylim(0.6, 1.7)
    plt.xticks(fontname='Cambria', fontsize=12)  # x 轴刻度
    plt.yticks(fontname='Cambria', fontsize=12)  # y 轴刻度

    plt.legend(prop={'weight': 'bold', 'size': 12}, frameon=False)
    plt.savefig('Uncertainty.png', dpi=600)
    # plt.show()


