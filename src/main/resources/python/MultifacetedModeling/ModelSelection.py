import pandas as pd
import numpy as np
from sklearn import preprocessing
from Printting import create_workbook, save_to_excel_1d, save_to_excel_2d
from sklearn.metrics import mean_squared_error, r2_score
from ModelsCV import predictors
import warnings
warnings.filterwarnings('ignore')


# Loading data
def load_data(filename, sheet_name='data'):
    data_frame = pd.DataFrame(pd.read_excel(filename, sheet_name=sheet_name))
    data_set = data_frame.values
    return data_set


# model selection
def model_selection(data, trains, tests, models, wb_name, p):
    for m in models:
        cv_rmses, test_rmses, test_mapes, test_r2s, coefs, inters, feature_importance = [], [], [], [], [], [], []

        create_workbook(wb_name)

        for i in range(p):
            new_trains = np.array(trains[i][~ np.isnan(trains[i])]).astype('int8')
            train_x = data[new_trains, :-1]
            train_y = data[new_trains, -1]
            new_tests = np.array(tests[i][~ np.isnan(tests[i])]).astype('int8')
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
            mape = sum(abs((predict_y - test_y) / test_y)) / len(test_y)
            r2 = r2_score(test_y, predict_y)
            cv_rmses.append(avg_rmse)
            test_rmses.append(rmse)
            test_mapes.append(mape)
            test_r2s.append(r2)
            print('CV RMSE: %.5f    Test RMSE: %.5f    Test MAPE: %.5f    Test R2: %.5f\n' % (avg_rmse, rmse, mape, r2))

        # '''
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
        save_to_excel_1d(test_mapes, 'Test MAPE', wb_name, sheet_name, 3, 2)
        save_to_excel_1d(test_r2s, 'Test R2', wb_name, sheet_name, 4, 2)
        # '''

        avg_cv_rmses = np.average(cv_rmses)
        avg_test_rmses = np.average(test_rmses)
        avg_test_mapes = np.average(test_mapes)
        avg_test_r2s = np.average(test_r2s)
        avg_performances = [avg_cv_rmses, avg_test_rmses, avg_test_mapes, avg_test_r2s]
        std_cv_rmses = np.std(cv_rmses)
        std_test_rmses = np.std(test_rmses)
        std_test_mapes = np.std(test_mapes)
        std_test_r2s = np.std(test_r2s)
        std_performances = [std_cv_rmses, std_test_rmses, std_test_mapes, std_test_r2s]
        return avg_performances, std_performances


if __name__ == '__main__':
    load_data_wb = './DataInput/data-85.xlsx'
    load_indices_wb = './DataInput/data-85-split.xlsx'
    all_data = load_data(load_data_wb, sheet_name='data')
    train_indices = load_data(load_indices_wb, sheet_name='Train').T
    validation_indices = load_data(load_indices_wb, sheet_name='Validation').T
    test_indices = load_data(load_indices_wb, sheet_name='Test').T

    model_name = ['MLR']
    execute_times = 5
    avgs, stds = [], []

    array_x = [5, 12, 23, 27, 30, 36, 39, 40]
    wb = './DataOutput/' + model_name[0] + '/kernel_features_prediction.xlsx'
    all_train_indices = np.hstack((train_indices, validation_indices))
    avg_performance, std_performance = model_selection(all_data[:, np.append(array_x, 45)], all_train_indices,
                                                       test_indices, model_name, wb, execute_times)