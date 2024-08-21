import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os

metricList = ["FunIndex"]  # , "AvgVisits", "AvgServiceTime", "AvgQueueTime"]
fileIdx = [1, 2]


def plotRho():

    dataFrame = pd.read_csv("./Out/Data/Rho/MultipliedRhos" + ".csv")
    plotRhoVariationChart(dataFrame)


def plotRhoVariationChart(dataFrame: pd.DataFrame):
    groupedDataFrame = dataFrame.groupby(by=["CenterName"])

    for smallGroupSize in dataFrame["SmallGroupSize"].unique():
        plt.figure(figsize=(10, 5))
        for name, group in groupedDataFrame:
            data = group[group["SmallGroupSize"] == smallGroupSize]
            plt.plot(
                data["SmallPercentageSize"],
                data["Rho"],
                label=name[0],
                marker="o",
            )

        plt.title("Rho Variation - Small Group Size: " + str(smallGroupSize))
        plt.ylabel("Rho")
        plt.xlabel("Small Percentage Size")
        plt.xticks(data["SmallPercentageSize"])
        plt.tight_layout()
        plt.legend()

        plt.savefig("./Out/Charts/Rho/RhoChart_" + str(smallGroupSize) + ".png")
        plt.clf()


if __name__ == "__main__":
    os.makedirs("./Out/Charts/Rho/", exist_ok=True)

    plotRho()
