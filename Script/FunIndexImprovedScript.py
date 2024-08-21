import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os

metricList = ["FunIndex"]  # , "AvgVisits", "AvgServiceTime", "AvgQueueTime"]
fileIdx = [1, 2]


def plotFunIndexLambdaVariationChart(dataFrame: pd.DataFrame):
    groupedDataFrame = dataFrame.groupby(by=["Priority", "SmallSeatsPercentage"])

    plt.figure(figsize=(16, 9))
    for name, group in groupedDataFrame:
        lambdaSeries = group["PoissonParam"]
        data = group["FunIndex"]
        interval = group["Interval"]

        plt.plot(lambdaSeries, np.log10(data), label=name)
        plt.fill_between(
            lambdaSeries, 
            np.log10(data - interval), 
            np.log10(data + interval),
            alpha = 0.2
        )

    plt.tight_layout()
    plt.legend()

    plt.xlabel("Lambda Values")
    plt.ylabel("Log_10 FunIndex")

    plt.show()


if __name__ == "__main__":
    for idx in fileIdx:
        try :
            dataFrame = pd.read_csv("./Out/Data/Fun/FunIndex_" + str(idx) + ".csv")
            plotFunIndexLambdaVariationChart(dataFrame)
        except :
            pass
