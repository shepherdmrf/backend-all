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
from Printting import create_directory, create_workbook, save_to_excel_2d
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')
# 步骤二：计算特征决策表每个分组中各个特征出现的频率

print("run start")
models = ['MLR', 'SVR', 'KNN', 'GPR']
for model in models:
    for i in range(1):
        result_index = 'result' + str(i)

        load_data_wb = '../Results/OnTrain/DKNCOR/' + model + '.xlsx'
        all_data = pd.DataFrame(pd.read_excel(load_data_wb, sheet_name=result_index))
        features = all_data['Features']
        cs = all_data['d_class']

        feature_groups = [[], [], [], []]
        nums = [0, 0, 0, 0]
        for f,  c in zip(features, cs):
            f = f.strip('[').strip(']')
            feature_groups[c].extend(np.array(f.split(',')).astype(np.int8))
            nums[c] += 1

        counts = []
        for x in feature_groups:
            c = Counter(x)
            counts.append(c)

        frequencies = []
        for index, x in enumerate(counts):
            frequency = np.full(45, 0.0)
            for i in range(45):
                frequency[i] = x[i] / nums[index]
            frequencies.append(frequency)

        path = '../DataOutput/' + model + '/frequency.xlsx'
        create_directory(path)
        create_workbook(path)
        save_to_excel_2d(frequencies, range(45), path, result_index, 1, 2)
