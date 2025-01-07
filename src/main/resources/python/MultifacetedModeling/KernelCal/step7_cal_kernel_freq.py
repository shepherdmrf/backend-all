import io
import ast
import os
import sys
current_dir = os.path.dirname(os.path.abspath(__file__))
parent_dir = os.path.dirname(current_dir)  # 上一层目录
sys.path.append(parent_dir)
import numpy as np
import pandas as pd
from collections import Counter
from Printting import create_directory, create_workbook, save_to_excel_1d

# 计算特征决策表最优分组中每个“数据特特征”的频率

if __name__ == '__main__':
    load = '../DataOutput/best_class.xlsx'
    models = ['MLR', 'SVR', 'KNN', 'GPR']

    for m in models:
        load1 = '../DataOutput/' + m + '/kernels.xlsx'
        load2 = '../Results/OnTrain/DKNCOR/' + m + '.xlsx'
        load3 = '../DataOutput/' + m + '/avg_classes.xlsx'
        all_kernels = pd.read_excel(load1, 'data').values
        all_best_classes = pd.read_excel(load, m).values

        pos_kernels, neg_kernels = [], []
        for i in range(1):
            sheet = 'result' + str(i)
            best_class = all_best_classes[i]
            best_class_size = pd.read_excel(load3, sheet).values[:, -1][best_class]

            all_data = pd.DataFrame(pd.read_excel(load2, sheet_name=sheet))
            all_features = all_data['Features']
            all_class_ids = all_data['d_class']
            features = []
            for j in range(all_data.shape[0]):
                if all_class_ids[j] == best_class:
                    temp_ids = all_features[j].strip('[').strip(']')
                    array_ids = np.array(temp_ids.split(',')).astype(np.int8)
                    features.extend(array_ids)
            count = Counter(features)

            print(all_kernels[i][0])
            print(len(all_kernels[i][0]))
            if len(all_kernels[i][0]) != 2:
                temp_kernels = np.array(all_kernels[i][0].strip('[').strip(']').split(',')).astype(np.int8)
            else:
                temp_kernels = []
            print(temp_kernels)
            ker_freq = []
            for ker in temp_kernels:
                freq = count[ker] / best_class_size
                ker_freq.append([ker, freq[0]])
            print(ker_freq)

            del_kers = []
            for kf in ker_freq:
                if kf[1] < 0.3:     # 当频率大于0.3为正数据核特征
                    del_kers.append(kf[0])
            new_kers = np.setdiff1d(temp_kernels, del_kers)
            pos_kernels.append(list(new_kers))
            neg_kernels.append(list(del_kers))
        save = '../DataOutput/' + m + '/final_kernels.xlsx'
        create_directory(save)
        create_workbook(save)
        save_to_excel_1d(pos_kernels, 'Pos-Kernels', save, 'kernels', 1, 2)
        save_to_excel_1d(neg_kernels, 'Neg-Kernels', save, 'kernels', 2, 2)

