import numpy as np
import pandas as pd
from Printting import create_directory, create_workbook, save_to_excel_2d, save_to_excel_1d

# 步骤六：计算特征决策表分组的平均预测性能

if __name__ == '__main__':
    models = ['MLR', 'SVR', 'KNN', 'GPR']
    for m in models:
        wb = '../Results/OnTrain/DKNCOR/' + m + '.xlsx'
        for i in range(10):
            result_index = 'result' + str(i)
            all_data = pd.DataFrame(pd.read_excel(wb, sheet_name=result_index)).values
            indicators = all_data[:, [3, 4, 5]]
            cs = all_data[:, 7]
            inds = [np.array([0, 0, 0]), np.array([0, 0, 0]), np.array([0, 0, 0]), np.array([0, 0, 0])]
            nums = [0, 0, 0, 0]
            for ins, c in zip(indicators, cs):
                # print(ins)
                inds[c] = inds[c] + ins
                nums[c] += 1
            for i in range(4):
                inds[i] = inds[i] / np.full(3, nums[i])
            print(inds)
            save = '../DataOutput/' + m + '/avg_classes.xlsx'
            create_directory(save)
            create_workbook(save)
            save_to_excel_2d(inds, ['CV RMSE', 'RMSE', 'R2'], save, result_index, 1, 2)
            save_to_excel_1d(nums, 'Num', save, result_index, 4, 2)
