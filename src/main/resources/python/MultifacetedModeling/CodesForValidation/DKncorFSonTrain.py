import os
import sys
import warnings
import io
import ast
current_dir = os.path.dirname(os.path.abspath(__file__))
parent_dir = os.path.dirname(current_dir)  # 上一层目录
sys.path.append(parent_dir)
import numpy as np
import copy
import warnings
from Training import Data, load_data, FS_test

warnings.filterwarnings('ignore')
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

def clear_folder(folder_path):
    # 确保路径存在且是文件夹
    if not os.path.exists(folder_path):
        print(f"Folder {folder_path} does not exist.")
        return
    if not os.path.isdir(folder_path):
        print(f"{folder_path} is not a folder.")
        return

    # 遍历文件夹中的所有内容并删除
    for item in os.listdir(folder_path):
        item_path = os.path.join(folder_path, item)
        try:
            if os.path.isfile(item_path) or os.path.islink(item_path):  # 删除文件或符号链接
                os.unlink(item_path)
            elif os.path.isdir(item_path):  # 删除子文件夹
                shutil.rmtree(item_path)
        except Exception as e:
            print(f"Failed to delete {item_path}. Reason: {e}")

    print(f"Folder {folder_path} has been cleared.")

# 基于训练集的所有样本，执行不带核特征的NCOR-FS，并在验证集上进行预测，以构建特征决策表

# NASICON数据集的KNCOR
knowledge_driven_NCOR = [[[26, 27], [28]], [[0], [38]], [[1], [39]], [[2], [40]], [[4, 5], [42]], [[15, 16], [43]],
                         [[6, 7, 4, 5], [8]], [[9, 10, 4, 5], [11]], [[12, 13, 4, 5], [14]],
                         [[17, 18, 15, 16], [19]], [[20, 21, 15, 16], [22]], [[23, 24, 15, 16], [25]],
                         [[34, 35], [36], [37]], [[38, 39, 40], [41]]]

if __name__ == '__main__':
    if len(sys.argv) > 1:
            parameter = ast.literal_eval(sys.argv[1])
    load_data_wb = '../DataInput/uploadedFile.xlsx'
    load_indices_wb = '../DataInput/uploadedFile-split.xlsx'
    all_data = load_data(load_data_wb, sheet_name='data')
    train_indices = load_data(load_indices_wb, sheet_name='Train').astype(int).T
    test_indices = load_data(load_indices_wb, sheet_name='Validation').astype(int).T
    n_features = np.array(all_data).shape[1] - 1

    partitions, num = parameter[0], parameter[1]  # 数据集划分次数，特征选择算法的重复执行次数
    population_size = 50  # 粒子群中粒子的总数
    max_steps = parameter[2]  # 最大迭代次数
    best_values = {'value1': 0, 'value2': 0.05}  # 迭代停止条件
    kernel = []

    KNCORs = copy.deepcopy(knowledge_driven_NCOR)
    models = ['MLR', 'SVR', 'KNN', 'GPR']
    try:
        clear_folder("../Results/OnTrain/DKNCOR/")
        print("原文件已清空")
    except Exception as e:
        print(f"清空文件夹时发生错误: {e}")

    for m in models:
        for i in range(1):
            train_data = Data(all_data[train_indices[i], :])
            new_tests = np.array(test_indices[i])
            test_data = Data(all_data[new_tests, :])
            dd_flag = True
            kd_flag = True

            wb = '../Results/OnTrain/DKNCOR/' + m + '.xlsx'
            sheet = 'result' + str(i)
            FS_test(train_data, test_data, kernel, KNCORs, num, population_size, max_steps, best_values,
                    m, dd_flag, kd_flag, wb, sheet, i)
