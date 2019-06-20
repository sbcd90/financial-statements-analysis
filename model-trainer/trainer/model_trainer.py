import pandas as pd

df = pd.read_csv("../files/autompg-dataset/auto-mpg.csv")
print(df.head())

df = df.drop("name", axis=1)