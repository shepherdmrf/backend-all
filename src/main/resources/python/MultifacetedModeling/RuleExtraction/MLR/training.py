import pandas as pd
import numpy as np
from sklearn import preprocessing
from Printting import create_workbook, save_to_excel_1d
from ModelsCV import predictors
import warnings

warnings.filterwarnings('ignore')


def load_data(filename, sheet_name='data'):
    data_frame = pd.DataFrame(pd.read_excel(filename, sheet_name=sheet_name))
    data_set = data_frame.values
    return data_set


# 利用最佳特征团训练模型，以获得特征重要度
if __name__ == '__main__':
    load_data_wb = '../../DataInput/data-85.xlsx'
    data = load_data(load_data_wb, sheet_name='data')[:,
           [0, 3, 7, 8, 9, 11, 12, 13, 16, 17, 18, 21, 22, 26, 27, 28, 29, 31, 35, 37, 42, 44, 45]]
    train_x = data[:, :-1]
    train_y = data[:, -1]

    scaler = preprocessing.StandardScaler()
    train_x_pro = scaler.fit_transform(train_x)
    model = predictors(train_x_pro, train_y, type='MLR')
    cv_rmse = np.abs(model.best_score_)
    best_model = model.best_estimator_
    feature_importance = np.append(best_model.coef_, best_model.intercept_)

    print(cv_rmse)

    wb = './training_result.xlsx'
    create_workbook(wb)
    save_to_excel_1d(feature_importance, 'Importance', wb, 'data-3', 1, 2)
