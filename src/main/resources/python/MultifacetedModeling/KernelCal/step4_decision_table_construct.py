import io
import ast
import os
import sys
current_dir = os.path.dirname(os.path.abspath(__file__))
parent_dir = os.path.dirname(current_dir)  # 上一层目录
sys.path.append(parent_dir)
import numpy as np
import pandas as pd
from Printting import create_workbook, save_to_excel_1d, save_to_excel_2d
from KernelCal.step3_threshold_analysis import binary_code

# 步骤四：根据步骤三确定的阈值构建“最简决策表”

if __name__ == '__main__':
    models = ['MLR', 'SVR', 'KNN', 'GPR']
    for model in models:
        for i in range(1):
            result_index = 'result' + str(i)

            load_data_wb = '../Results/OnTrain/DKNCOR/' + model + '.xlsx'
            all_data = pd.DataFrame(pd.read_excel(load_data_wb, sheet_name=result_index))
            conditions = all_data['Features']
            cs = all_data['d_class']

            avg_data = pd.DataFrame(pd.read_excel('../DataOutput/' + model + '/frequency.xlsx', sheet_name=result_index)).values

            number = 45
            binary_conditions = []
            for x in conditions:
                x = x.strip('[').strip(']')
                array_x = np.array(x.split(',')).astype(np.int8)
                binary_x = binary_code(array_x, number)
                binary_conditions.append(binary_x)

            for x, c in zip(binary_conditions, cs):
                for i,  f in zip(range(45), avg_data[c]):
                    if x[i] == 1 and f < 0.43:      # 步骤三确定的阈值（不同的模型不同）
                        x[i] = 0

            path = '../DataOutput/' + model + '/decision_table.xlsx'
            create_workbook(path)
            save_to_excel_2d(binary_conditions, range(45), path, result_index, 1, 2)
            save_to_excel_1d(cs, 'd_class', path, result_index, 46, 2)

