import pandas as pd
import numpy as np
from matplotlib import pyplot as plt
from sklearn import preprocessing
from Printting import create_workbook, save_to_excel_1d
from ModelsCV import predictors
import warnings
warnings.filterwarnings('ignore')

def load_data(filename, sheet_name='data'):
    data_frame = pd.DataFrame(pd.read_excel(filename, sheet_name=sheet_name))
    data_set = data_frame.values
    return data_set
plt.rcParams["font.sans-serif"] = ["Microsoft YaHei"]
plt.rcParams["font.size"] = 18
plt.rcParams["axes.unicode_minus"] = False
# 利用最佳特征团训练模型，以获得支持向量
if __name__ == '__main__':
    load_data_wb = 'pred.xlsx'
    data = load_data(load_data_wb, sheet_name='Sheet1')[:, [0, 1, 2]]
    data_odd = data[0::2, :]
    data_even = data[1::2, :]
    reversed_data_even = np.flipud(data_even)
    print(data_odd)
    print(reversed_data_even)
    print(len(data_odd))
    print(len(reversed_data_even))
    data = np.concatenate((data_odd, reversed_data_even), axis=0)
    data = pd.DataFrame(data, columns=['BVSE energy barrier', 'pred value', 'std value'])
    data.to_excel('new_data.xlsx', index=True)

