import warnings
import numpy as np
import sys
import ast
import io
import os
import shutil
from Training import load_data, model_selection

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

# 选择“关键领域核特征”

if __name__ == '__main__':
    if len(sys.argv) > 1:
        kernels = ast.literal_eval(sys.argv[1])
        print(kernels)
    load_data_wb = '../DataInput/data-85.xlsx'
    load_indices_wb = '../DataInput/data-85-split.xlsx'
    all_data = load_data(load_data_wb, sheet_name='data')
    train_indices = load_data(load_indices_wb, sheet_name='Train').T
    validation_indices = load_data(load_indices_wb, sheet_name='Validation').T
    test_indices = load_data(load_indices_wb, sheet_name='Test').T
    all_train_indices = np.hstack((train_indices, validation_indices))

    partitions = 10

    model_names = [['MLR'], ['SVR'], ['KNN'], ['GPR']]
    for name in model_names:
        clear_folder('../Results/OnTrain/KnowledgeKernel/' + name[0])
        for i, x in enumerate(kernels):
            wb = '../Results/OnTrain/KnowledgeKernel/' + name[0] + '/' + str(i) + '.xlsx'
            dims = np.hstack((x, [45]))
            model_selection(all_data[:, dims], all_train_indices, test_indices, name, wb, partitions)
    print("测试成功")
