import io
import ast
import os
import sys
current_dir = os.path.dirname(os.path.abspath(__file__))
parent_dir = os.path.dirname(current_dir)  # 上一层目录
sys.path.append(parent_dir)

import numpy as np
import pandas as pd
import copy
from Printting import create_directory, create_workbook, save_to_excel_2d

# 步骤三：“阈值”敏感性分析，其中“阈值”约简决策表的阈值
print("run start")

# 将特征组合转换为二进制编码数组
def binary_code(x, num):
    temp_x = np.full(num, 0)
    for i in range(num):
        if i in x:
            temp_x[i] = 1
    return temp_x


def cal_ind(a):
    m = np.array(a).shape[0]
    candidates = list(range(m))
    inds = []
    for i in range(m):
        if i in candidates:
            candidates[i] = None
            ind = [i]
            for j in range(m):
                if candidates[j]:
                    if np.array_equal(a[i], a[j]):
                        ind.append(j)
                        candidates[j] = None
            inds.append(ind)
    return inds


# 计算正域
def cal_positive_region(conditions, decision):
    positive_regions = []
    for d in decision:
        for c in conditions:
            if set(c) <= set(d):
                positive_regions.extend(c)
    return positive_regions


if __name__ == '__main__':
    models = ['MLR', 'SVR', 'KNN', 'GPR']
    for model in models:
        for i in range(1):
            result_index = 'result' + str(i)

            load_data_wb = '../Results/OnTrain/DKNCOR/' + model + '.xlsx'
            all_data = pd.DataFrame(pd.read_excel(load_data_wb, sheet_name=result_index))
            conditions = all_data['Features']
            cs = all_data['d_class']

            path = '../DataOutput/' + model + '/frequency.xlsx'
            create_directory(path)
            avg_data = pd.DataFrame(pd.read_excel(path, sheet_name=result_index)).values

            number = 45
            binary_conditions = []
            for x in conditions:
                x = x.strip('[').strip(']')
                array_x = np.array(x.split(',')).astype(np.int8)
                binary_x = binary_code(array_x, number)
                binary_conditions.append(binary_x)

            total_complete_degrees = []
            for t in np.arange(0.1, 0.70, 0.01):
                for x, c in zip(binary_conditions, cs):
                    for i, f in zip(range(45), avg_data[c]):
                        if x[i] == 1 and f < t:
                            x[i] = 0

                '''
                path = './DataOutput' + model + '/decision_table.xlsx'
                create_workbook(path)
                save_to_excel_2d(binary_conditions, range(45), path, model, 1, 2)
        
                load_data_wb = './DataInput/decision_table_revised.xlsx'
                all_data = pd.DataFrame(pd.read_excel(load_data_wb, sheet_name="GPR")).values
                '''

                conditions = copy.deepcopy(binary_conditions)
                decisions = copy.deepcopy(cs)
                retained_conditions = list(range(45))

                global_inds_conditions = cal_ind(conditions)
                inds_decisions = cal_ind(decisions)
                global_positive_regions = cal_positive_region(global_inds_conditions, inds_decisions)
                total_complete_degree = len(global_positive_regions) / len(decisions)
                print(total_complete_degree)
                print('******')
                total_complete_degrees.append([t, total_complete_degree])

                for i in range(45):
                    retained_conditions = np.setdiff1d(range(45), i)
                    new_binary_conditions = np.array(conditions)[:, retained_conditions]
                    inds_conditions = cal_ind(new_binary_conditions)
                    positive_regions = cal_positive_region(inds_conditions, inds_decisions)
                    # 计算知识依赖度
                    complete_degree = len(positive_regions) / len(decisions)
                    # 输出使得知识依赖度减小的特征编号
                    if complete_degree < total_complete_degree:
                        print(i)
                        print(complete_degree)
            print(total_complete_degrees)
            path = '../DataOutput/' + model + '/threshold_analysis.xlsx'
            create_directory(path)
            create_workbook(path)
            save_to_excel_2d(total_complete_degrees, ['Threshold', 'Total Complete Degree'], path, result_index, 1, 2)
