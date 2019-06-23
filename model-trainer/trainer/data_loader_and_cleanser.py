import numpy as np
import pandas as pd


class DataLoader:

    def load_data(self):
        df1 = pd.read_csv("../files/company_data1.csv")
        df2 = pd.read_csv("../files/company_data2.csv")
        df4 = pd.read_csv("../files/company_data4.csv")
        df5 = pd.read_csv("../files/company_data5.csv")
        df6 = pd.read_csv("../files/company_data6.csv")
        df7 = pd.read_csv("../files/company_data7.csv")
        df7 = pd.concat([df1, df2, df4, df5, df6, df7])
        return df7

    def load_data(self, path):
        df1 = pd.read_csv(path + "/company_data1.csv")
        df2 = pd.read_csv(path + "/company_data2.csv")
        df4 = pd.read_csv(path + "/company_data4.csv")
        df5 = pd.read_csv(path + "/company_data5.csv")
        df6 = pd.read_csv(path + "/company_data6.csv")
        df7 = pd.read_csv(path + "/company_data7.csv")
        df7 = pd.concat([df1, df2, df4, df5, df6, df7])
        return df7

    def load_data_by_category(self, category):
        df = self.load_data()
        return df[df["CompanyCategory"].str.contains(category)]

    def prepare_non_test_data(self, company, path=""):
        df = self.load_data(path)
        df = df[df["Ticker"] == company]
        df = df.drop("PercentChangeInRoe", axis=1)
        df = df.drop("PercentOfPositiveWords", axis=1)
        df = df.drop("PercentOfNegativeWords", axis=1)
        df = df.drop("PercentOfLitiguousWords", axis=1)
        df = df.drop("PercentOfConstrainingWords", axis=1)
        df = df.drop("PercentOfStrongModalWords", axis=1)
        df = df.drop("PercentOfWeakModalWords", axis=1)
        df = df.drop("PercentOfUncertaintyWords", axis=1)
        return df

    def prepare_test_data(self, company, path=""):
        df = self.load_data(path)
        df = df[df["Ticker"] == company]
        df = df.drop("Ticker", axis=1)
        df = df.drop("CompanyName", axis=1)
        df = df.drop("CompanyCategory", axis=1)
        df = df.drop("Date", axis=1)
        df = df.drop("TtmNetIncome", axis=1)
        df = df.drop("ShareholdersEquity", axis=1)

        df["PreviousRoe"] = df["PreviousRoe"].astype(float)
        df["PercentChangeInRoe"] = df["PercentChangeInRoe"].astype(float)
        df["PercentOfPositiveWords"] = df["PercentOfPositiveWords"].astype(float)
        df["PercentOfNegativeWords"] = df["PercentOfNegativeWords"].astype(float)
        df["PercentOfLitiguousWords"] = df["PercentOfLitiguousWords"].astype(float)
        df["PercentOfConstrainingWords"] = df["PercentOfConstrainingWords"].astype(float)
        df["PercentOfStrongModalWords"] = df["PercentOfStrongModalWords"].astype(float)
        df["PercentOfWeakModalWords"] = df["PercentOfWeakModalWords"].astype(float)
        df["PercentOfUncertaintyWords"] = df["PercentOfUncertaintyWords"].astype(float)
        return df

    def prepare_train_data(self, category):
        df = self.load_data_by_category(category)

        df = df.drop("Ticker", axis=1)
        df = df.drop("CompanyName", axis=1)
        df = df.drop("CompanyCategory", axis=1)
        df = df.drop("Date", axis=1)
        df = df.drop("TtmNetIncome", axis=1)
        df = df.drop("ShareholdersEquity", axis=1)
        # df["origin"] = df["origin"].replace({1: 'america', 2: 'europe', 3: 'asia'})
        # df = pd.get_dummies(df, columns=["origin"])
        print(df.head())

        # df = df[df["PreviousRoe"].str.contains("$")]
        df = df.replace("#NAME?", np.nan)
        df = df.replace(np.inf, np.nan)
        df = df.replace(-np.inf, np.nan)
        df = df.replace("", np.nan)
        df = df.dropna()

        df["PreviousRoe"] = df["PreviousRoe"].astype(float)
        df["PercentChangeInRoe"] = df["PercentChangeInRoe"].astype(float)
        df["PercentOfPositiveWords"] = df["PercentOfPositiveWords"].astype(float)
        df["PercentOfNegativeWords"] = df["PercentOfNegativeWords"].astype(float)
        df["PercentOfLitiguousWords"] = df["PercentOfLitiguousWords"].astype(float)
        df["PercentOfConstrainingWords"] = df["PercentOfConstrainingWords"].astype(float)
        df["PercentOfStrongModalWords"] = df["PercentOfStrongModalWords"].astype(float)
        df["PercentOfWeakModalWords"] = df["PercentOfWeakModalWords"].astype(float)
        df["PercentOfUncertaintyWords"] = df["PercentOfUncertaintyWords"].astype(float)
        df = df[(df["PreviousRoe"] < 100000.0) & (df["PreviousRoe"] > -100000.0)]
        df = df[(df["PercentChangeInRoe"] < 100000.0) & (df["PercentChangeInRoe"] > -100000.0)]

        y_train = df.PercentChangeInRoe.values

        return df, y_train