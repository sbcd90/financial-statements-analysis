# financial-statements-analysis

- The goal of this project is to design a software system which can predict a company's Return on Equity(ROE) score for the upcoming quarter quarter/year.  

- Return on equity (ROE) is a measure of financial performance calculated by dividing net income by shareholders' equity. Because shareholders' equity is equal to a company’s assets minus its debt, ROE could be thought of as the return on net assets.

## Design

### Data preparation

![data_preparation.jpg](https://github.com/sbcd90/financial-statements-analysis/blob/master/data_preparation.JPG)

### Training

![Training.JPG](https://github.com/sbcd90/financial-statements-analysis/blob/master/Training.JPG)

### Features used

- PreviousRoe
- PercentOfPositiveWords
- PercentOfNegativeWords
- PercentOfLitiguousWords
- PercentOfConstrainingWords
- PercentOfStrongModalWords
- PercentOfWeakModalWords
- PercentOfUncertaintyWords
- (Label) PercentChangeInRoe

### Master data

- Ticker
- CompanyName
- CompanyCategory
- Date
- TtmNetIncome
- ShareholdersEquity

### Sample data

![data.JPG](https://github.com/sbcd90/financial-statements-analysis/blob/master/Data.JPG)

## Run locally

- [Data Preparation](https://github.com/sbcd90/financial-statements-analysis/tree/master/data-preparation) part is written in Java. It is a mvn project.

```
mvn clean install
```
- Run [MainApplication.java](https://github.com/sbcd90/financial-statements-analysis/blob/master/data-preparation/src/main/java/com/edgar/analyzer/MainApplication.java)

- [Training](https://github.com/sbcd90/financial-statements-analysis/tree/master/model-trainer) is written in python.

- Run [Training](https://github.com/sbcd90/financial-statements-analysis/blob/master/model-trainer/trainer/train_app.py) app to train a new model.

- Run [Prediction](https://github.com/sbcd90/financial-statements-analysis/blob/master/model-trainer/trainer/predict_app.py) app to start serving predictions.

## Baseline comparison

- Baseline comparison for MSFT, GOOGL, ORCL & TWTR available [here](https://github.com/sbcd90/financial-statements-analysis/blob/master/model-trainer/Baseline_Comparisons.ipynb).

## References

- [Common Annual Algorithm](https://airccj.org/CSCP/vol7/csit76615.pdf)
- [loughran mcdonald financial dictionary](https://sraf.nd.edu/textual-analysis/resources/)
- [CBOW based Financial Keyword Expansion](https://www.aclweb.org/anthology/D14-1152)
- [Regression techniques](https://www.kaggle.com/c/house-prices-advanced-regression-techniques/kernels)
