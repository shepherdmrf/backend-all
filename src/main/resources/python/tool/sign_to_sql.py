from openpyxl import load_workbook
import pymysql

file = "../MultifacetedModeling/DataInput/data-85.xlsx"
wb = load_workbook(file)
data_sheet = wb.worksheets[0]
first_row_list = [str(cell.value) for cell in data_sheet[1][:45]]

conn = pymysql.connect(host='localhost', user='root', passwd='123456', db='feature_table')
cursor = conn.cursor()

try:
    cursor.execute("DELETE FROM sign_table")
    cursor.execute("ALTER TABLE sign_table AUTO_INCREMENT = 0")
    conn.commit()
    print("表已清空，AUTO_INCREMENT 重置为 0。")
except Exception as e:
    print(f"重置表时出错: {e}")

try:
    # 手动插入数据，ID 从 0 开始
    for index, value in enumerate(first_row_list):
        s = "INSERT INTO sign_table (value, name) VALUES (%s, %s);"
        cursor.execute(s, (index, value))  # 使用 index 作为 ID
    conn.commit()
    print("插入表成功")
except Exception as e:
    print(f"插入表出错: {e}")
finally:
    cursor.close()
    conn.close()
