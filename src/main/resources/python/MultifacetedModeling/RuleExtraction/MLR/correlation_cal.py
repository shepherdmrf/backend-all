import pandas as pd
import os
import ast
import sys
import numpy as np
current_dir = os.path.dirname(os.path.abspath(__file__))
parent_dir = os.path.dirname(current_dir)  # 上一层目录
grandparent_dir = os.path.dirname(parent_dir)  # 祖父目录
sys.path.append(grandparent_dir)
from Printting import create_workbook, save_to_excel_2d

def keep_columns_and_save(input_file, output_file, columns_to_keep):
    df = pd.read_excel(input_file, header=None)
    selected_columns = df.iloc[:, columns_to_keep]
    # 创建一个ExcelWriter对象来设置sheet名称
    with pd.ExcelWriter(output_file) as writer:
        selected_columns.to_excel(
            writer,
            sheet_name='data',  # 设置工作表名称为"data"
            index=False,
            header=False
        )

# 计算最优特征集合中的两两特征之间的相关性

if __name__ == '__main__':
    if len(sys.argv) > 1:
      kernels = ast.literal_eval(sys.argv[1])
    fileAddress=grandparent_dir+"/DataInput/uploadedFile.xlsx"
    kernels[0].append(45)
    keep_columns_and_save(fileAddress, "./choose.xlsx",kernels[0])
    data = pd.DataFrame(pd.read_excel("./choose.xlsx", sheet_name='data')).values[0:, 0:].T
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
    if os.path.exists(path):
        os.remove(path)
    create_workbook(path)
    num=len(all_coefs)
    print(num)
    save_to_excel_2d(all_coefs, range(num), path, 'all_data', 1, 2)