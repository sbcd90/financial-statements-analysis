from joblib import load
from trainer.data_loader_and_cleanser import DataLoader

if __name__ == "__main__":
    df = DataLoader().prepare_test_data("GOOGL", "../files")
    df_master = DataLoader().prepare_non_test_data("GOOGL", "../files")

    averaged_models_pkl = load("averaged_models.pkl")
    xgb_models_pkl = load("model_xgb.pkl")
    lgb_models_pkl = load("model_lgb.pkl")

    averaged_pred = averaged_models_pkl.predict(df.values)
    xgb_pred = xgb_models_pkl.predict(df)
    lgb_pred = lgb_models_pkl.predict(df)

    Averaged = 1 / 34.2450
    XGBoost = 1 / 121.6862
    LGBM = 1 / 172.3413
    Sum = Averaged + XGBoost + LGBM
    Averaged = Averaged / Sum
    XGBoost = XGBoost / Sum
    LGBM = LGBM / Sum
    print(Averaged, XGBoost, LGBM)

    ensemble = averaged_pred * Averaged + xgb_pred * XGBoost + lgb_pred * LGBM
    df["PercentChangeInRoe_pred"] = ensemble
    df["ActualRoe"] = ((df["PercentChangeInRoe"] + 100.0) * df["PreviousRoe"]) / 100.0
    df["ActualRoe_pred"] = ((df["PercentChangeInRoe_pred"] + 100.0) * df["PreviousRoe"]) / 100.0
    df_master = df_master.set_index("PreviousRoe").join(df.set_index("PreviousRoe"))
    df["ActualRoe"] = ((df["PercentChangeInRoe"] + 100.0) * df["PreviousRoe"]) / 100.0
    print(df_master.head())