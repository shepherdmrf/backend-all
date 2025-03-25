import pandas as pd
import numpy as np
from matplotlib import pyplot as plt
from sklearn import preprocessing
import warnings
import os
import sys
import urllib.parse

current_dir = os.path.dirname(os.path.abspath(__file__))
parent_dir = os.path.dirname(current_dir)
parent_dir1 = os.path.dirname(parent_dir)
sys.path.append(parent_dir1)

from Printting import create_workbook, save_to_excel_1d
from ModelsCV import predictors

warnings.filterwarnings('ignore')


def load_data(filename, sheet_name='data'):
    data_frame = pd.DataFrame(pd.read_excel(filename, sheet_name=sheet_name))
    data_set = data_frame.values
    return data_set


# 利用最佳特征团训练模型，以获得支持向量
if __name__ == '__main__':
    load_data_wb = '../../DataInput/data-85.xlsx'

    features1 = sys.argv[1]
    features1 = urllib.parse.unquote(features1)
    print(features1)
    features1 = [int(item.lstrip('+')) for item in features1.strip('[]').split(',')]
    data = load_data(load_data_wb, sheet_name='data')[:,features1]

    # data = load_data(load_data_wb, sheet_name='data')[:, [1, 2, 5, 11, 12, 13, 14, 16, 17, 18, 20, 23, 25, 26, 31, 32, 33, 34, 37, 38, 43, 45]]

    # [1, 2, 10, 12, 15, 16, 19, 21, 22, 23]

    train_x = data[:, :-1]
    train_y = data[:, -1]

    scaler = preprocessing.StandardScaler()
    train_x_pro = scaler.fit_transform(train_x)
    model = predictors(train_x_pro, train_y, type='GPR')
    cv_rmse = np.abs(model.best_score_)
    best_model = model.best_estimator_
    # print(best_model.support_)
    print(cv_rmse)

    predict_y, std = best_model.predict(train_x_pro, return_std=True)
    path = './data.xlsx'
    path2 = './std.xlsx'
    create_workbook(path)
    create_workbook(path2)
    save_to_excel_1d(predict_y, 'pred value', path, 'prediction', 1, 2)
    save_to_excel_1d(std, 'std value', path2, 'std', 1, 2)

    source_path = './data-85.xlsx'
    source_df = pd.read_excel(source_path)
    last_column = source_df.iloc[0:, -1]  # 获取最后一列

    # 合并为一个 DataFrame
    combined_df = pd.DataFrame({
        'BVSE energy barrier': last_column,
        'pred value': predict_y,
        'std value': std
    })

    # 保存到一个新的 Excel 文件
    output_path = './pred.xlsx'
    combined_df.to_excel(output_path, index=False)

    print(output_path)

