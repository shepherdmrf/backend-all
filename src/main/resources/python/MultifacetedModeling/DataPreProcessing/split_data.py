import os
import sys
import warnings
import io
import ast
current_dir = os.path.dirname(os.path.abspath(__file__))
parent_dir = os.path.dirname(current_dir)  # 上一层目录
sys.path.append(parent_dir)
import numpy as np
import pandas as pd
from sklearn.model_selection import train_test_split
from Printting import create_directory, create_workbook, save_to_excel_1d, save_to_excel_2d

warnings.filterwarnings('ignore')
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')
sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')

if __name__ == '__main__':
    if len(sys.argv) > 1:
        parameter = ast.literal_eval(sys.argv[1])
    num=parameter[0]
    train_indices = []
    test_indices = []
    validation_indices=[]
    all_data = pd.DataFrame(pd.read_excel('../DataInput/uploadedFile.xlsx', sheet_name='data'))

    wb_name = '../DataInput/uploadedFile-split.xlsx'
    sheet_name = 'data'
    create_directory(wb_name)
    create_workbook(wb_name)
    for i in range(num):

       X_train, X_test, y_train, y_test = train_test_split(all_data.iloc[:, :-1], all_data.iloc[:, -1],
                                                                test_size=0.25, shuffle=True)
       X_train, X_validation, y_train, y_validation = train_test_split(X_train[:],y_train[:],test_size=0.3)
       train_indices.append(X_train.index.values)
       validation_indices.append(X_validation.index.values)
       test_indices.append(X_test.index.values)

    save_to_excel_2d(np.array(train_indices).transpose(), range(1, num+1), wb_name, 'Train', 1, 2)
    save_to_excel_2d(np.array(validation_indices).transpose(), range(1, num+1), wb_name, 'Validation', 1, 2)
    save_to_excel_2d(np.array(test_indices).transpose(), range(1, num+1), wb_name, 'Test', 1, 2)
