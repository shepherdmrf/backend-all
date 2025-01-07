import io
import ast
import os
import sys
current_dir = os.path.dirname(os.path.abspath(__file__))
parent_dir = os.path.dirname(current_dir)  # 上一层目录
sys.path.append(parent_dir)
import numpy as np
import pandas as pd
from Printting import save_to_excel_1d
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler
from step3_threshold_analysis import binary_code

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')
# 步骤一：根据特征子集对应机器学习的预测性能指标，对特征决策表进行聚类分组

models = ['MLR', 'SVR', 'KNN', 'GPR']
for model in models:
    for i in range(1):
        result_index = 'result' + str(i)

        load_data_wb = '../Results/OnTrain/DKNCOR/' + model + '.xlsx'
        all_data = pd.DataFrame(pd.read_excel(load_data_wb, sheet_name=result_index))
        performances = all_data[['CV RMSE', 'RMSE', 'R2']]
        new_decisions = StandardScaler().fit_transform(performances)

        conditions = all_data['Features']
        number = 45
        binary_conditions = []
        for x in conditions:
            x = x.strip('[').strip(']')
            array_x = np.array(x.split(',')).astype(np.int8)
            binary_x = binary_code(array_x, number)
            binary_conditions.append(binary_x)

        km = KMeans(n_clusters=4, random_state=7)
        km.fit(binary_conditions)
        km.fit(new_decisions)
        labels = km.labels_
        print_data_wb = '../Results/OnTrain/DKNCOR/' + model + '.xlsx'
        save_to_excel_1d(labels, 'd_class', print_data_wb, result_index, 8, 2)

