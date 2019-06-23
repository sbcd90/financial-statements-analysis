import numpy as np
from joblib import dump, load
from sklearn.base import BaseEstimator, clone, RegressorMixin, TransformerMixin
from sklearn.ensemble import GradientBoostingRegressor
from sklearn.kernel_ridge import KernelRidge
from sklearn.linear_model import ElasticNet, Lasso
from sklearn.metrics import mean_squared_error
from sklearn.model_selection import KFold, cross_val_score
from sklearn.preprocessing import RobustScaler
from sklearn.pipeline import make_pipeline
from trainer.data_loader_and_cleanser import DataLoader

import xgboost as xgb
import lightgbm as lgb


class StackingAveragedModels(BaseEstimator, RegressorMixin, TransformerMixin):
    def __init__(self, base_models, meta_model, n_folds=5):
        self.base_models = base_models
        self.meta_model = meta_model
        self.n_folds = n_folds

    def fit(self, X, y):
        self.base_models_ = [list() for x in self.base_models]
        self.meta_model_ = clone(self.meta_model)
        kfold = KFold(n_splits=self.n_folds, shuffle=True)

        out_of_fold_predictions = np.zeros((X.shape[0], len(self.base_models)))
        for i, clf in enumerate(self.base_models):
            for train_index, holdout_index in kfold.split(X, y):
                instance = clone(clf)
                self.base_models_[i].append(instance)
                instance.fit(X[train_index], y[train_index])
                y_pred = instance.predict(X[holdout_index])
                out_of_fold_predictions[holdout_index, i] = y_pred

        self.meta_model_.fit(out_of_fold_predictions, y)
        return self

    def predict(self, X):
        meta_features = np.column_stack([
            np.column_stack([model.predict(X) for model in base_models]).mean(axis=1)
            for base_models in self.base_models_ ])
        return self.meta_model.predict(meta_features)


#stacked_averaged_models = StackingAveragedModels(base_models=(ENet, GBoost, KRR),
#                                                 meta_model=lasso)
#score = rmsle_cv(stacked_averaged_models)
#print("Stacking Averaged models score: {:.4f} ({:.4f})".format(score.mean(), score.std()))


class AveragingModels(BaseEstimator, RegressorMixin, TransformerMixin):
    def __init__(self, models):
        self.models = models

    def fit(self, X, y):
        self.models_ = [clone(x) for x in self.models]

        for model in self.models_:
            model.fit(X, y)
        return self

    def predict(self, X):
        predictions = np.column_stack([
            model.predict(X) for model in self.models_
        ])
        return np.mean(predictions, axis=1)


class ModelTrainer:

    def __init__(self, category):
        self.data_loader = DataLoader()
        df, y_df = self.data_loader.prepare_train_data(category)
        self.train = df
        self.y_train = y_df

    def get_hyperparams(self):
        n_folds = 5
        return n_folds

    def rmsle_cv(self, model):
        n_folds = self.get_hyperparams()
        kf = KFold(n_folds, shuffle=True, random_state=42).get_n_splits(self.train.values)
        rmse = np.sqrt(-cross_val_score(model, self.train.values, self.y_train, scoring="neg_mean_squared_error", cv=kf))
        return rmse

    def prepare_pipelines(self):
        lasso = make_pipeline(RobustScaler(), Lasso(alpha=0.0005, random_state=1))
        ENet = make_pipeline(RobustScaler(), ElasticNet(alpha=0.0005, l1_ratio=0.9, random_state=3))
        KRR = make_pipeline(RobustScaler(), KernelRidge(alpha=0.6, kernel="polynomial", degree=2, coef0=2.5))
        GBoost = GradientBoostingRegressor(n_estimators=3000, learning_rate=0.05,
                                           max_depth=4, max_features="sqrt",
                                           min_samples_leaf=15, min_samples_split=10,
                                           loss="huber", random_state=5)
        model_xgb = xgb.XGBRegressor(colsample_bytree=0.2, gamma=0.0,
                                     learning_rate=0.05, max_depth=6,
                                     min_child_weight=2, n_estimators=7200,
                                     reg_alpha=0.9, reg_lambda=0.6,
                                     subsample=0.2, seed=42, silent=True,
                                     random_state=7)
        model_lgb = lgb.LGBMRegressor(objective="regression", num_leaves=5,
                                      learning_rate=0.05, n_estimators=720,
                                      max_bin=55, bagging_fraction=0.8,
                                      bagging_freq=5, feature_fraction=0.2319,
                                      feature_fraction_seed=9, bagging_seed=9,
                                      min_data_in_leaf=6, min_sum_hessian_in_leaf=11)

        score = self.rmsle_cv(lasso)
        print("\nLasso score: {:.4f} ({:.4f})\n".format(score.mean(), score.std()))
        score = self.rmsle_cv(ENet)
        print("ElasticNet score: {:.4f} ({:.4f})\n".format(score.mean(), score.std()))
        score = self.rmsle_cv(KRR)
        print("Kernel Ridge score: {:.4f} ({:.4f})\n".format(score.mean(), score.std()))
        score = self.rmsle_cv(GBoost)
        print("Gradient Boosting score: {:.4f} ({:.4f})\n".format(score.mean(), score.std()))
        score = self.rmsle_cv(model_xgb)
        print("Xgboost score: {:.4f} ({:.4f})\n".format(score.mean(), score.std()))
        score = self.rmsle_cv(model_lgb)
        print("LGBM score: {:.4f} ({:.4f})\n".format(score.mean(), score.std()))

        averaged_models = AveragingModels(models=(ENet, GBoost, KRR, lasso))
        score = self.rmsle_cv(averaged_models)
        print("Averaged base models score: {:.4f} ({:.4f})\n".format(score.mean(), score.std()))

        return lasso, ENet, KRR, GBoost, model_xgb, model_lgb, averaged_models

    def rmsle(self, y, y_pred):
        return np.sqrt(mean_squared_error(y, y_pred))

    def model_fitting(self):
        lasso, ENet, KRR, GBoost, model_xgb, model_lgb, averaged_models = self.prepare_pipelines()

        averaged_models.fit(self.train.values, self.y_train)
        dump(averaged_models, "averaged_models.pkl")
        averaged_models_pkl = load("averaged_models.pkl")
        averaged_train_pred = averaged_models_pkl.predict(self.train.values)
#averaged_pred = averaged_models_pkl.predict(df_msft.values)
        print(self.rmsle(self.y_train, averaged_train_pred))

        model_xgb.fit(self.train, self.y_train)
        dump(model_xgb, "model_xgb.pkl")
        model_xgb_pkl = load("model_xgb.pkl")
        xgb_train_pred = model_xgb_pkl.predict(self.train)
#xgb_pred = model_xgb_pkl.predict(df_msft)
        print(self.rmsle(self.y_train, xgb_train_pred))

        model_lgb.fit(self.train, self.y_train)
        dump(model_lgb, "model_lgb.pkl")
        model_lgb_pkl = load("model_lgb.pkl")
        lgb_train_pred = model_lgb_pkl.predict(self.train)
#lgb_pred = model_lgb_pkl.predict(df_msft.values)
        print(self.rmsle(self.y_train, lgb_train_pred))

        print("RMSLE score on train data:")
        print(self.rmsle(self.y_train, averaged_train_pred*0.70 +
                xgb_train_pred * 0.10 + lgb_train_pred * 0.20))
        return averaged_train_pred, xgb_train_pred, lgb_train_pred

    def calculate_ensemble_weights(self):
        Averaged = 1/36.2940
        XGBoost = 1/159.6667
        LGBM = 1/170.8728
        Sum = Averaged + XGBoost + LGBM
        Averaged = Averaged/Sum
        XGBoost = XGBoost/Sum
        LGBM = LGBM/Sum
        print(Averaged, XGBoost, LGBM)
        averaged_train_pred, xgb_train_pred, lgb_train_pred = self.model_fitting()

        print("RMSLE score on train data:")
        print(self.rmsle(self.y_train, averaged_train_pred * Averaged +
                    xgb_train_pred * XGBoost +
                    lgb_train_pred * LGBM))

#ensemble = averaged_pred * Averaged + xgb_pred * XGBoost + lgb_pred * LGBM
#df_msft["PercentChangeInRoe_pred"] = ensemble
#df_msft_entire = df_msft_entire.set_index("PreviousRoe").join(df_msft.set_index("PreviousRoe"))
#print(df_msft_entire.head())
