import numpy as np
import pandas as pd
from Printting import save_to_excel_1d
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler
from KernelCal.threshold_analysis3 import binary_code

# 步骤一：根据特征子集对应机器学习的预测性能指标，对特征决策表进行聚类分组

models = ['MLR', 'SVR', 'KNN', 'GPR']
for model in models:
    for i in range(10):
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

