import pandas as pd
import numpy as np
from Printting import create_workbook, save_to_excel_2d

# 计算最优特征集合中的两两特征之间的相关性

if __name__ == '__main__':
    data = pd.DataFrame(pd.read_excel('./data.xlsx', sheet_name='data')).values[1:, 3:].T
    print(data)
    all_coefs = []

    for x in data:
        coefs = []
        for y in data:
            temp = [[], []]
            temp[0] = list(x)
            temp[1] = list(y)
            pcc_coef = np.corrcoef(temp)[0, 1]
            coefs.append(pcc_coef)
        all_coefs.append(coefs)

    path = './correlation_coef.xlsx'
    create_workbook(path)
    save_to_excel_2d(all_coefs, range(23), path, 'all_data', 1, 2)