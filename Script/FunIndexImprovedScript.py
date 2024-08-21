import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os

metricList = ["FunIndex"]  # , "AvgVisits", "AvgServiceTime", "AvgQueueTime"]
fileIdx = [1, 2]


def plotFunIdxChart() :
    for idx in fileIdx:
        try:
            dataFrame = pd.read_csv(
                "./Out/Data/Fun/Improved/FunIndex_" + str(idx) + ".csv"
            )
            plotFunIndexLambdaVariationChart(dataFrame)
        except:
            pass

def plotFunIndexLambdaVariationChart(dataFrame: pd.DataFrame):
    groupedDataFrame = dataFrame.groupby(by=["Priority"])

    for smallPerc in dataFrame["SmallSeatsPercentage"].unique():
        plt.figure(figsize=(10, 5))
        for name, group in groupedDataFrame:
            lambdaSeries = group[group["SmallSeatsPercentage"] == smallPerc][
                "PoissonParam"
            ]
            data = group[group["SmallSeatsPercentage"] == smallPerc]["FunIndex"]
            interval = group[group["SmallSeatsPercentage"] == smallPerc]["ConfInterval"]

            plt.plot(lambdaSeries, np.log10(data), label=name[0], marker="o")
            plt.fill_between(
                lambdaSeries,
                np.log10(data - interval),
                np.log10(data + interval),
                alpha=0.2,
            )

        plt.title("Small Percentage Seats = " + str(smallPerc))
        plt.ylabel("Log_10 FunIndex")
        plt.xlabel("Lambda Values")

        plt.xticks(lambdaSeries)
        plt.yticks(np.arange(-1.5, 0.6, 0.25))

        plt.tight_layout()
        plt.savefig("./Out/Charts/Fun/Improved/SmallPerc_" + str(smallPerc) + ".png")
        plt.clf()


def priorityQueueTimeChart():
    plt.figure(figsize=(10, 5))
    for idx in fileIdx:
        try:
            dataFrame: pd.DataFrame = pd.read_csv(
                "./Out/Data/Fun/Improved/PriorityQueueTime_" + str(idx) + ".csv"
            )
            plotQueueTimeChart(dataFrame, idx)
        except:
            pass


def plotQueueTimeChart(dataFrame: pd.DataFrame, idx: int):
    groupedDataFrame = dataFrame.groupby(["CenterName"])
    for smallPercSeats in dataFrame["SmallSeatsPercentage"].unique():
        for name, group in groupedDataFrame:
            for priority in dataFrame["Priority"].unique():
                data = group[(group["Priority"] == priority) & (group["SmallSeatsPercentage"] == smallPercSeats)]
                plt.plot(
                    data["PoissonParam"],
                    data["AvgQueueTime"],
                    label=f"{priority}",
                    marker="o",
                )

                plt.fill_between(
                    data["PoissonParam"],
                    data["AvgQueueTime"] - data["Interval"],
                    data["AvgQueueTime"] + data["Interval"],
                    alpha=0.2,
                )

            plt.xticks(ticks=data["PoissonParam"])
            plt.xlabel(xlabel="Lambda Values")
            plt.ylabel(ylabel="AvgQueueTime")

            plt.axhline(y=30, color="black", linestyle="--", label="QoS")

            plt.title(label=name[0] + " - Avg E[Tq]")

            plt.tight_layout()
            plt.legend()
            plt.savefig(
                "./Out/Charts/Fun/Improved/Small_"
                + str(idx)
                + "/"
                + "PriorityQueueTime_"
                + name[0]
                + ".png"
            )
            plt.clf()


if __name__ == "__main__":
    for idx in fileIdx :
        os.makedirs("./Out/Charts/Fun/Improved/Small_" + str(idx), exist_ok=True)
    os.makedirs("./Out/Charts/Fun/Improved/", exist_ok=True)
    plotFunIdxChart()
    priorityQueueTimeChart()
