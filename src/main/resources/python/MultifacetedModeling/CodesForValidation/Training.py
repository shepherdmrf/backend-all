import pandas as pd
import numpy as np
from sklearn import preprocessing
from sklearn.metrics import mean_squared_error, r2_score
import sys
import os
current_dir = os.path.dirname(os.path.abspath(__file__))
parent_dir = os.path.dirname(current_dir)  # 上一层目录

from Printting import create_directory, create_workbook, save_to_excel_1d
from FeatureSelection.DncorCal import DataDrivenNCOR
from FeatureSelection.MBPSO import BPSO
from ModelsCV import predictors
import time
import copy
import warnings
warnings.filterwarnings('ignore')


def my_loss_func(y_true, y_pred):
    value = np.sqrt(mean_squared_error(y_true, y_pred))
    return value


def load_data(filename, sheet_name='data'):
    data_frame = pd.DataFrame(pd.read_excel(filename, sheet_name=sheet_name))
    data_set = data_frame.values
    return data_set


class Data(object):
    def __init__(self, data):
        self.count = np.array(data).shape[0]
        self.dim = np.array(data).shape[1] - 1
        self.X = data[:, :self.dim]
        self.Y = data[:, -1]

    def data_pro_precessing(self, scaler=None):
        if not scaler:
            scaler = preprocessing.StandardScaler()
            self.X = scaler.fit_transform(self.X)
        else:
            self.X = scaler.transform(self.X)
        return scaler


# 剔除核特征后更新NCOR
def change_rules(ker, original_rules):
    temp = copy.deepcopy(ker)
    rule = copy.deepcopy(original_rules)
    if len(temp):
        for i in range(len(temp)):
            delete = []
            for j in range(len(rule)):
                remove = []
                for k in range(len(rule[j])):
                    if temp[i] in rule[j][k]:
                        rule[j][k] = list(rule[j][k])
                        if len(rule[j][k]) == 1 and len(rule[j]) == 2:
                            delete.append(j)
                            delete = list(set(delete))
                        else:
                            rule[j][k].remove(temp[i])
                            if len(rule[j][k]) == 0:
                                remove.append(k)
                    for t in range(len(rule[j][k])):
                        if rule[j][k][t] > temp[i]:
                            rule[j][k][t] = rule[j][k][t] - 1
                for t in range(len(remove)):
                    del rule[j][remove[t]]
            for index, value in enumerate(temp):
                if value > temp[i]:
                    temp[index] = temp[index] - 1
            for j in range(len(delete)):
                del rule[delete[j]-j]
    return rule


# 从表格中获取所有的特征子集
def get_all_feature_sets(path, sheet_name):
    data = pd.read_excel(path, sheet_name=sheet_name)
    num_feature_sets = data.shape[0]  # Number of feature sets
    final_results = data.iloc[:num_feature_sets, 0]  # all feature sets
    return num_feature_sets, final_results


# 机器学习模型在特定特征子集上、特定数据划分下的预测性能
def leave_out_checkout(train, test, features, model_name):
    model = predictors(train.X[:, features], train.Y, type=model_name)
    predict_y = model.best_estimator_.predict(test.X[:, features])
    predict_y = np.array(predict_y).flatten()
    rmse = np.sqrt(mean_squared_error(test.Y, predict_y))
    mape = sum(abs((predict_y - test.Y) / test.Y)) / len(test.Y)
    r2 = r2_score(test.Y, predict_y)
    # print("RMSE: %.5f    MAPE: %.5f    R2: %.5f    " % (rmse, mape, r2))
    print("RMSE: %.5f    R2: %.5f    " % (rmse, r2))
    return rmse, mape, r2, []


def FS_test(data_train, data_test, ker, ncors, total, pop_size, max_step, threshold, model_name,
            d_flag, dk_flag, wb_name, sheet_name, ith):

    features, length, value1s, value2s = [], [], [], []
    rmses, mapes, r2s, times = [], [], [], []
    DNCORs = []
    KNCORs = copy.deepcopy(ncors)
    raw_features = range(data_train.dim)

    train = copy.deepcopy(data_train)
    train_pro = copy.deepcopy(data_train)
    test = copy.deepcopy(data_test)
    scaler = train_pro.data_pro_precessing()
    test.data_pro_precessing(scaler)

    if d_flag and dk_flag:
        data = np.column_stack((train.X, train.Y))
        DNCOR = DataDrivenNCOR(data, [], 0.90, 200, '../DataInput/data-85.xlsx')
        # path = '../Results/OnTrain/DataDrivenNCOR/correlation-values.xlsx'
        # result_wb = '../Results/OnTrain/DataDrivenNCOR/NCORs.xlsx'
        path = '../Results/OnAllTrain/DataDrivenNCOR/correlation-values.xlsx'
        result_wb = '../Results/OnAllTrain/DataDrivenNCOR/NCORs.xlsx'
        result_sheet = str(ith) + ' th'
        highly_features, temp = DNCOR.feature_selection(path, result_wb, result_sheet)
        DNCORs = cal_NCOR(highly_features)
        DNCORs = change_rules(ker, DNCORs)

    KNCORs = change_rules(ker, KNCORs)
    NCORs = {'dncors': DNCORs, 'kncors': KNCORs}
    print(NCORs)

    for i in range(total):
        print(str(i) + 'th Execution:')
        bpso = BPSO(train, pop_size, max_step, 2, threshold, ker, NCORs, model_name)
        strat_time = time.time()
        best, feature_set = bpso.evolve()
        revised_feature_set = list(
            filter(None, [value if index in feature_set else None for index, value in enumerate(raw_features)]))
        if 0 in feature_set:
            revised_feature_set.insert(0, 0)
        print('Best Feature Set: ', revised_feature_set)
        end_time = time.time()
        times.append(end_time - strat_time)

        rmse, mape, r2, coef = leave_out_checkout(train_pro, test, feature_set, model_name)
        rmses.append(rmse)
        mapes.append(mape)
        r2s.append(r2)
        features.append(revised_feature_set)
        length.append(len(revised_feature_set))
        value1s.append(best['value1'])
        value2s.append(best['value2'])
        print('CV RMSE：%.5f' % best['value2'])
        # value2s.append(best['value2'] - len(revised_feature_set) / 234)
        # print('CV RMSE：%.5f' % (best['value2'] - len(revised_feature_set) / 234))

    create_directory(wb_name)
    create_workbook(wb_name)
    save_to_excel_1d(features, 'Features', wb_name, sheet_name, 1, 2)
    save_to_excel_1d(length, 'Length', wb_name, sheet_name, 2, 2)
    save_to_excel_1d(value1s, 'Score', wb_name, sheet_name, 3, 2)
    save_to_excel_1d(value2s, 'CV RMSE', wb_name, sheet_name, 4, 2)
    save_to_excel_1d(rmses, 'RMSE', wb_name, sheet_name, 5, 2)
    # save_to_excel_1d(mapes, 'MAPE', wb_name, sheet_name, 6, 2)
    save_to_excel_1d(r2s, 'R2', wb_name, sheet_name, 6, 2)
    save_to_excel_1d(times, 'Time', wb_name, sheet_name, 7, 2)


# 将高度相关特征转换为NCOR的形式
def cal_NCOR(old_relevance_rules):
    relevance_rules = []
    for index, value in enumerate(old_relevance_rules):
        temp = []
        for x in value:
            temp.append([x])
        relevance_rules.append(temp)
    return relevance_rules


def model_selection(data, trains, tests, models, wb_name, p):
    for m in models:
        cv_rmses, test_rmses, test_mapes, test_r2s, coefs, inters, feature_importance = [], [], [], [], [], [], []

        create_directory(wb_name)
        create_workbook(wb_name)

        for i in range(p):
            new_trains = np.array(trains[i])
            train_x = data[new_trains, :-1]
            train_y = data[new_trains, -1]
            new_tests = np.array(tests[i])
            test_x = data[new_tests, :-1]
            test_y = data[new_tests, -1]

            scaler = preprocessing.StandardScaler()
            X_train_pro = scaler.fit_transform(train_x)
            X_test_pro = scaler.transform(test_x)

            model = predictors(X_train_pro, train_y, type=m)
            avg_rmse = np.abs(model.best_score_)
            best_model = model.best_estimator_
            best_model.fit(X_train_pro, train_y)
            predict_y = best_model.predict(X_test_pro)

            rmse = np.sqrt(mean_squared_error(test_y, predict_y))
            # mape = sum(abs((predict_y -test_y) / test_y)) / len(test_y)
            # mae = sum(abs((predict_y - test_y))) / len(test_y)
            r2 = r2_score(test_y, predict_y)
            cv_rmses.append(avg_rmse)
            test_rmses.append(rmse)
            # test_mapes.append(mape)
            test_r2s.append(r2)
            # print('CV MAPE: %.5f    Test RMSE: %.5f    Test MAE: %.5f    Test R2: %.5f\n' % (avg_rmse, rmse, mae, r2))
            print('CV RMSE: %.5f    Test RMSE: %.5f    Test R2: %.5f\n' % (avg_rmse, rmse, r2))

            save_to_excel_1d(test_y, str(i) + 'th', wb_name, m + '-values', i + 1, 2)
            save_to_excel_1d(predict_y, str(i) + 'th', wb_name, m + '-predicts', i + 1, 2)

            if m == 'RF':
                feature_importance = best_model.feature_importances_
            if m == 'LASSO':
                feature_importance = np.append(best_model.coef_, best_model.intercept_)
            if len(feature_importance) != 0:
                save_to_excel_1d(feature_importance, str(i) + ' th', wb_name, m + '-importance', i + 1, 2)

        sheet_name = m + '-' + str(10)
        save_to_excel_1d(cv_rmses, 'CV RMSE', wb_name, sheet_name, 1, 2)
        save_to_excel_1d(test_rmses, 'Test RMSE', wb_name, sheet_name, 2, 2)
        # save_to_excel_1d(test_mapes, 'Test MAPE', wb_name, sheet_name, 3, 2)
        save_to_excel_1d(test_r2s, 'Test R2', wb_name, sheet_name, 3, 2)

