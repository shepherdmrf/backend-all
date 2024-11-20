import warnings
import numpy as np
import sys
import io
from Training import load_data, model_selection
warnings.filterwarnings('ignore')
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')
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
        for i, x in enumerate(kernels):
            wb = '../Results/OnTrain/KnowledgeKernel/' + name[0] + '/' + str(i) + '.xlsx'
            dims = np.hstack((x, [45]))
            model_selection(all_data[:, dims], all_train_indices, test_indices, name, wb, partitions)
    print("测试成功")
