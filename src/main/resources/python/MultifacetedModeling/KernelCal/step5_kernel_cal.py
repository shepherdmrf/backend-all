import numpy as np
import pandas as pd
import copy
from Printting import create_directory, create_workbook, save_to_excel_1d
from KernelCal.step3_threshold_analysis import binary_code, cal_ind, cal_positive_region

# 步骤五：计算核特征

if __name__ == '__main__':
    models = ['MLR', 'SVR', 'KNN', 'GPR']
    # models = ['GPR']
    for model in models:
        all_kernels = []
        for i in range(10):
            result_index = 'result' + str(i)

            # 获取最优约简阈值
            load_wb = '../DataOutput/' + model + '/threshold_analysis.xlsx'
            # 0.1~0.6
            thresholds = pd.DataFrame(pd.read_excel(load_wb, sheet_name=result_index)).values[:52, :]
            t = 0
            for i in range(1, thresholds.shape[0]):
                if thresholds[i-1][1] == 1 and thresholds[i][1] < 1:
                    t = thresholds[i-1][0]
            if t == 0:
                t = thresholds[-1][0]

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
                for i, f in zip(range(45), avg_data[c]):
                    if x[i] == 1 and f < t:
                        x[i] = 0

            conditions = copy.deepcopy(binary_conditions)
            decisions = copy.deepcopy(cs)
            retained_conditions = list(range(45))

            global_inds_conditions = cal_ind(conditions)
            inds_decisions = cal_ind(decisions)
            global_positive_regions = cal_positive_region(global_inds_conditions, inds_decisions)
            total_complete_degree = len(global_positive_regions) / len(decisions)
            # print(total_complete_degree)

            kernels = []

            for i in range(45):
                retained_conditions = np.setdiff1d(range(45), i)
                new_binary_conditions = np.array(conditions)[:, retained_conditions]
                inds_conditions = cal_ind(new_binary_conditions)

                positive_regions = cal_positive_region(inds_conditions, inds_decisions)
                complete_degree = len(positive_regions) / len(decisions)

                if complete_degree < total_complete_degree:
                    kernels.append(i)
                    # print(i)
                    # print(complete_degree)
            all_kernels.append(kernels)
        save = '../DataOutput/' + model + '/kernels.xlsx'
        create_directory(save)
        create_workbook(save)
        save_to_excel_1d(all_kernels, 'Kernels', save, 'data', 1, 2)