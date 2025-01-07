import pandas as pd
import numpy as np
from sklearn import preprocessing
import os
import sys

current_dir = os.path.dirname(os.path.abspath(__file__))
parent_dir = os.path.dirname(current_dir)
parent_dir = os.path.dirname(parent_dir)
sys.path.append(parent_dir)

from Printting import create_workbook, save_to_excel_1d
from ModelsCV import predictors
import warnings
import urllib.parse
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
    train_x = data[:, :-1]
    train_y = data[:, -1]

    scaler = preprocessing.StandardScaler()
    train_x_pro = scaler.fit_transform(train_x)
    model = predictors(train_x_pro, train_y, type='SVR')
    cv_rmse = np.abs(model.best_score_)
    best_model = model.best_estimator_
    print(best_model.support_)
    print(cv_rmse)

    predict_y = best_model.predict(train_x_pro)
    path = './data.xlsx'
    create_workbook(path)
    save_to_excel_1d(predict_y, 'pred value', path, 'prediction', 1, 2)
