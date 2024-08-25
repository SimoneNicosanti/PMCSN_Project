import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
import os

metricList = ["FunIndex"]  # , "AvgVisits", "AvgServiceTime", "AvgQueueTime"]
fileIdx = [1, 2]


def plotFunIdxChart():
    for idx in fileIdx:
            dataFrame = pd.read_csv(
                "./Out/Data/Fun/Improved/FunIndex_" + str(idx) + ".csv"
            )
            plotFunIndexLambdaVariationChart(idx, dataFrame)


def plotFunIndexLambdaVariationChart(idx: int, dataFrame: pd.DataFrame):
    groupedDataFrame = dataFrame.groupby(by=["Priority"])

    for smallPerc in dataFrame["SmallSeatsPercentage"].unique():
        fig, axes = plt.subplots(nrows = 1, ncols = 2, figsize=(12.5, 5))
        for name, group in groupedDataFrame:
            lambdaSeries = group[group["SmallSeatsPercentage"] == smallPerc][
                "PoissonParam"
            ]
            data = group[group["SmallSeatsPercentage"] == smallPerc]["FunIndex"]
            interval = group[group["SmallSeatsPercentage"] == smallPerc]["ConfInterval"]

            axes[0].plot(lambdaSeries, data, label=name[0], marker="o")
            axes[0].fill_between(
                lambdaSeries,
                data - interval,
                data + interval,
                alpha=0.2,
            )

            if (name[0] != "PRIORITY") :
                axes[1].plot(lambdaSeries, data, label=name[0], marker="o")
                axes[1].fill_between(
                    lambdaSeries,
                    data - interval,
                    data + interval,
                    alpha=0.2,
                )
        for i in range(0, 2) :
            axes[i].set_title("Small Percentage Seats = " + str(smallPerc))
            axes[i].set_ylabel("FunIndex")
            axes[i].set_xlabel("Lambda Values")

            axes[i].set_xticks(lambdaSeries)
            axes[i].legend()

            axes[i].grid()

            if (i == 1) :
                #axes[i].set_yticks(np.arange(0.0, 0.8, 0.05))
                pass
        #plt.yticks(np.arange(-1.5, 0.6, 0.25))

        plt.tight_layout()
        
        plt.savefig(
            "./Out/Charts/Fun/Improved/Small_"
            + str(idx)
            + "/FunIndex_SmallPerc_"
            + str(smallPerc)
            + ".png"
        )
        plt.clf()


def priorityQueueTimeChart():
    for idx in fileIdx:
        try:
            dataFrame: pd.DataFrame = pd.read_csv(
                "./Out/Data/Fun/Improved/PriorityQueueTime_" + str(idx) + ".csv"
            )
            plotQueueTimeChart(dataFrame, idx)
        except:
            pass


# SmallSeatsPercentage,PoissonParam,CenterName,Priority,AvgQueueTime,ConfInterval
def plotQueueTimeChart(dataFrame: pd.DataFrame, idx: int):
    groupedDataFrame = dataFrame.groupby(["CenterName"])
    for smallPercSeats in dataFrame["SmallSeatsPercentage"].unique():
        for name, group in groupedDataFrame:
            plt.figure(figsize=(10, 5))
            for priority in dataFrame["Priority"].unique():
                data = group[
                    (group["Priority"] == priority)
                    & (group["SmallSeatsPercentage"] == smallPercSeats)
                ]
                plt.plot(
                    data["PoissonParam"],
                    data["AvgQueueTime"],
                    label=f"{priority}",
                    marker="o",
                )

                plt.fill_between(
                    data["PoissonParam"],
                    data["AvgQueueTime"] - data["ConfInterval"],
                    data["AvgQueueTime"] + data["ConfInterval"],
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
                + "/PriorityQueueTime_"
                + name[0]
                + ".png"
            )
            plt.clf()


if __name__ == "__main__":
    os.makedirs("./Out/Charts/Fun/Improved/", exist_ok=True)
    for idx in fileIdx:
        os.makedirs("./Out/Charts/Fun/Improved/Small_" + str(idx), exist_ok=True)
    plotFunIdxChart()
    priorityQueueTimeChart()
