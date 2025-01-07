import numpy as np
import copy
import warnings
import sys
import ast
import io
import os
import json
from Training import Data, load_data, FS_test
import urllib.parse
from pathlib import Path
import subprocess

warnings.filterwarnings('ignore')
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')
# 基于训练集+验证集的所有样本，执行带“关键领域核特征+正数据核特征”的NCOR-FS，并在测试集上进行预测

# NASICON数据集的KNCOR
knowledge_driven_NCOR = [[[26, 27], [28]], [[0], [38]], [[1], [39]], [[2], [40]], [[4, 5], [42]], [[15, 16], [43]],
                         [[6, 7, 4, 5], [8]], [[9, 10, 4, 5], [11]], [[12, 13, 4, 5], [14]],
                         [[17, 18, 15, 16], [19]], [[20, 21, 15, 16], [22]], [[23, 24, 15, 16], [25]],
                         [[34, 35], [36], [37]], [[38, 39, 40], [41]]]

if __name__ == '__main__':
    load_data_wb = '../DataInput/uploadedFile.xlsx'
    load_indices_wb = '../DataInput/uploadedFile-split.xlsx'
    all_data = load_data(load_data_wb, sheet_name='data')
    train_indices = load_data(load_indices_wb, sheet_name='Train').T
    validation_indices = load_data(load_indices_wb, sheet_name='Validation').T
    all_train_indices = np.hstack((train_indices, validation_indices))
    test_indices = load_data(load_indices_wb, sheet_name='Test').T
    n_features = np.array(all_data).shape[1] - 1

    partitions, num = 1, 5  # 数据集划分次数，特征选择算法的重复执行次数
    population_size = 50  # 粒子群中粒子的总数
    max_steps = 1  # 最大迭代次数
    best_values = {'value1': 0, 'value2': 0.05}  # 迭代停止条件

    # GPR上的“关键领域核特征+正数据核特征”
    '''
    kernel = [[3, 5, 23], [2, 19, 20, 22, 39], [18], [8, 19, 20, 22, 23, 26, 28, 32], [20, 22],
              [2, 3, 4, 7, 12, 22, 25, 27, 36, 39], [2, 8, 17, 18], [10, 11, 23, 26, 28, 29, 30, 31, 36, 37]]
    '''
    # KNN上的“关键领域核特征+正数据核特征”
    '''
    kernel = [[8, 14, 25, 26, 27, 28, 32, 33, 35, 36, 37], [22, 44], [9, 10, 13, 19, 27, 32, 36, 37],
              [9, 11, 19], [25, 27, 28, 33, 34, 36], [3, 8, 9, 23, 25, 26, 27, 29, 35], [8, 19, 37],
              [6, 9, 11, 16, 23, 28, 33, 37], [11, 25, 30, 31, 32, 34, 44]]
    '''
    # SVR上的“关键领域核特征+正数据核特征”
    '''
    kernel = [[1, 3, 17, 21, 29, 33, 37], [8, 19, 36], [25, 37], [4, 9, 11, 12, 19, 32, 33, 37, 44],
              [13, 28, 29, 31, 37, 44], [3, 32, 33, 37, 44], [1, 6, 9, 28, 37],
              [3, 6, 13, 19, 25, 26, 37, 42], [28, 31, 37], [22, 33, 37]]
    '''
    # MLR上的“关键领域核特征+正数据核特征”
    # '''
#     kernel = [[5, 12, 23, 27, 30, 37, 39], [0, 10, 12, 15, 22, 29, 30, 36, 44],
#               [12, 17, 20, 21, 23, 37], [16, 29, 37, 42],
#               [5, 24, 28, 29, 37], [7, 9, 37], [2, 7, 10, 29, 37],
#               [6, 7, 10, 12, 13, 15, 17, 18, 21, 33, 37, 38, 42],
#               [22, 27, 34, 37, 42], [6, 31, 37, 43, 44]]
    # '''

    # 获取传递的参数
    json_features1 = sys.argv[1]
    json_features2 = sys.argv[2]
    model = sys.argv[3]
    print(model)

    json_features1 = urllib.parse.unquote(json_features1)
    json_features2 = urllib.parse.unquote(json_features2)

    # 将 JSON 字符串解析为 Python 对象
    features1 = json.loads(json_features1)
    features2 = json.loads(json_features2)

    # 打印检查传递的 JSON 字符串
    print(json_features1)  # 检查传递的 JSON 字符串
    print(json_features2)
    print('2222222')

    # 处理 features1 的 features 字符串，去除其中的 "+" 符号（仅去掉多余的加号）
    features1_values = []
    for f in features1:
        feature = f['positive_kernel'].replace('+', '')  # 去掉 "+"，而不是删除所有 "+"
        feature_values = json.loads(feature)      # 解析为列表
        features1_values.append(feature_values)

    # 处理 features2 中的 features（这里假设 features2 只有一个元素）
    additional_features = json.loads(features2[0]["features"].replace('+', ''))

    # 创建 kernel 列表，用来保存合并后的结果
    kernel = []

    # 将 features2 的 features 添加到每个 features1 的 features 中
    for feature1_values in features1_values:
        # 合并 features1 和 features2 中的 features
        merged_features = feature1_values + additional_features

        # 添加到最终的 kernel 列表中
        kernel.append(merged_features)

    # 打印合并后的结果
    print(kernel)
    KNCORs = copy.deepcopy(knowledge_driven_NCOR)
    models = model
    wb = '../Results/OnAllTrainKernel/DKNCOR/Know+Pos-Kernel/' + models + '.xlsx'
    subprocess.run(["del", "/f", "/q", wb], shell=True)

    for i in range(partitions):
        train_data = Data(all_data[all_train_indices[i], :])
        new_tests = np.array(test_indices[i])
        test_data = Data(all_data[new_tests, :])
        dd_flag = True
        kd_flag = True

        sheet = 'result' + str(i)
        FS_test(train_data, test_data, kernel[i], KNCORs, num, population_size, max_steps, best_values,
                models, dd_flag, kd_flag, wb, sheet, i)
