import numpy as np
import copy
import warnings
from CodesForValidation.Training import Data, load_data, FS_test
warnings.filterwarnings('ignore')

# 基于训练集+验证集的所有样本，执行不带核特征的NCOR-FS，并在测试集上进行预测

# NASICON数据集的KNCOR
knowledge_driven_NCOR = [[[26, 27], [28]], [[0], [38]], [[1], [39]], [[2], [40]], [[4, 5], [42]], [[15, 16], [43]],
                         [[6, 7, 4, 5], [8]], [[9, 10, 4, 5], [11]], [[12, 13, 4, 5], [14]],
                         [[17, 18, 15, 16], [19]], [[20, 21, 15, 16], [22]], [[23, 24, 15, 16], [25]],
                         [[34, 35], [36], [37]], [[38, 39, 40], [41]]]

if __name__ == '__main__':
    load_data_wb = '../DataInput/data-85.xlsx'
    load_indices_wb = '../DataInput/data-85-split.xlsx'
    all_data = load_data(load_data_wb, sheet_name='data')
    train_indices = load_data(load_indices_wb, sheet_name='Train').T
    validation_indices = load_data(load_indices_wb, sheet_name='Validation').T
    all_train_indices = np.hstack((train_indices, validation_indices))
    test_indices = load_data(load_indices_wb, sheet_name='Test').T
    n_features = np.array(all_data).shape[1] - 1

    partitions, num = 10, 10    # 数据集划分次数，特征选择算法的重复执行次数
    population_size = 50    # 粒子群中粒子的总数
    max_steps = 50  # 最大迭代次数
    best_values = {'value1': 0, 'value2': 0.05}  # 迭代停止条件
    kernel = []

    KNCORs = copy.deepcopy(knowledge_driven_NCOR)
    models = ['GPR']
    for m in models:
        for i in range(partitions):
            train_data = Data(all_data[all_train_indices[i], :])
            new_tests = np.array(test_indices[i])
            test_data = Data(all_data[new_tests, :])
            dd_flag = True
            kd_flag = True

            wb = '../Results/OnAllTrain/DKNCOR/' + m + '.xlsx'
            sheet = 'result' + str(i)
            FS_test(train_data, test_data, kernel, KNCORs, num, population_size, max_steps, best_values,
                    m, dd_flag, kd_flag, wb, sheet, i)
